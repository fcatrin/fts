package fts.android.fileselector;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fts.android.AndroidWindow;
import fts.android.R;
import fts.core.Callback;
import fts.core.CoreUtils;
import fts.core.SimpleBackgroundTask;
import fts.core.UserVisibleException;
import fts.utils.dialogs.DialogContext;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileChooserConfig;
import fts.vfile.VirtualFile;

public class FilesPanel {
	
	protected static final String LOGTAG = null;
	final ListView lv;
	private boolean busy;
	private final TextView txtPanelStatus1;
	private final TextView txtPanelStatus2;
	private final Activity activity;
	private final DialogContext context;
	private final TextView txtStorage;
	private final ImageView iconStorage;
	private VirtualFile currentStorage;
	private VirtualFile currentFolder;
	protected boolean focused = false;
	private VirtualFile currentParent;
	private final VirtualFile sysRoot;
	private final FileChooserConfig config;

	@SuppressWarnings("unused")
	static class FolderInfo {
		List<VirtualFile> list;
		long freeSpace;
		long totalSpace;
		public int nElements;
		String error;
		Exception e;
	}
	
	public FilesPanel(DialogContext context, VirtualFile sysRoot, ListView listView, TextView txtStorage, ImageView iconStorage, TextView txtPanelStatus1, TextView txtPanelStatus2,
					  final Callback<VirtualFile> openCallback, final FileChooserConfig config) {
		this.context = context;
		this.activity = (AndroidWindow)context;
		this.lv = listView;
		this.txtStorage = txtStorage;
		this.iconStorage = iconStorage;
		this.txtPanelStatus1 = txtPanelStatus1;
		this.txtPanelStatus2 = txtPanelStatus2;
		this.sysRoot = sysRoot;
		this.config = config;
		this.currentFolder = config.initialDir;
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (busy) return;

				VirtualFile vf = (VirtualFile)lv.getAdapter().getItem(position);
				Log.d(LOGTAG, "FileChooser go to " + vf);

				if (vf.getName().equals("_select_")) {
					openCallback.onResult(currentFolder);
					return;
				}

				if (vf.isDirectory()) {
					browse(vf);
				} else {
					if (!config.isDirOnly) {
						openCallback.onResult(vf);
					}
				}
			}
		});
		
		lv.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				focused = hasFocus;
			}
		});
		
		
	}
	
	public VirtualFile getCurrentStorage() {
		return currentStorage;
	}
	
	public VirtualFile getCurrentFolder() {
		return currentFolder;
	}

	
	public void refresh() {
		lv.setAdapter(null);
		if (config.browseCallback!=null) config.browseCallback.onResult(currentFolder);
		browse(currentFolder, lv.getSelectedItemPosition());
	}
	
	public void browse(final VirtualFile browseDir) {
		browse(browseDir, 0);
	}
	
	public void browse(final VirtualFile browseDir, final int selectItem) {
		setBusy(true);
		final VirtualFile dir = browseDir == null ? sysRoot : browseDir;
		currentFolder = dir;
		
		String path = dir.getPath();
		if (path.equals("/")) {
			path = "";
		} else {
			path = " - " + path;
		}
		
		currentStorage = dir.getStorage();
		if (currentStorage == null) currentStorage = sysRoot;
		
		txtStorage.setText(currentStorage.getFriendlyName() + path);
		iconStorage.setImageResource(currentStorage.getIconResourceId());

		List<VirtualFile> loadingList = new ArrayList<VirtualFile>();
		VirtualFile loadingFileDummy = new VirtualFile(VirtualFile.ROOT_LOCAL + VirtualFile.TYPE_SEPARATOR);
		loadingFileDummy.setIsLoading(true);
		loadingList.add(loadingFileDummy);
		FileListAdapter adapter = new FileListAdapter(loadingList);
		lv.setAdapter(adapter);

		SimpleBackgroundTask task = new SimpleBackgroundTask() {
			FolderInfo folderInfo;
			@Override
			public void onBackgroundTask() throws Exception {
				folderInfo = loadDir(dir);
			}

			@Override
			public void onSuccess() {
				if (config.browseCallback!=null) config.browseCallback.onResult(dir);

				Log.d("FILES", "size " + folderInfo.list.size());
				long size = 0;
				for(VirtualFile file : folderInfo.list) {
					Log.d("FILES", "file " + file);
					size += file.getSize();
				}

				if (currentFolder.getHandler().hasElements()) {
					int nElements = folderInfo.nElements;

					String strElements = nElements == 0 ? activity.getString(R.string.folder_elements_0) :
							(nElements == 1 ?
									activity.getString(R.string.folder_elements_1) :
									activity.getString(R.string.folder_elements_n).replace("{n}",String.valueOf(nElements)));

					if (size>0) {
						strElements += " / " + CoreUtils.size2human(size);
					}

					txtPanelStatus1.setText(strElements);
				} else {
					txtPanelStatus1.setText("");
				}

				long free = folderInfo.freeSpace;
				long total = folderInfo.totalSpace;

				if (total>0) {
					String freeHuman  = CoreUtils.size2human(free);
					String totalHuman = CoreUtils.size2human(total);
					int percentFree = (int)(free * 100.0f / total);
					txtPanelStatus2.setText(
							activity.getString(R.string.folder_free)
									.replace("{free}", freeHuman)
									.replace("{total}", totalHuman)
									.replace("{percent}", String.valueOf(percentFree)));
				} else {
					txtPanelStatus2.setText("");
				}

				FileListAdapter adapter = new FileListAdapter(folderInfo.list);
				lv.setAdapter(adapter);
				setBusy(false);

				int itemToSelect = selectItem;
				if (itemToSelect >= folderInfo.list.size()) {
					itemToSelect = folderInfo.list.size() - 1;
				}
				if (itemToSelect>=0) {
					lv.setSelection(itemToSelect);
				}

				lv.requestFocus();

				if (folderInfo.error != null) {
					DialogUtils.message(context, folderInfo.error);
				} else if (folderInfo.e != null) {
					DialogUtils.message(context, folderInfo.e.toString());
				}
			}
		};
		task.execute();
	}

	
	private FolderInfo loadDir(VirtualFile dir) {
		Log.d(LOGTAG, "Loading dir for " + dir);
		List<VirtualFile> list = new ArrayList<VirtualFile>();

		VirtualFile parent = dir.getParent();
		if (parent!=null) {
			parent.setIsParent(true);
			parent.setFriendlyName(activity.getString(R.string.folder_parent));
			list.add(parent);
		}

		currentParent = parent;

		if ((config.isDirOnly || config.isDirOptional) && !dir.isStorage() && !dir.getPath().equals("/")) {
			VirtualFile vFileSelectFolder = new VirtualFile(dir, "_select_");
			vFileSelectFolder.setFriendlyName(activity.getString(R.string.folder_select));
			vFileSelectFolder.setIconResourceId(R.drawable.ic_label_outline_white_36dp);
			vFileSelectFolder.setIsDirectory(true);
			list.add(vFileSelectFolder);
		}
		
		FolderInfo folderInfo = new FolderInfo();
		try {
			List<VirtualFile> tmpList = dir.list();
			
			if (dir.canSort()) {
				Collections.sort(tmpList, new Comparator<VirtualFile>() {

					@Override
					public int compare(VirtualFile lhs, VirtualFile rhs) {
						if (lhs.isDirectory() && !rhs.isDirectory()) {
							return -1;
						}
						if (!lhs.isDirectory() && rhs.isDirectory()) {
							return 1;
						}
						return lhs.getName().toLowerCase(Locale.US).compareTo(rhs.getName().toLowerCase(Locale.US));
					}
				});
			}

			list.addAll(tmpList);
			folderInfo.freeSpace = dir.getFreeSpace();
			folderInfo.totalSpace = dir.getTotalSpace();
			folderInfo.nElements = tmpList.size();
		} catch (Exception e) {
			if (e instanceof UserVisibleException) {
				folderInfo.error = e.getMessage();
			} else {
				folderInfo.e = e;
			}
		}
		
		folderInfo.list = list;
		return folderInfo;
	}
	
	private void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void requestFocus() {
		lv.requestFocus();
	}

	public boolean isFocused() {
		return focused;
	}

	public boolean onBackPressed() {
		if (busy) return false;
		
		if (lv.getSelectedItemPosition()>0) {
			lv.setSelection(0);
			return true;
		}
		
		if (currentParent!=null) {
			browse(currentParent);
			return true;
		}
		
		return false;
	}
}

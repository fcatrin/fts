package fts.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fts.core.Callback;
import fts.core.CoreUtils;
import fts.core.CoreUtils.Compact;
import fts.core.Log;
import fts.core.SimpleBackgroundTask;
import fts.ui.Window;
import fts.ui.widgets.TextWidget;
import fts.utils.dialogs.DialogUtils;
import fts.utils.dialogs.FileChooserConfig;
import fts.utils.dialogs.FolderInfo;
import fts.utils.dialogs.OnItemSelectedListener;
import fts.vfile.VirtualFile;

public class FileListPanel {
	private static final String LOGTAG = FileListPanel.class.getSimpleName();

	private Window nativeWindow;
	private FileListWidget list;
	private TextWidget title;
	private VirtualFile sysRoot;
	private VirtualFile currentFolder;
	private FileChooserConfig config;

	private boolean busy;
	private VirtualFile currentStorage;
	private VirtualFile currentParent;
	protected boolean focused = false;

	public FileListPanel(Window nativeWindow, TextWidget title, FileListWidget list, VirtualFile sysRoot,
						 FileChooserConfig config,
						 final Callback<VirtualFile> openCallback) {
		this.title = title;
		this.list = list;
		this.sysRoot = sysRoot;
		this.config = config;
		this.currentFolder = config.initialDir;
		this.nativeWindow = nativeWindow;
		
		list.setOnItemSelectedListener(new OnItemSelectedListener<VirtualFile>() {
			
			@Override
			public void onItemSelected(VirtualFile item, int index) {
				if (busy) return;
				
				// Log.d(LOGTAG, "FileChooser go to " + item);
				
				if (item.getName().equals("_select_")) {
					openCallback.onResult(currentFolder);
					return;
				}
				
				if (item.isDirectory()) {
					browse(item);
				} else {
					if (!FileListPanel.this.config.isDirOnly) {
						openCallback.onResult(item);
					}
				}
			}
		});
		
		/*
		list.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				focused = hasFocus;
			}
		});
		*/
	}
	
	
	public VirtualFile getCurrentStorage() {
		return currentStorage;
	}
	
	public VirtualFile getCurrentFolder() {
		return currentFolder;
	}

	
	public void refresh() {
		list.setAdapter(null);
		if (config.browseCallback!=null) config.browseCallback.onResult(currentFolder);
		browse(currentFolder);
	}
	
	public void browse(final VirtualFile browseDir) {
		browse(browseDir, 0);
	}
	
	public void browse(final VirtualFile browseDir, final int selectItem) {
		Log.d(LOGTAG, "browse dir " + browseDir);
		
		setBusy(true);
		final VirtualFile dir = browseDir == null ? sysRoot : browseDir;
		currentFolder = dir;
		
		String path = dir.getPath();
		if (path.equals("/")) {
			path = "";
		} else {
			path = CoreUtils.compact(path, Compact.Middle, 32);
		}
		
		currentStorage = dir.getStorage();
		if (currentStorage == null) currentStorage = sysRoot;
		
		String storageName = currentStorage.getFriendlyName();
		if (CoreUtils.isEmptyString(storageName)) storageName = currentStorage.getName();
		
		title.setText(storageName + " - " + path);

		List<VirtualFile> loadingList = new ArrayList<VirtualFile>();
		VirtualFile loadingFileDummy = new VirtualFile(new File("/"));
		loadingFileDummy.setIsLoading(true);
		loadingList.add(loadingFileDummy);
		FileListAdapter adapter = new FileListAdapter(nativeWindow, loadingList);
		list.setAdapter(adapter);
		
		SimpleBackgroundTask task = new SimpleBackgroundTask() {
			FolderInfo folderInfo;

			@Override
			public void onBackgroundTask() throws Exception {
				folderInfo = loadDir(dir);
			}

			@Override
			public void onSuccess() {
				if (config.browseCallback!=null) config.browseCallback.onResult(dir);
				
                Log.d("LIST FILES", "size " + folderInfo.list);
                
				FileListAdapter adapter = new FileListAdapter(nativeWindow, folderInfo.list);
				list.setAdapter(adapter);
				setBusy(false);
				
				int itemToSelect = selectItem;
				if (itemToSelect >= folderInfo.list.size()) {
					itemToSelect = folderInfo.list.size() - 1;
				}
				if (itemToSelect>=0) {
					// list.setSelection(itemToSelect);
				}
				
				list.requestFocus();
				
				if (folderInfo.error != null) {
					DialogUtils.message(nativeWindow, folderInfo.error);
				} else if (folderInfo.e != null) {
					DialogUtils.message(nativeWindow, folderInfo.e.toString());
					folderInfo.e.printStackTrace();
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
			parent.setFriendlyName("Go to parent folder");
			list.add(parent);
		}

		currentParent = parent;

		if ((config.isDirOnly || config.isDirOptional) && !dir.isStorage() && !dir.getPath().equals("/")) {
			VirtualFile vFileSelectFolder = new VirtualFile(dir, "_select_");
			vFileSelectFolder.setFriendlyName("Select this folder");
			// vFileSelectFolder.setIconResourceId(R.drawable.ic_label_outline_white_36dp);
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
			
			for(int i=0; i<tmpList.size(); i++) {
				VirtualFile file = tmpList.get(i);
				//int icon = file.isDirectory() ? R.drawable.ic_folder_white_36dp : R.drawable.ic_insert_drive_file_grey600_36dp;
				//icon = SystemRootHandler.isMount(file) ? R.drawable.ic_sd_storage_white_36dp : icon;
				//icon = file.canRead() ? icon : R.drawable.ic_block_grey600_36dp;
				//file.setIconResourceId(icon);
				list.add(file);
			}
			folderInfo.nElements = tmpList.size();
		} catch (Exception e) {
			folderInfo.e = e;
			/*
			if (e instanceof UserVisibleException) {
				folderInfo.error = e.getMessage();
			} else {
				folderInfo.e = e;
			}
			*/
		}
		
		folderInfo.list = list;
		return folderInfo;
	}
	
	private void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void requestFocus() {
		list.requestFocus();
	}

	public boolean isFocused() {
		return focused;
	}

	public boolean onBackPressed() {
		if (busy) return false;
		
		/*
		if (list.getSelectedItemPosition()>0) {
			list.setSelection(0);
			return true;
		}
		*/
		
		if (currentParent!=null) {
			browse(currentParent);
			return true;
		}
		
		return false;
	}

}

package fts.android.fileselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import fts.android.AndroidFonts;
import fts.android.R;
import fts.android.TintableImageView;
import fts.core.Utils;
import fts.vfile.VirtualFile;

import java.util.List;

public class FileListAdapter extends BaseAdapter {
	public static String fontName;

	private static ViewCustomizer viewCustomizer;
	final List<VirtualFile>files;

	public FileListAdapter(List<VirtualFile> files) {
		this.files = files;
	}
	
	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext()
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View fileView = convertView!=null?convertView:inflater.inflate(R.layout.listitem_file, parent, false);

		TextView txtName = fileView.findViewById(R.id.listFileName);
		TextView txtSize = fileView.findViewById(R.id.listFileSize);
		
		if (fontName!=null) {
			AndroidFonts.setViewFont(txtName, fontName);
			AndroidFonts.setViewFont(txtSize, fontName);
		}
		
		TintableImageView icon = fileView.findViewById(R.id.listFileIcon);
		ProgressBar iconLoading = fileView.findViewById(R.id.listFileLoading);

		VirtualFile vf = (VirtualFile)getItem(position);
		if (vf.isLoading()) {
			iconLoading.setVisibility(View.VISIBLE);
			icon.setVisibility(View.GONE);
			txtName.setText(fileView.getContext().getString(R.string.folder_loading));
		} else {
			boolean isParent = vf.isParent();
	
			String name = vf.getFriendlyName();
			if (isParent) {
				if (name == null || name.isEmpty())	name = "..";
			} else if (name == null) {
				name = vf.getName();
			}
			txtName.setText(name);
			
			if ((vf.isDirectory() && vf.getSize() == 0) || vf.getSize() < 0) {
				txtSize.setVisibility(View.GONE);
			} else {
				txtSize.setText(Utils.size2human(vf.getSize()));
				txtSize.setVisibility(View.VISIBLE);
			}
			
			icon.setImageResource(isParent ? R.drawable.ic_navigate_before_white_36dp : vf.getIconResourceId());
			icon.setVisibility(View.VISIBLE);
			iconLoading.setVisibility(View.GONE);
		}
		
		if (viewCustomizer!=null) {
			viewCustomizer.customize(icon, iconLoading, txtName, txtSize);
		}
		
		return fileView;
	}
	
	public static void setViewCustomizer(ViewCustomizer viewCustomizer) {
		FileListAdapter.viewCustomizer = viewCustomizer;
	}

	public interface ViewCustomizer {
		void customize(TintableImageView icon, ProgressBar iconLoading, TextView txtName, TextView txtSize);
	}
}

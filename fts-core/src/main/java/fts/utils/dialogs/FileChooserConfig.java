package fts.utils.dialogs;

import java.util.List;

import fts.core.Callback;
import fts.vfile.VirtualFile;

public class FileChooserConfig {
    public String title;
    public VirtualFile initialDir;
    public List<String> matchList;
    public Callback<VirtualFile> callback;
    public Callback<VirtualFile> browseCallback;
    public boolean isDirOnly;
    public boolean isDirOptional;
}

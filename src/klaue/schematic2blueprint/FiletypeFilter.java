package klaue.schematic2blueprint;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter for filetypes by extension
 * @author klaue
 */
public class FiletypeFilter extends FileFilter {
	String[] extensions;
	String description;
	
	/**
	 * @param extensions the extensions to allow (without the dot)
	 * @param description the description of this filter
	 */
	public FiletypeFilter(String[] extensions, String description) {
		this.extensions = new String[extensions.length];
		for (int i = 0; i < extensions.length; ++i) {
			this.extensions[i] = "." + extensions[i].toLowerCase();
		}
		this.description = description;
	}
	
	/**
	 * @param extension the extension to allow (without the dot)
	 * @param description the description of this filter
	 */
	public FiletypeFilter(String extension, String description) {
		this.extensions = new String[1];
		this.extensions[0] = "." + extension.toLowerCase();
		this.description = description;
	}
	
	/**
	 * use this ctor for directories only
	 */
	public FiletypeFilter() {
		this.extensions = null;
		this.description = "Directories";
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) return true; // always accept dirs
		if (this.extensions == null) {
			// dir-only mode
			return false;
		}
		String filename = f.getName().toLowerCase();
		for (String extension : this.extensions) {
			if (filename.endsWith(extension)) return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
}
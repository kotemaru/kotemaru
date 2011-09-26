package org.kotemaru.yui;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.yahoo.platform.yui.compressor.YUICompressor;

public class YuiCompressTask extends Task {
	private ArrayList<FileSet> filesets = new ArrayList<FileSet>();

	private String file;

	private boolean verbose = false;
	private String type = null;
	private String charset = null;
	private int column = 0;

	private boolean munge = true;
	private boolean semi = true;
	private boolean optimize = true;

	public FileSet createFileSet() {
		FileSet fs = new FileSet();
		filesets.add(fs);
		return fs;
	}

	
	public void execute() throws BuildException {
		if (file == null) throw new BuildException("Require file attribute.");
		try{
			_execute();
		} catch(Exception e) {
			throw new BuildException(e);
		}
	}
	private void _execute() throws Exception {
		List<String> argList = new ArrayList<String>(32);

		
		opt(argList, "-o", file);
		if (type != null) opt(argList, "--type", type);
		if (charset != null) opt(argList, "--charset", charset);
		if (column > 0) opt(argList, "--line-break", ""+column);
		if (verbose) opt(argList, "--verbose", null);
		if (!munge) opt(argList, "--nomunge", null);
		if (!semi) opt(argList, "--preserve-semi", null);
		if (!optimize) opt(argList, "--disable-optimizations", null);

		for (FileSet fs : filesets) {
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String dir = ds.getBasedir().getPath();
			String[] files = ds.getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				String inFile = dir+"/"+files[i];
				argList.add(inFile);
			}
		}
		log("YUICompressor"+argList);
        YUICompressor.main((String[])argList.toArray(new String[0]));
	}

	private List<String> opt(List<String> list, String a1, String a2) {
		list.add(a1);
		if (a2 != null)list.add(a2);
		return list;
	}
	
	
	
	
	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	public boolean isVerbose() {
		return verbose;
	}


	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public boolean isMunge() {
		return munge;
	}


	public void setMunge(boolean munge) {
		this.munge = munge;
	}


	public boolean isSemi() {
		return semi;
	}


	public void setSemi(boolean semi) {
		this.semi = semi;
	}


	public boolean isOptimize() {
		return optimize;
	}


	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

 
}
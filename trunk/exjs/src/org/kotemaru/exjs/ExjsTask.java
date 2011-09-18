package org.kotemaru.exjs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.kotemaru.util.jsg.BnfParser;

public class ExjsTask extends Task {
	private ArrayList<FileSet> filesets = new ArrayList<FileSet>();

	public FileSet createFileSet() {
		FileSet fs = new FileSet();
		filesets.add(fs);
		return fs;
	}

	public void setDebug(String val) {
		Exjs.isDebug = Boolean.parseBoolean(val);
	}
	
	public void execute() throws BuildException {
		try{
			_execute();
		} catch(Exception e) {
			throw new BuildException(e);
		}
	}
	private void _execute() throws Exception {
		
		BnfParser parser = Exjs.parseBnf("exjs.bnf");
		Transformer trans = Exjs.parseJsg("exjs.jsg");
		
		for (FileSet fs : filesets) {
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String dir = ds.getBasedir().getPath();
			String[] files = ds.getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				String inFile = dir+"/"+files[i];
				if (!inFile.endsWith(".exjs")) continue;
				Exjs.compile(parser, trans, inFile);
			}
		}
	}    
    
}
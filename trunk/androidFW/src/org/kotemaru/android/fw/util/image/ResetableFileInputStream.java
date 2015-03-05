package org.kotemaru.android.fw.util.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;


public class ResetableFileInputStream extends FilterInputStream {
	
	private File mFile;

	protected ResetableFileInputStream(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
		mFile = file;
	}
	
	@Override
	public void reset() {
		try {
			super.in.close();
			super.in = new FileInputStream(mFile);
		} catch (IOException e) {
			throw new Error(e);
		}
	}

}

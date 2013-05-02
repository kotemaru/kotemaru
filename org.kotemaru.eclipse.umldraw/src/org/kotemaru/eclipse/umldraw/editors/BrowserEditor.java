package org.kotemaru.eclipse.umldraw.editors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.EditorPart;


public class BrowserEditor extends TextEditor {

	private static final String ENCODING = "utf-8";
	
	private Browser browser;
	private boolean isDirty = false;
	
	public BrowserEditor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
	}


	@Override
	public void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);
			browser.setJavascriptEnabled(true);			
			browser.addStatusTextListener(new MyStatusTextListener());
			
			URL aboutURL = this.getClass().getResource("editor.html");
			URL url = FileLocator.resolve(aboutURL);
			browser.setUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private class MyStatusTextListener implements StatusTextListener {
		@Override
		public void changed(StatusTextEvent ev) {
			log("status="+ev.text);
			String[] params = ev.text.split(",");
			String method = params[0];
			if ("load".equals(method)) {
				onLoad(params);
			} else if ("change".equals(method)) {
				onChange(params);
			}
		}
	}
/*
	private void onLoad(String[] params) {
		StringBuilder sbuf = new StringBuilder();
		try {
			IFileEditorInput input = (IFileEditorInput)getEditorInput();
			InputStream in = input.getFile().getContents();
			try {
				Reader reader = new InputStreamReader(in, ENCODING);
				int n;
				char[] buff = new char[1024];
				while ((n=reader.read(buff))>=0) {
					sbuf.append(buff,0,n);
				}
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		String data = sbuf.toString();
		//data = data.replaceAll("[']", "\\\'").replaceAll("[\\n]", "\\\n");
		
		browser.execute("Eclipse.setContent('"+data+"')");
	}
*/
	private void onLoad(String[] params) {
		browser.execute("Eclipse.startup()");
		
		IFileEditorInput input = (IFileEditorInput)getEditorInput();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		File workspaceDirectory = workspace.getRoot().getLocation().toFile(); 
		String path = input.getFile().getFullPath().toString().replaceFirst("^/","");
		File file = new File(workspaceDirectory,path);
		
		String url = "file:///"+file.getAbsolutePath().replaceAll("[\\\\]","/");
		browser.execute("Eclipse.setContentUrl('"+url+"')");
	}
	
	private void onChange(String[] params) {
		setDirty(true);
	}
	

	@Override
	public void doSave(IProgressMonitor monitor) {
		String content = (String) browser.evaluate("return Eclipse.getContent();");
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(
				new ByteArrayInputStream(content.getBytes(ENCODING)),
				true,  // keep saving, even if IFile is out of sync with the Workspace
				false, // dont keep history
				monitor); // progress monitor
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void setDirty(boolean b) {
		isDirty = b;
		firePropertyChange(EditorPart.PROP_DIRTY); 
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}


	@Override
	public void dispose() {
		browser.dispose();
		super.dispose();
	}

	@Override
	public void setFocus() {
	}
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	@Override
	public void doSaveAs() {
		// Unsupported
	}
	
	private void log(String msg) {
		System.out.println(msg); // TODO:ちゃんとログ。
	}

}

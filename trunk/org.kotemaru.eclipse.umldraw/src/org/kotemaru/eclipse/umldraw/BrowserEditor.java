package org.kotemaru.eclipse.umldraw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
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
	private int changeHistoryCount = 0;
	private int savedHistoryCount  = 0;
	
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
			
			URL aboutURL = this.getClass().getResource("/webapps/editor.html");
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
			} else if ("syncPreferences".equals(method)) {
				onSyncPreferences(params);
			}
		}

	}

	private void onLoad(String[] params) {
		startup();
		
		IFileEditorInput input = (IFileEditorInput)getEditorInput();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		File workspaceDirectory = workspace.getRoot().getLocation().toFile(); 
		String path = input.getFile().getFullPath().toString().replaceFirst("^/","");
		File file = new File(workspaceDirectory,path);
		
		String url = "file:///"+file.getAbsolutePath().replaceAll("[\\\\]","/");
		browser.execute("Eclipse.setContentUrl('"+url+"')");
	}
	private void startup() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String json = "";
		for (int i=0; i<Preference.ITEMS.length; i++) {
			String key = Preference.ITEMS[i].key;
			if (Preference.ITEMS[i].type == String.class) {
				json += key+":'"+store.getString(key)+"',";
			} else {
				json += key+":"+store.getString(key)+",";
			}
		}
		
		browser.execute("Eclipse.startup({"+json+"})");
	}
	
	private void onSyncPreferences(String[] params) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		for (int i=0; i<Preference.ITEMS.length; i++) {
			String key = Preference.ITEMS[i].key;
			String val = (String) browser.evaluate("return Eclipse.getPreferences('"+key+"')");
			store.setValue(key, val);
		}
		try {
			((IPersistentPreferenceStore)store).save();
		} catch (Exception e) {
			log("Error:"+e);
		}
	}
	private void onChange(String[] params) {
		changeHistoryCount = Integer.valueOf(params[1]);
		firePropertyChange(EditorPart.PROP_DIRTY); 
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
			savedHistoryCount = changeHistoryCount;
			firePropertyChange(EditorPart.PROP_DIRTY); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isDirty() {
		return savedHistoryCount != changeHistoryCount;
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
		System.out.println(msg); // TODO:
	}

}

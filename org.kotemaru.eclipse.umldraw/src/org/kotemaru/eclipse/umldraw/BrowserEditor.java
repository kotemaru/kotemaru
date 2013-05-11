package org.kotemaru.eclipse.umldraw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public class BrowserEditor extends TextEditor {

	private static final String ENCODING = "utf-8";

	private Browser browser;
	private Clipboard clipboard = null;
	private int changeHistoryCount = 0;
	private int savedHistoryCount = 0;

	static class JsAction extends Action {
		private BrowserEditor editor;
		private String script;
		public JsAction(BrowserEditor editor, String script, boolean enable) {
			this.editor = editor;
			this.script = script;
			this.setEnabled(enable);
		}
		public void run() {
			editor.browser.execute(script);
		}
	}
	private JsAction printAction = new JsAction(this, "Eclipse.print()", true);
	private JsAction undoAction = new JsAction(this, "Eclipse.undo()", true);
	private JsAction redoAction = new JsAction(this, "Eclipse.redo()", true);
	private JsAction configAction = new JsAction(this, "Eclipse.config()", true);
	
	public BrowserEditor() {
		super();
		//Display display = new Display();
		//clipboard = new Clipboard(display);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		
	}

	public void setActions(IActionBars bars) {
		setAction(ITextEditorActionConstants.PRINT, printAction);
		setAction(ITextEditorActionConstants.UNDO, undoAction);
		setAction(ITextEditorActionConstants.REDO, redoAction);
		
		String pkg = getClass().getPackage().getName();
	    bars.setGlobalActionHandler("undo", undoAction);
	    bars.setGlobalActionHandler("redo", redoAction);
	    bars.setGlobalActionHandler(pkg+".configAction", configAction);
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
			log("status=" + ev.text);
			String[] params = ev.text.split(",");
			String method = params[0];
			if ("load".equals(method)) {
				onLoad(params);
			} else if ("change".equals(method)) {
				onChange(params);
			} else if ("syncPreferences".equals(method)) {
				onSyncPreferences(params);
			} else if ("copyClip".equals(method)) {
				onCopyClip(params);
			}
		}

	}
/*
	private void onLoad(String[] params) {
		startup();

		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String path = input.getFile().getFullPath().toString().replaceFirst("^/", "");
		File file = new File(workspaceDirectory, path);

		String url = "file://" + file.getAbsolutePath().replaceAll("[\\\\]", "/");
		browser.execute("Eclipse.setContentUrl('" + url + "')");
	}
*/
	
	private void onLoad(String[] params) {
		startup();
		
		try {
			IFileEditorInput input = (IFileEditorInput)getEditorInput();
			InputStream in = input.getFile().getContents();
			try {
				browser.execute("Eclipse.openContent()");
				Reader r = new InputStreamReader(in,ENCODING);
				char[] buff = new char[4096];
				int n;
				while ((n=r.read(buff))>=0) {
					byte[] plain = new String(buff,0,n).getBytes(ENCODING);
					String base64 = EncodingUtils.encodeBase64(plain);
					browser.execute("Eclipse.addContent('"+base64+"')");
				}
				browser.execute("Eclipse.closeContent()");
			} catch (Exception e) {
				browser.execute("Eclipse.failContent('"+e+"')");
				throw e;
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
  
	private void startup() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String json = "";
		for (int i = 0; i < Preference.ITEMS.length; i++) {
			String key = Preference.ITEMS[i].key;
			if (Preference.ITEMS[i].type == String.class) {
				json += key + ":'" + store.getString(key) + "',";
			} else {
				json += key + ":" + store.getString(key) + ",";
			}
		}

		browser.execute("Eclipse.startup({" + json + "})");
	}

	private void onSyncPreferences(String[] params) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		for (int i = 0; i < Preference.ITEMS.length; i++) {
			String key = Preference.ITEMS[i].key;
			String val = (String) browser.evaluate("return Eclipse.getPreferences('" + key + "')");
			store.setValue(key, val);
		}
		try {
			((IPersistentPreferenceStore) store).save();
		} catch (Exception e) {
			log("Error:" + e);
		}
	}

	private void onChange(String[] params) {
		changeHistoryCount = Integer.valueOf(params[1]);
		firePropertyChange(EditorPart.PROP_DIRTY);
	}
	
	private void onCopyClip(String[] params) {
		String textData = (String) browser.evaluate("return Eclipse.getCopyClip();");
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { textData },
				new Transfer[] { textTransfer });
	}

	// TODO:
	public void onPasteClip(String[] params) {
		TextTransfer transfer = TextTransfer.getInstance();
		String data = (String) clipboard.getContents(transfer);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		String content = (String) browser.evaluate("return Eclipse.getContent();");
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(
					new ByteArrayInputStream(content.getBytes(ENCODING)),
					true, // keep saving, even if IFile is out of sync with the Workspace
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
		if (browser != null) browser.dispose();
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

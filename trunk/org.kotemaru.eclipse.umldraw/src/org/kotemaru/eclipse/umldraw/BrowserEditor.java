package org.kotemaru.eclipse.umldraw;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public class BrowserEditor extends TextEditor {

	private BrowserCtrl browserCtrl;

	private Action printAction  ;
	private Action undoAction   ;
	private Action redoAction   ;
	private Action configAction ;
	
	public BrowserEditor() {
		super();
	}
	public BrowserCtrl getBrowserCtrl() {
		return browserCtrl;
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
		browserCtrl = new BrowserCtrl(this, parent);
		printAction  = browserCtrl.getAction("print");
		undoAction   = browserCtrl.getUndoAction();
		redoAction   = browserCtrl.getRedoAction();
		configAction = browserCtrl.getAction("config");
	}

	public void doActivate(IActionBars bars) {
		setAction(ITextEditorActionConstants.PRINT, printAction);
		setAction(ITextEditorActionConstants.UNDO, undoAction);
		setAction(ITextEditorActionConstants.REDO, redoAction);
		
		String pkg = getClass().getPackage().getName();
	    bars.setGlobalActionHandler("undo", undoAction);
	    bars.setGlobalActionHandler("redo", redoAction);
	    bars.setGlobalActionHandler(pkg+".configAction", configAction);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		browserCtrl.doSave(monitor, file);
	}

	@Override
	public boolean isDirty() {
		return browserCtrl.isDirty();
	}
	public void updateDirty() {
		firePropertyChange(EditorPart.PROP_DIRTY);
	}

	@Override
	public void dispose() {
		if (browserCtrl != null) browserCtrl.dispose();
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void doSaveAs() {
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		IPath path = dialog.getResult();
		if (path != null) {
			final IFile file 
				= ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			try {
				WorkspaceModifyOperation ope = new WorkspaceModifyOperation() {
					public void execute(final IProgressMonitor monitor) {
						browserCtrl.doSave(monitor, file);
					}
				};
				new ProgressMonitorDialog(shell).run(false,false,ope);
				setInput(new FileEditorInput(file)); // Change edit this file.
			} catch (InterruptedException e) {
				// should not happen, since the monitor dialog is not cancelable
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void log(String msg) {
		System.out.println(msg); // TODO:
	}


}

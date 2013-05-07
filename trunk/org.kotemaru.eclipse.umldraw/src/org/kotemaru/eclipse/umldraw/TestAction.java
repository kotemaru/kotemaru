package org.kotemaru.eclipse.umldraw;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;

public class TestAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	public TestAction() {
	}
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	@Override
	public void run(IAction action) {
		//IEditorPart editor = window.getActivePage().getActiveEditor();
		//if (editor instanceof BrowserEditor) {
		//	IAction act = ((BrowserEditor)editor).getAction(action.getId());
		//	act.run();
		//}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}
}
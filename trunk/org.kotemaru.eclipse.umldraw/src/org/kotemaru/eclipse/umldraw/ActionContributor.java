package org.kotemaru.eclipse.umldraw;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;


public class ActionContributor extends BasicTextEditorActionContributor {
	public ActionContributor() {
	}
	
	@Override
	public void setActiveEditor(IEditorPart part) {
		IActionBars bars = getActionBars();
		((BrowserEditor) part).setActions(bars);
		super.setActiveEditor(part);
	}

}

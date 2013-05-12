package org.kotemaru.eclipse.umldraw;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;


public class NewWizard extends BasicNewResourceWizard {

	/**
	 * The wizard id for creating new files in the workspace.
	 * @since 3.4
	 */
	public static final String WIZARD_ID = "org.eclipse.ui.wizards.new.file"; //$NON-NLS-1$

	private NewWizardPage mainPage;
	

	public NewWizard() {
		super();
	}

	/* (non-Javadoc)
	 * Method declared on IWizard.
	 */
	public void addPages() {
		super.addPages();
		mainPage = new NewWizardPage("newFilePage1", getSelection());
		mainPage.setTitle("UML draw");
		mainPage.setDescription("");
		mainPage.setFileExtension("udr");
		addPage(mainPage);
	}

	/* (non-Javadoc)
	 * Method declared on IWorkbenchWizard.
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle("UML Draw");
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * Method declared on IWizard.
	 */
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}

		selectAndReveal(file);

		// Open editor on new file.
		IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				IWorkbenchPage page = dw.getActivePage();
				if (page != null) {
					IDE.openEditor(page, file, true);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	
	static class NewWizardPage extends WizardNewFileCreationPage {
		private static final String INIT_DATA =
		"<?xml version='1.0' encoding='utf-8' ?>" +
		"<svg xml:space='preserve' width='1' height='1' xmlns='http://www.w3.org/2000/svg'>" +
		"<metadata id='umldraw-data'>{}</metadata>" +
		"</svg>";

		public NewWizardPage(String pageName, IStructuredSelection selection) {
			super(pageName, selection);
		}
		
		protected InputStream getInitialContents() {
			try {
				return new ByteArrayInputStream(INIT_DATA.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}

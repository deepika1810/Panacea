package io.sour.sampleplugin.actions;

import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import io.sour.sampleplugin.database.CodeDatabase;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import java.util.*;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {


		String message = "&&&&&&&&&&";
		String message2 = "##########";
		StringBuilder message3 = new StringBuilder();
		boolean flag = false;
		IEditorPart editor = window.getActivePage().getActiveEditor();
		if (editor == null) {
			MessageDialog.openError(window.getShell(), "Sample Plugin", "No file open to check.");
			return;
		}
	    ITextEditor ite = (ITextEditor)editor;
	    IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());
	    message = doc.get();
	    if(message.contains(" @Override\n" + 
	    		"		    public void checkClientTrusted(X509Certificate[] certs, String authType) {\n" + 
	    		"		    }\n" + 
	    		"\n" + 
	    		"		    @Override\n" + 
	    		"		    public void checkServerTrusted(X509Certificate[] certs, String authType) {\n" + 
	    		"		    }"))
	    {
	    		message2 = "Found it!!";
		    Scanner sc = new Scanner(message);
		    while(sc.hasNextLine())
		    {
		      String line = sc.nextLine();
		      // process the line
		      if(line.trim().startsWith("TrustManager[]"))
		      {
		    	  	flag = true;
		      }
		      if(flag == true)
		      {
		    	  	message3.append(line);
		      }
		      if(line.endsWith("};"))
		      {
		    	  	flag = false;
		      }
		    }
		    sc.close();
	    }
	    
	    String message4 = "\n\n^^^$$$$$$$$$$^^^\n\n";
	    
	    message4 += CodeDatabase.compareSnippet(message3.toString()) ? "DATABASE MATCH" : "NO MATCH FOUND IN DATABASE";
	
		//message += "\n" + b;
		//message += "\n" + c;
		//message += "\n" + d;
		MessageDialog.openInformation(
			window.getShell(),
			"Sampleplugin",
			"Hello, Eclipse world" + "\n" + message2+ "\n" + message3 + "\n" + message4);
		
		
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
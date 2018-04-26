package io.sour.sampleplugin.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
	public void run(IAction action) 
	{
		
		File file = new File("/Users/deepikamulchandani/Downloads/Keywords");
		String code;
		String keyword;
		String message = "Welcome to Automatic Security Bug Fix Plugin!";
		String message2 = " ";
		String message3 = " ";
		IEditorPart editor = window.getActivePage().getActiveEditor();
		if (editor == null) {
			MessageDialog.openError(window.getShell(), "Sample Plugin", "No file open to check.");
			return;
		}
		try 
		{
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine())
			{
				keyword = sc.next();
				ITextEditor ite = (ITextEditor)editor;
			    IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());
			    code = doc.get();
			    if(code.contains(keyword))
			    {
			    	 try 
			    	 {
			    		 	 message2 = CodeDatabase.compareSnippet(code,keyword);
			    		 	 message3 = CodeDatabase.compareSnippetAST(code,keyword);
			    	 }
			    	 catch(InvalidFormatException | IOException e) 
			    	 {
			 			// TODO Auto-generated catch block
			 			e.printStackTrace();
			 	 }
			    	 
			    }
			    
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		final String customMessage;
		System.out.println(message2);
		if(message2 == " ")
			customMessage="\nResult from Tokenizer\n"+message3;
		else if(message3 == " ")
			customMessage="\nResult from GumTree Diff Score\n"+message2;
		else if(message2==" " && message3==" ")
			customMessage="\nYour code seems secure to Automatic Security Bug Fixer";
		else
			customMessage="\nResult from Tokenizer\n"+message2+"\nResult from GumTree Diff Score\n"+message3;
		
		
		MessageDialog m = new MessageDialog(window.getShell(), "Security Issue Plugin", null, message+ "\n", 0, 0,"Okay") {
			 @Override
			  protected Control createCustomArea( Composite parent ) {
			    Link link = new Link( parent, SWT.WRAP );
			    // link.setText( "Please visit <a href=\"http://google.com\">this link</a>." );
			    link.setText(customMessage);
			    link.addSelectionListener(new SelectionAdapter(){
			    		
		            public void widgetSelected(SelectionEvent e) {
		                   System.out.println("You have selected: "+e.text);
		                   try {
		                    //  Open default external browser 
		                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
		                  } 
		                 catch (PartInitException ex) {
		                    // TODO Auto-generated catch block
		                     ex.printStackTrace();
		                } 
		                catch (MalformedURLException ex) {
		                    // TODO Auto-generated catch block
		                    ex.printStackTrace();
		                }
		            }
		        });
			    return link;
			  }

		};
		m.open();
		
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
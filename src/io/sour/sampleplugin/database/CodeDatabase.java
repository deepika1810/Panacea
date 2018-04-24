package io.sour.sampleplugin.database;

import java.util.StringTokenizer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
public class CodeDatabase 
{
	 private static IWorkbenchWindow window;
	 public static final String insecure_posts = "/Users/deepikamulchandani/Downloads/InsecurePosts4.xls";
	 public static int count = 0;
	/*public static String[] SNIPPETS = {
"Snippet 0",
"		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {" + 
"		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {" + 
"		        return null;" + 
"		    }" + 
"		    @Override" + 
"		    public void checkClientTrusted(X509Certificate[] certs, String authType) {" + 
"		    }" + 
"		    @Override" + 
"		    public void checkServerTrusted(X509Certificate[] certs, String authType) {" + 
"		    }" + 
"		}};",
"SNIPPET 2",
"SNIPPET 3",
"ETC, ETC"
	};*/
	
	@SuppressWarnings("deprecation")
	public static String compareSnippet(String sourceSnippet,String keyword) throws IOException , InvalidFormatException
	{
		
		Workbook workbook = WorkbookFactory.create(new File(insecure_posts));
       /* System.out.println("Workbook has " + workbook.getNumberOfSheets());
        workbook.forEach(sheet -> {
            System.out.println("=> " + sheet.getSheetName());
        });*/
		Sheet sheet = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		int noOfColumns = sheet.getRow(0).getLastCellNum();
		//int token_column_number = 4;
		StringBuilder result = new StringBuilder();
		HashSet<Integer> row_numbers = new HashSet<Integer>();
		HashSet<String> reasons_list = new HashSet<String>();
		sheet.forEach(row -> {
			Cell keywordCell = row.getCell(noOfColumns-1);
			String keywordCellValue = dataFormatter.formatCellValue(keywordCell);
			boolean flag = false;
			boolean isMatch = true;
            if(keywordCellValue.equals(keyword))
            {
            		//8,7
            		StringBuilder devCode = new StringBuilder();
            		Cell startTokenCell = row.getCell(7);
            		String startTokenCellValue = dataFormatter.formatCellValue(startTokenCell);
            		Cell endTokenCell = row.getCell(6);
            		String endTokenCellValue = dataFormatter.formatCellValue(endTokenCell);
            		Cell tokenizedCell = row.getCell(3);
            		String tokenizedCellValue = dataFormatter.formatCellValue(tokenizedCell);
            		StringTokenizer databaseTokenizedValue = new StringTokenizer(tokenizedCellValue);
            		Scanner sc = new Scanner(sourceSnippet);
        		    while(sc.hasNextLine())
        		    {
        		      String line = sc.nextLine();
        		      // process the line
        		      if(line.trim().startsWith(startTokenCellValue))
        		      {
        		    	  		flag = true;
        		      }
        		      if(flag == true)
        		      {
        		    	  	devCode.append(line);
        		    	  	devCode.append("\n");
        		      }
        		      if(line.endsWith(endTokenCellValue))
        		      {
        		    	  	StringTokenizer interimDevCode = new StringTokenizer(devCode.toString());
        		    	  	if(endTokenCellValue.equals("}") && databaseTokenizedValue.countTokens()==(interimDevCode.countTokens()))
        		    	  	{
        		    	  		flag = false;
    		    	  			break;
        		    	  	}
        		    	  	
        		      }
        		    }
        		   
        		    String DevCode = devCode.toString();
        		    StringTokenizer tokenizedDevCode = new StringTokenizer(DevCode);
        		    
            		if (tokenizedDevCode.countTokens() != databaseTokenizedValue.countTokens()) 
            		{
            			System.out.println("Count not matched!");
            		}
            		else
            		{
            			while (databaseTokenizedValue.hasMoreTokens() && tokenizedDevCode.hasMoreTokens() ) 
            			{
            				if (!databaseTokenizedValue.nextToken().equalsIgnoreCase(tokenizedDevCode.nextToken())) 
            	    			{
            					System.out.println("WHYYYYYY");
            					isMatch = false;
            	    				break;
            	    			}
            	    		}
            			if (isMatch) 
            			{
            					count++;
            					if(!row_numbers.contains(row.getRowNum()))
            						row_numbers.add(row.getRowNum());
            					System.out.println("IT WORKS!!!");
            			}
            		}
            }
        });
		/*for (String snippet: CodeDatabase.SNIPPETS) {
	    	boolean isMatch = true;
			StringTokenizer tokenizedSnippet = new StringTokenizer(sourceSnippet);
	    	StringTokenizer databaseSnippet = new StringTokenizer(snippet);
	    	if (tokenizedSnippet.countTokens() != databaseSnippet.countTokens()) {
	    		System.out.println("COUNT NOT MATCHED");
	    		continue;
	    	}
	    	while (databaseSnippet.hasMoreTokens()) {
	    		if (!databaseSnippet.nextToken().equalsIgnoreCase(tokenizedSnippet.nextToken())) {
	    			isMatch = false;
	    			break;
	    		}
	    	}
	    	if (isMatch) {
	    		System.out.println("ISMATCH IS TRUE");
	    		return true;
	    	}
	    }
		System.out.println("RETURNING FALSE");
	    return false;
	    */
	System.out.println(count);
	
	if(count>0)
	{
		//4,5
		result.append("It appears that a part of your code is insecure."+"\n");
		for(Integer i:row_numbers)
		{
			sheet.forEach(row -> {
				if(row.getRowNum()==i)
				{
					Cell reason = row.getCell(5);
					String reasonValue = dataFormatter.formatCellValue(reason);
					if(!reasons_list.contains(reasonValue))
					{
						reasons_list.add(reasonValue);
						Cell url = row.getCell(4);
						String urlValue = dataFormatter.formatCellValue(url);
						if(count==1)
							result.append("The reason seems to be "+ reasonValue + "\n");
						else
							result.append("The reason could also be "+ reasonValue + "\n");
						
						if(urlValue!="") 
						{
							result.append("The URL of the possible post you possibly referred is "+ urlValue + "\n");
						}
						else
						{
							result.append("Sorry! Currently, our database does not have a URL you could have visited for this insecure code."+"\n");
						}
	
					}
						
					}
				});
			
		}
			
	}
	else
	{
		result.append("Everything seems safe to Security Bug Fixer!");
		result.append("\n");
	}
		return result.toString();
	}
	
	public static void compareSnippetAST(String sourceSnippet,String keyword) throws IOException , InvalidFormatException
	{
		Workbook workbook = WorkbookFactory.create(new File(insecure_posts));
		Sheet sheet = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		int noOfColumns = sheet.getRow(0).getLastCellNum();
		HashMap<Integer,String> row_code = new HashMap<Integer,String>();
		sheet.forEach(row -> {
			StringBuilder devCode = new StringBuilder();
			Cell keywordCell = row.getCell(noOfColumns-1);
			String keywordCellValue = dataFormatter.formatCellValue(keywordCell);
			boolean flag = false;
			boolean isMatch = true;
            if(keywordCellValue.equals(keyword))
            {
	        		Cell startTokenCell = row.getCell(7);
	        		String startTokenCellValue = dataFormatter.formatCellValue(startTokenCell);
	        		Cell endTokenCell = row.getCell(6);
	        		String endTokenCellValue = dataFormatter.formatCellValue(endTokenCell);
	        		Cell tokenizedCell = row.getCell(3);
	        		String tokenizedCellValue = dataFormatter.formatCellValue(tokenizedCell);
	        		StringTokenizer databaseTokenizedValue = new StringTokenizer(tokenizedCellValue);
	            Scanner sc = new Scanner(sourceSnippet);
	    		    while(sc.hasNextLine())
	    		    {
		    		      String line = sc.nextLine();
		    		      // process the line
		    		      if(line.trim().startsWith(startTokenCellValue))
		    		      {
		    		    	  		flag = true;
		    		      }
		    		      if(flag == true)
		    		      {
		    		    	  	devCode.append(line);
		    		    	  	devCode.append("\n");
		    		      }
		    		      if(line.endsWith(endTokenCellValue))
		    		      {
		    		    	  	StringTokenizer interimDevCode = new StringTokenizer(devCode.toString());
		    		    	  	if(endTokenCellValue.equals("}") && databaseTokenizedValue.countTokens()==(interimDevCode.countTokens()))
		    		    	  	{
		    		    	  		flag = false;
				    	  		break;
		    		    	  	}
			    		    	  	
			    		  }
			    	 }
			   
			    		 	if(!devCode.toString().equals("") && devCode != null)
			    		 	{
			    		 		if(!row_code.containsKey(row.getRowNum()))
            						row_code.put(row.getRowNum(),devCode.toString());
			    		 
			    		 		
			    		 	}
						
				
	    		    
            }
		});
		try 
    		{
			for( Integer i:row_code.keySet())
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/deepikamulchandani/Downloads/DevCode"+i+".java")));
				writer.write(row_code.get(i).toString());
				writer.close();
			}
		
    		} 
		catch (IOException e) 
		{	
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sheet.forEach(row -> {
			if(row_code.containsKey(row.getRowNum()))
			{
				
				Cell javafileid = row.getCell(2);
				String javafileidValue = dataFormatter.formatCellValue(javafileid);
				String filename = FindFile(javafileidValue);
				System.out.println("HELLO FROM AST from outside");
				System.out.println(filename);
				String output;
				if(!filename.equals("null"))
				{
					//Executing gumtree
				//	/Users/deepikamulchandani/gumtree/dist/build/distributions/gumtree/bin/gumtree diff /Users/deepikamulchandani/Desktop/WM/Walmart_DeepikaM/MovieTheaterDesign.java /Users/deepikamulchandani/Desktop/WM/MovieTheaterDesign.java
					System.out.println("HELLO FROM AST");
					Runtime r = Runtime.getRuntime();
				    Process p;
				    BufferedReader is;
				    try 
				    {
						p = r.exec("/Users/deepikamulchandani/gumtree/dist/build/distributions/gumtree/bin/gumtree diff /Users/deepikamulchandani/Downloads/answers/"+filename+" /Users/deepikamulchandani/Downloads/DevCode"+row.getRowNum()+".java");
						is = new BufferedReader(new InputStreamReader(p.getInputStream()));
						while ((output = is.readLine()) != null)
						{
							System.out.println(output);
						}
					} 
				    catch (IOException e) 
				    {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}	
			
		});
		
		
	}
	
	public static String FindFile(String filename) 
	{
		File folder = new File("/Users/deepikamulchandani/Downloads/answers/");
		File[] listOfFiles = folder.listFiles();
		String result = "null";
		 for (int i = 0; i < listOfFiles.length; i++) 
		 {
			 //String temp = listOfFiles[i].getName().substring(7,7+filename.length()-1);
			 //System.out.println(temp);
			 if(listOfFiles[i].getName().contains(filename))
				  result = listOfFiles[i].getName();
		 }
		 return result;
	}
	
	
	
	
}

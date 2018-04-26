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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
public class CodeDatabase 
{
	 public static final String insecure_posts = "/Users/deepikamulchandani/Downloads/InsecurePosts4.xls";
	 public static final String secure_posts = "/Users/deepikamulchandani/Downloads/SecurePosts.xls";
	 public static int count = 0;
	 public static int countAST =0;
	 public static int iter = 0;
	 public static int linenumberstart =0;
	 public static int linenumberend =0;
	 public static int countline =0;
	 public static int snippetlines =0;
	//TOKENIZER
	public static String compareSnippet(String sourceSnippet,String keyword) throws IOException , InvalidFormatException
	{
		
		Workbook workbook = WorkbookFactory.create(new File(insecure_posts));
		Workbook sec_workbook = WorkbookFactory.create(new File(secure_posts));
		Sheet sheet = workbook.getSheetAt(0);
		Sheet sec_sheet = sec_workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		int noOfColumns = sheet.getRow(0).getLastCellNum();
		StringBuilder result = new StringBuilder();
		HashSet<Integer> row_numbers = new HashSet<Integer>();
		HashSet<String> reasons_list = new HashSet<String>();
		HashMap<Integer,Integer> linestart = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> lineend = new HashMap<Integer,Integer>();
		sheet.forEach(row -> {
			countline=0;
			snippetlines=0;
			Cell keywordCell = row.getCell(noOfColumns-2);
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
        		      countline++;
        		      if(line.trim().startsWith(startTokenCellValue))
        		      {
        		    	  		flag = true;
        		    	  		linenumberstart = countline;
    	    		    	  		if(!linestart.containsKey(row.getRowNum()))
    	    		    	  			linestart.put(row.getRowNum(),linenumberstart);
        		      }
        		      if(flag == true)
        		      {	
        		    	    snippetlines++;
        		    	  	devCode.append(line);
        		    	  	devCode.append("\n");
        		      }
        		      if(line.endsWith(endTokenCellValue))
        		      {
        		    	  	StringTokenizer interimDevCode = new StringTokenizer(devCode.toString());
        		    	  	if(endTokenCellValue.equals("}") )
        		    	  	{
        		    	  		if(databaseTokenizedValue.countTokens()==(interimDevCode.countTokens()))
        		    	  		{
        		    	  			flag = false;
            		    	  		linenumberend = linenumberstart + snippetlines;
    	    		    	  			if(!lineend.containsKey(row.getRowNum()))
    	    		    	  				lineend.put(row.getRowNum(),linenumberend);
    	    		    	  			break;
        		    	  		}	
        		    	  	}
        		    	  	else
        		    	  	{
        		    	  		flag = false;
        		    	  		linenumberend = linenumberstart + countline;
	    		    	  		if(!lineend.containsKey(row.getRowNum()))
		    		    	  		lineend.put(row.getRowNum(),linenumberend);
    		    	  			break;
        		    	  	}
        		      }
        		    }
        		   
        		    String DevCode = devCode.toString();
        		    StringTokenizer tokenizedDevCode = new StringTokenizer(DevCode);
        		   
        		    		
            		if (tokenizedDevCode.countTokens() != databaseTokenizedValue.countTokens()) 
            		{
            			//System.out.println("Count not matched!");
            		}
            		else
            		{
            			while (databaseTokenizedValue.hasMoreTokens() && tokenizedDevCode.hasMoreTokens() ) 
            			{
            				if (!databaseTokenizedValue.nextToken().equalsIgnoreCase(tokenizedDevCode.nextToken())) 
            	    			{
            					//System.out.println("WHYYYYYY");
            					isMatch = false;
            	    				break;
            	    			}
            	    		}
            			if (isMatch) 
            			{
            					count++;
            					if(!row_numbers.contains(row.getRowNum()))
            						row_numbers.add(row.getRowNum());
            					//System.out.println("IT WORKS!!!");
            			}
            		}
            }
        });
		System.out.println(count);
		iter = count;
		if(iter>0)
		{
			//4,5
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
							if(iter == count)
							{
								result.append((count-iter+1)+". It appears that a part of your code from line number "+linestart.get(row.getRowNum()) +" to line number "+lineend.get(row.getRowNum())+" is insecure."+"\n");
								result.append("The reason seems to be "+ reasonValue + "\n");
							}	
							else
							{
								result.append((count-iter+1)+". It appears that a part of your code from line number "+linestart.get(row.getRowNum()) +" to line number "+lineend.get(row.getRowNum())+" is insecure."+"\n");
								result.append("The reason could also be "+ reasonValue + "\n");
							}
								
							
							if(urlValue!="") 
							{
								result.append("The URL of the possible post you possibly referred is:\n <a href=\""+ urlValue + "\">" + urlValue + "</a>\n");
							}
							iter--;
						}
						result.append(" START line number: "+linestart.get(row.getRowNum()));
						result.append(" END line number: "+lineend.get(row.getRowNum()));
						Cell secure = row.getCell(noOfColumns-1);
						String secureValue = dataFormatter.formatCellValue(secure);
						int secureintvalue = Integer.parseInt(secureValue);
						if(secureintvalue!=0)
						{
							int num_of_rows = sec_sheet.getLastRowNum();
							if(secureintvalue <= num_of_rows)
							{
								Row sec_row = sec_sheet.getRow(secureintvalue);
								Cell secureCode = sec_row.getCell(3);
								String secureCodeValue = dataFormatter.formatCellValue(secureCode);
								result.append(" SECURE FOUND: "+secureCodeValue);
							}
						}
						
						
						
					}
			  });
				
			}
			
		}
		return result.toString();
		}
	//AST Difference
		public static String compareSnippetAST(String sourceSnippet,String keyword) throws IOException , InvalidFormatException
		{
			iter = 0;
			Workbook workbook = WorkbookFactory.create(new File(insecure_posts));
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter dataFormatter = new DataFormatter();
			int noOfColumns = sheet.getRow(0).getLastCellNum();
			HashSet<Integer> row_numbers = new HashSet<Integer>();
			HashSet<String> reasons_list = new HashSet<String>();
			StringBuilder result = new StringBuilder();
			HashMap<Integer,String> row_code = new HashMap<Integer,String>();
			HashMap<Integer,Integer> linestart = new HashMap<Integer,Integer>();
			HashMap<Integer,Integer> lineend = new HashMap<Integer,Integer>();
			sheet.forEach(row -> {
				countline=0;
				snippetlines=0;
				StringBuilder devCode = new StringBuilder();
				Cell keywordCell = row.getCell(noOfColumns-2);
				String keywordCellValue = dataFormatter.formatCellValue(keywordCell);
				boolean flag = false;
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
			    		      countline++;
			    		      if(line.trim().startsWith(startTokenCellValue))
			    		      {
			    		    	  		flag = true;
			    		    	  		linenumberstart = countline;
				    		    	  	if(!linestart.containsKey(row.getRowNum()))
				    		    	  		linestart.put(row.getRowNum(),linenumberstart);
			    		      }
			    		      if(flag == true)
			    		      {
			    		    	    snippetlines++;
			    		    	  	devCode.append(line);
			    		    	  	devCode.append("\n");
			    		      }
				    		   if(endTokenCellValue.equals("}") )
		        		    	  {
				    			   StringTokenizer interimDevCode = new StringTokenizer(devCode.toString());
				    			   if(databaseTokenizedValue.countTokens()==(interimDevCode.countTokens()))
		        		    	  		{
		        		    	  			flag = false;
		            		    	  		linenumberend = linenumberstart + snippetlines;
		    	    		    	  			if(!lineend.containsKey(row.getRowNum()))
		    	    		    	  				lineend.put(row.getRowNum(),linenumberend);
		    	    		    	  			break;
		        		    	  		}	
		        		    	  }
		        		    	  else
		        		    	  {
		        		    	  		flag = false;
		        		    	  		linenumberend = linenumberstart + countline;
			    		    	  		if(!lineend.containsKey(row.getRowNum()))
				    		    	  		lineend.put(row.getRowNum(),linenumberend);
		    		    	  			break;
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
					int score = 0 ;
					Cell javafileid = row.getCell(2);
					String javafileidValue = dataFormatter.formatCellValue(javafileid);
					String filename = FindFile(javafileidValue);
					System.out.println(filename);
					String output;
					if(!filename.equals("null"))
					{
						//Executing gumtree
						Runtime r = Runtime.getRuntime();
					    Process p;
					    BufferedReader is;
					    try 
					    {
							p = r.exec("/Users/deepikamulchandani/gumtree/dist/build/distributions/gumtree/bin/gumtree diff /Users/deepikamulchandani/Downloads/answers/"+filename+" /Users/deepikamulchandani/Downloads/DevCode"+row.getRowNum()+".java");
							is = new BufferedReader(new InputStreamReader(p.getInputStream()));
							while ((output = is.readLine()) != null)
							{
								//System.out.println(output);
								if(output.startsWith("Match ")||output.startsWith("Update SimpleName:"))
								{
									
								}
								else 
								{
									score++;
								}
							}
						} 
					    catch (IOException e) 
					    {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					
					System.out.println(score);
					if(score<10)
					{
						countAST++;
						if(!row_numbers.contains(row.getRowNum()))
							row_numbers.add(row.getRowNum());
					}
					
				}	
				
			});
			iter = countAST;
			if(iter>0)
			{
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
								if(iter==countAST) 
								{
									result.append((countAST-iter+1)+". It appears that the part of your code from line number "+linestart.get(row.getRowNum()) +" to line number "+lineend.get(row.getRowNum())+" is insecure."+"\n");
									result.append("The reason seems to be "+ reasonValue + "\n");
								}	
								else
								{
									result.append((countAST-iter+1)+". It appears that the part of your code from line number "+linestart.get(row.getRowNum()) +" to line number "+lineend.get(row.getRowNum())+" is insecure."+"\n");
									result.append("The reason could also be "+ reasonValue + "\n");
								}
								if(urlValue!="") 
								{
									result.append("The URL of the possible post you possibly referred is:\n <a href=\""+ urlValue + "\">" + urlValue + "</a>\n");
								}
							  iter--;
							}
								
							}
						});
				}
			}
			//System.out.println(result.toString());
			return result.toString();
	
		}
	
		public static String FindFile(String filename) 
		{
			File folder = new File("/Users/deepikamulchandani/Downloads/answers/");
			File[] listOfFiles = folder.listFiles();
			String result = "null";
			 for (int i = 0; i < listOfFiles.length; i++) 
			 {
				 if(listOfFiles[i].getName().contains(filename))
					  result = listOfFiles[i].getName();
			 }
			 return result;
		}
}

package io.sour.sampleplugin.database;

import java.util.StringTokenizer;

public class CodeDatabase {
	public static String[] SNIPPETS = {
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
	};
	
	public static boolean compareSnippet(String sourceSnippet) {
	    for (String snippet: CodeDatabase.SNIPPETS) {
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
	}

}

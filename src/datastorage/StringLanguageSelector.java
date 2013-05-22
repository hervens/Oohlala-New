package datastorage;

import java.util.Locale;

import android.util.Log;


public class StringLanguageSelector {
	
	public static String retrieveString(String string){
		if (string != null){
			string = preParse(string);
			//Log.i("StringLanguageSelector", string);
			
			String enString = "]en[";
			String frString = "]fr[";
			if (Locale.getDefault().getDisplayLanguage().contains("English")){
				String parsedString = null;
				if (string.matches("(?i).*\\]en\\[.*")){
					parsedString = string.substring(
							string.indexOf(enString) + enString.length());
					if (parsedString.matches("(?i).*\\]fr\\[.*")){
						parsedString = parsedString.substring(0, parsedString.indexOf(frString));
					} 
					
					return postParse(parsedString);
				} else {
					return postParse(string);
				}
			} else if(Locale.getDefault().getDisplayLanguage().contains("French")){
				String parsedString = null;
				if (string.matches("(?i).*\\]fr\\[.*")){
					parsedString = string.substring(
							string.indexOf(frString) + frString.length());
				
					return postParse(parsedString);
				} else {
					return postParse(string);
				}
			} else {
				String parsedString = null;
				if (string.matches("(?i).*\\]en\\[.*")){
					parsedString = string.substring(
							string.indexOf(enString) + enString.length());
					if (parsedString.matches("(?i).*\\]fr\\[.*")){
						parsedString = parsedString.substring(0, parsedString.indexOf(frString));		
					}
					
					return postParse(parsedString);
				} else {
					return postParse(string);
				}
			}
			
			/*
			String name1 = null, name2 = null, name3 = null, name4 = null;
			
			if (Locale.getDefault().getDisplayLanguage().contains("English")){
				if (string.matches("(?i).*\\]en\\[.*")){
					String[] tokens = string.split("\\[");
					name1 = tokens[0].split("\\]")[1];
					name2 = tokens[1].split("\\]")[0];
					//System.out.println(name1 +" "+ name2 +" "+ name3 +" "+ name4);
					return name2;
				} else {
					return string;
				}
			} else {
				if (string.matches("(?i).*\\]fr\\[.*")){
					String[] tokens = string.split("\\[");
					name1 = tokens[0].split("\\]")[1];
					name2 = tokens[1].split("\\]")[0];
					name3 = tokens[1].split("\\]")[1];
					name4 = tokens[2];
					//System.out.println(name1 +" "+ name2 +" "+ name3 +" "+ name4);
					return name4;
				} else {
					return string;
				}
			}
			*/
		} else {
			return null;
		}
	}
	
	public static String preParse(String s){
		s = s.replaceAll("\n", "\\\\n");
		s = s.replaceAll("\r", "\\\\r");
		
		return s;
	}
	
	public static String postParse(String s){
		s = s.replaceAll("\\\\n", "\n");
		s = s.replaceAll("\\\\r", "\r");
		
		return s;
	}
	
}

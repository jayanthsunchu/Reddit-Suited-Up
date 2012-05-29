package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Mdown {
	
	
	
	public static ArrayList<String> getLinks(String selfText){
		
		
		return new Mparser().getCommentLinks(selfText);
	}
	
	public static String getHtml(String selfText) {
		
			return new Mparser().markdown(selfText);
		
	}
}

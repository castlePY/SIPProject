package com.sip.util;

import java.io.InputStream;

public class URLUtil {
	public static InputStream getURL(String url){
	return URLUtil.class.getClassLoader().getResourceAsStream(url);
	}
}

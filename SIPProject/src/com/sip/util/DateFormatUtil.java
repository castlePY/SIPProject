package com.sip.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
 public static String getDateTime(){
	 
	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
	 return simpleDateFormat.format(new Date());
	 
 }
}

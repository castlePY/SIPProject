package com.sip.call;

import java.util.EventListener;

public interface CallListener extends EventListener {
  
	public void callEvent(CallEvent event);
	   
  
}

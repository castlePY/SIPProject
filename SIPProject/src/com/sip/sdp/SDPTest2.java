package com.sip.sdp;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

public class SDPTest2 {
   public void text(String SDPdata){
	   try {
		SdpFactory instance = SdpFactory.getInstance();
		SessionDescription createSessionDescription = instance.createSessionDescription(SDPdata);
		System.out.println("SDPText::"+createSessionDescription.toString());
	} catch (SdpException e) {
		e.printStackTrace();
	}
   }
}

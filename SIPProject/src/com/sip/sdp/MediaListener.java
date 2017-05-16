package com.sip.sdp;

import com.sip.call.Call;
import com.sip.call.CallEvent;
import com.sip.call.CallListener;
import com.sip.media.JMFReceiver;
import com.sip.media.JMFSender;
import com.sip.media.MediaManager;

public class MediaListener implements CallListener{
	MediaManager media;
	@Override
	public void callEvent(CallEvent event) {
		String oldStatus=event.getOldCallState();
		String newStatus=event.getCallState();
		Call call=event.getSource();
		if(oldStatus!=newStatus){
			if(newStatus.equals(Call.CONNECTED)){
				media=new MediaManager(call);
			}
			else if(newStatus.equals(Call.DISCONNECTED)){
				if(media!=null){
					media.stop();
					System.out.println("¹Ø±ÕÁ÷");
				}
				
			}
		}
	}
	

}

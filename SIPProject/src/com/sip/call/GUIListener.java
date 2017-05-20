package com.sip.call;

import com.sip.gui.ChatFrame;

public class GUIListener implements CallListener{
	ChatFrame chat;
	public GUIListener(ChatFrame chat) {
		this.chat=chat;
	}
	@Override
	public void callEvent(CallEvent event) {
		Call call=event.getSource();
		if(!call.getState().equals(Call.DISCONNECTED)){
			chat.getRight_jb2().setEnabled(false);
			
		}
		else{
			chat.getRight_jb2().setEnabled(true);
			chat.getMyLabel().stopTimer();
			}
		if(call.getState().equals(Call.CONNECTED)||call.getState().equals(Call.RINGING)){
			chat.getRight_jb3().setEnabled(true);
		}
		else{
			chat.getRight_jb3().setEnabled(false);
		}
		if(call.getState().equals(Call.CONNECTED)){
			chat.getMyLabel().setVisible(true);
			chat.getMyLabel().startTimer();
		}
	}
	
}

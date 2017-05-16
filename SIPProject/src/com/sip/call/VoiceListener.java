package com.sip.call;

import com.sip.util.HashTableUtil;

public class VoiceListener implements CallListener {

	VoiceManager voiceManager;

	@Override
	public void callEvent(CallEvent event) {
		if (voiceManager == null) {
			voiceManager = new VoiceManager();
		}
		String oldState = event.getOldCallState();
		String newState = event.getCallState();
		if (oldState != newState) {
			if (oldState.equals(Call.ALERTING)) {
				System.out.println("stop alertting");
				voiceManager.stopAlert("sound/alerting.wav");
			} else if (oldState.equals(Call.RINGING)) {
				System.out.println("stop ringing");
				voiceManager.stopAlert("sound/ringing.wav");
			}
			

			if (newState.equalsIgnoreCase(Call.ALERTING)) {
				voiceManager.startAlert("sound/alerting.wav");
			} else if (newState.equalsIgnoreCase(Call.RINGING)) {
				voiceManager.startAlert("sound/ringing.wav");
			}
			else if(newState.equalsIgnoreCase(Call.DISCONNECTED)){
				
				HashTableUtil.remove(event.getSource());
				System.out.println("removed");
			}
		}

	}

}

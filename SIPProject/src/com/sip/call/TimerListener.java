package com.sip.call;

public class TimerListener implements CallListener {

	@Override
	public void callEvent(CallEvent event) {
		Call call=event.getSource();
		if(call.getState().equals(Call.CONNECTED)){
			
		}
	}

}

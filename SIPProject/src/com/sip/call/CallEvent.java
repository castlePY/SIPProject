package com.sip.call;

import java.util.EventObject;

public class CallEvent extends EventObject{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String callState="";
	private String oldCallState="";
	public CallEvent(Object source,String callState) {
		super(source);
		this.callState=callState;
	}
	public String getCallState() {
		return callState;
	}
	public void setCallState(String callState) {
		this.callState = callState;
	}
	public Call getSource(){
		return (Call)source;
	}
	public String getOldCallState() {
		return oldCallState;
	}
	public void setOldCallState(String oldCallState) {
		this.oldCallState = oldCallState;
	}
	
	
}

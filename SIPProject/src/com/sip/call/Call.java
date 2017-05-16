package com.sip.call;

import java.util.ArrayList;

import javax.sip.Dialog;
import javax.sip.message.Request;

import com.sip.sdp.MediaListener;

public class Call {
	public static final String DEFUALTSTATE="idel";
	public static final String ALERTING = "Alerting";
	public static final String RINGING = "Ringing";
	public static final String CONNECTED = "Connected";
	public static final String DISCONNECTED = "Disconnected";
	public static final String FAILED = "Failed";
	public static final String BUSY="busy";
	private String callState =Call.DEFUALTSTATE;
	private Request request= null;
	private  Dialog  dialog= null;
	private String remoteDescription = null;
	private String localDescription=null;
	private int localPort;
	String oldState;
	
	public String getLocalDescription() {
		return localDescription;
	}
	public void setLocalDescription(String localDescription) {
		this.localDescription = localDescription;
	}
	ArrayList<CallListener> listeners;
	
	public Call(Request initialRequest,Dialog dialog){
		this.request=initialRequest;
		this.dialog=dialog;
		CallListener callListener=new VoiceListener();
		if (listeners == null) {
			listeners = new ArrayList<CallListener>();
		}
		listeners.add(callListener);
		}
	public String getState(){
		return callState;
	}
	public void setState(String state){
		oldState=this.callState;
		this.callState=state;
		fireStateChange(oldState);
		
	}
	// ---------------------Event---------------------------------
	public void addCallListener(CallListener callListener) {
		
		listeners.add(callListener);
	}

	public void setRemoteDescription(String remoteSdpData){
		this.remoteDescription=remoteSdpData;
		addCallListener(new MediaListener());
	}
	public String getRemoteDesString(){
		return remoteDescription;
	}
	public void fireStateChange(String oldState) {
		System.out.println("listenersµÄ´óÐ¡:"+listeners.size());
		CallEvent callEvent=new CallEvent(this,callState);
		callEvent.setOldCallState(oldState);
		if(listeners!=null){
			for(CallListener callListener:listeners){
				callListener.callEvent(callEvent);
			}
		}
	}
	public void setLocalPort(int localPort) {
		this.localPort=localPort;
	}
	public int getLocalPort(){
		System.out.println("localPort:"+localPort);
		return localPort;
	}
	public String getOldState() {
		return oldState;
	}
}

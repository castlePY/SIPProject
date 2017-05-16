package com.sip.control;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;

import javax.sdp.SessionDescription;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import com.sip.call.Call;
import com.sip.call.GUIListener;
import com.sip.domain.Command;
import com.sip.domain.UserInfo;
import com.sip.gui.ChatFrame;
import com.sip.gui.OptionFrame;
import com.sip.gui.TipFrame;
import com.sip.sdp.SessionDescriptionManager;
import com.sip.util.DateFormatUtil;
import com.sip.util.HashTableUtil;

public class UACManager {
	String to;
	SipProcessor sipProcessor;
	UserInfo userInfo;
	HeaderFactory headerFactory;
	SessionDescriptionManager sessionDescriptionManager;
	Call call;
	SessionDescription sessionDescription ;
	MessageProcessor messageProcessor;
	UASManager uasManager;
	UACManager(SipProcessor sipProcessor){
		this.sipProcessor=sipProcessor;
		userInfo=sipProcessor.getUserinfo();
		System.out.println("userInfo"+userInfo.getUsername());
		sessionDescriptionManager=sipProcessor.getSessionDescriptionManager();
		messageProcessor=sipProcessor.getMessageProcessor();
	}
	public Request initialRequest(String action,String message) {
		AddressFactory addressFactory=sipProcessor.getAddressFactory();
		this.headerFactory=sipProcessor.getHeaderFactory();
		MessageFactory messageFactory=sipProcessor.getMessageFactory();
		SipProvider sipProvider=sipProcessor.getSipProvider();
		String toUsername=to.substring(to.indexOf(':')+1,to.indexOf('@'));
		String address=to.substring(to.indexOf('@')+1);
		Request request=null;
		ContentTypeHeader contentTypeHeader;
		ContactHeader contactHeader=null;
		System.out.println("address"+address);
		try {
			SipURI toURI=addressFactory.createSipURI(toUsername, address);
			SipURI fromURI=addressFactory.createSipURI(userInfo.getUsername(),userInfo.getInetAddress().getHostAddress()+":"+userInfo.getPort());
			Address fromAddress=addressFactory.createAddress(fromURI);
			Address toAddress=addressFactory.createAddress(toURI);
			FromHeader fromHeader=headerFactory.createFromHeader(fromAddress, "haha1");
			ToHeader toHeader=headerFactory.createToHeader(toAddress, null);
			SipURI sipRequestURI=addressFactory.createSipURI(toUsername, address);
			sipRequestURI.setTransportParam("udp");
			ArrayList<ViaHeader> arrayList = new ArrayList<ViaHeader>();
			ViaHeader viaHeader=headerFactory.createViaHeader(userInfo .getInetAddress().getHostAddress(),userInfo.getPort(),"udp","branch1");
			arrayList.add(viaHeader);
			MaxForwardsHeader maxForwardsHeader=headerFactory.createMaxForwardsHeader(70);
			CallIdHeader callIdHeader=sipProvider.getNewCallId();
			SipURI contactUri=addressFactory.createSipURI(userInfo.getUsername(), userInfo.getInetAddress().getHostAddress()+":"+userInfo.getPort());
			Address contactAddress=addressFactory.createAddress(contactUri);
			contactHeader=headerFactory.createContactHeader(contactAddress);
			if(action.equals(Command.Text)){
				CSeqHeader cseqHeader=headerFactory.createCSeqHeader(1l, Request.MESSAGE);
				request=messageFactory.createRequest(sipRequestURI, Request.MESSAGE, callIdHeader, cseqHeader, fromHeader, toHeader, arrayList, maxForwardsHeader);
				contentTypeHeader=headerFactory.createContentTypeHeader("text","plain");
				request.setContent(message, contentTypeHeader);
				String title="我 "+DateFormatUtil.getDateTime();
				messageProcessor.processSendingMessage(title, Color.BLUE, true);
				messageProcessor.processSendingMessage(message, Color.BLACK, false);
				messageProcessor.clearSendingText();;
			}
			else if(action.equals(Command.CALL)){
				CSeqHeader cseqHeader=headerFactory.createCSeqHeader(1l, Request.INVITE);

				request=messageFactory.createRequest(sipRequestURI, Request.INVITE, callIdHeader, cseqHeader, fromHeader, toHeader, arrayList, maxForwardsHeader);

				contentTypeHeader=headerFactory.createContentTypeHeader("application","sdp");
				sessionDescription= sessionDescriptionManager.getSessionDescription(null, userInfo.getPort());
				request.setContent(sessionDescription.toString(), contentTypeHeader);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} 
	
		request.addHeader(contactHeader);
		return request;

	}

	public void processAction(String to, String action, String message) {
         if(action.equals(Command.CANCEL)){
		    processCancel();
		}
         else{
        	 SipProvider sipProvider=sipProcessor.getSipProvider();
     		this.to=to;
     		Request request=initialRequest(action,message);
     		
     		try {
     			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
     			if(request.getMethod().equals(Request.INVITE)){
     				Dialog dialog=clientTransaction.getDialog();
         			call=new Call(request,dialog);
         			call.addCallListener(new GUIListener((ChatFrame)messageProcessor));
         			uasManager.setCall(call);
         			HashTableUtil.put(call,dialog);
     			}
     			
     			clientTransaction.sendRequest();
     			
     		} catch (SipException e) {
     			e.printStackTrace();
     		} 
         }
		
}
	private void processCancel() {
		if(call==null){
			String message="电话目前处于空闲状态";
			new TipFrame(message);
			return ;
		}
		else{
			String message="是否确认挂断电话";
			OptionFrame optionFrame = new OptionFrame();
			optionFrame.showOptionFrame(message);
			if(optionFrame.isResult()){
				if(call.getState().equals(Call.CONNECTED)){
					processBye(HashTableUtil.getCallId(call));
				}
				else if(call.getState().equals(Call.RINGING)){
					processRealCancel(HashTableUtil.getCallId(call));
				}
				
			}	
		else{
			return;
		}
		}
			
			
	}
	private void processRealCancel(Dialog dialog) {
		if(dialog!=null){
			
			ClientTransaction firstTransaction =(ClientTransaction) dialog.getFirstTransaction();
			try {
				Request cancel = firstTransaction.createCancel();
				ClientTransaction clientTransaction = sipProcessor.getSipProvider().getNewClientTransaction(cancel);
				clientTransaction.sendRequest();
			} catch (SipException e) {
				e.printStackTrace();
			}
		}
	}
	private void processBye(Dialog dialog) {
		if(dialog!=null){
			
			try {
				Request bye=dialog.createRequest(Request.BYE);
				ClientTransaction clientTransaction = sipProcessor.getSipProvider().getNewClientTransaction(bye);
				dialog.sendRequest(clientTransaction);
			} catch (SipException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	public void processResponse(ResponseEvent arg0) {
		Response response=arg0.getResponse();
		int statusCode=response.getStatusCode();
		ClientTransaction clientTransaction=arg0.getClientTransaction();
		if(clientTransaction==null){
			System.out.println("错误:无事务状态");
		}
		String method=((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod();
		
		if(statusCode==Response.RINGING){
			if(method.equals(Request.INVITE)){
				System.out.println("call state"+method);
				call.setState(Call.RINGING);
			}
		}
		else if(statusCode==Response.OK){
			if(method.equals(Request.INVITE)){
				processInviteOk(arg0,clientTransaction);
			}
			else if(method.equals(Request.BYE)||method.equals(Request.CANCEL)){
				
				call.setState(Call.DISCONNECTED);
				System.out.println("oldState"+call.getOldState());
				call=null;
				uasManager.setCall(null);
				clearText();
				
			}
		}
		else if(statusCode>=300&&method.equals(Request.INVITE)){
			call.setState(Call.DISCONNECTED);
			call=null;
			uasManager.setCall(null);
			new TipFrame("对方正忙");
		}
		
		
	}
	private void processInviteOk(ResponseEvent arg0,ClientTransaction clientTransaction) {
	     try {
				System.out.println("200 ok received1");
				
			Request ack=clientTransaction.getDialog().createAck(((CSeqHeader)(arg0.getResponse().getHeader(CSeqHeader.NAME))).getSeqNumber());
			clientTransaction.getDialog().sendAck(ack);
			call.setRemoteDescription(new String(arg0.getResponse().getRawContent()));
//			call.setLocalDescription(sessionDescription.toString());
			System.out.println("UAClocalPort"+sessionDescriptionManager.getLocalPort());
			call.setLocalPort(sessionDescriptionManager.getLocalPort());
			System.out.println("200 ok received2");
			call.setState(Call.CONNECTED);
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
	     
		
	}
	public void setCall(Call call) {
		this.call=call;
	}
	public void setUASManager(UASManager uasManager) {
		this.uasManager=uasManager;
	}
	public void clearText(){
		messageProcessor.clearToText();
	}
	public void processWindowClose() {
		if(call==null){
			return;
		}
		else {
			processAction(to,Command.CANCEL,null);
		}
	}
}
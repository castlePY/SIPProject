package com.sip.control;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Hashtable;

import javax.sdp.SessionDescription;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import com.sip.call.Call;
import com.sip.call.GUIListener;
import com.sip.call.TimerListener;
import com.sip.domain.UserInfo;
import com.sip.gui.ChatFrame;
import com.sip.gui.OptionFrame;
import com.sip.gui.TipFrame;
import com.sip.sdp.SessionDescriptionManager;
import com.sip.util.DateFormatUtil;
import com.sip.util.HashTableUtil;

public class UASManager {
	SipProcessor sipProcessor;
	MessageProcessor messageProcessor;
	MessageFactory messageFactory;
	SipProvider sipProvider;
	SessionDescriptionManager sessionDescriptionManager;
	HeaderFactory headerFactory;
	UserInfo userInfo;
	FromHeader fromHeader;
	OptionFrame op;
	Hashtable<CallIdHeader, Call> hashtable=new Hashtable<CallIdHeader, Call>();
	Call call;
	UACManager uacManager;
	
	public UASManager(SipProcessor sipProcessor) {
		this.uacManager=sipProcessor.getUac();
		uacManager.setUASManager(this);
		this.userInfo=sipProcessor.getUserinfo();
		this.sipProcessor=sipProcessor;
		this.messageProcessor=sipProcessor.getMessageProcessor();
		this.messageFactory=sipProcessor.getMessageFactory();
		this.headerFactory=sipProcessor.getHeaderFactory();
		this.sipProvider=sipProcessor.getSipProvider();
		this.sessionDescriptionManager=sipProcessor.getSessionDescriptionManager();
	}
	public void processRequest(RequestEvent arg0){
		Request request=arg0.getRequest();
//		System.out.println("request"+request.toString());
		messageProcessor.showMessage(request.toString());
		String method=request.getMethod();
		ServerTransaction serverTransaction = arg0.getServerTransaction();
		if(serverTransaction==null){
			try {
				serverTransaction=((SipProvider)arg0.getSource()).getNewServerTransaction(request);
			} catch (TransactionAlreadyExistsException e) {
				e.printStackTrace();
			} catch (TransactionUnavailableException e) {
				e.printStackTrace();
			}
		}
		if(method.equals(Request.INVITE)){
			if(call==null){
				processInvite(serverTransaction,arg0);
			}
			else{
				if(call.getState().equals(Call.CONNECTED)){
					
					rejectBusyCall(serverTransaction,arg0);
				}
			}
		}
		else if(method.equals(Request.ACK)){
			System.out.println("ack received");
			processACK(serverTransaction,arg0);
		}
//		else if(method.equals(Request.MESSAGE)){
//			
//			messageProcessor.processMessage(fromHeader.getAddress().toString(),new String(request.getRawContent()));
//		}
		else if(method.equals(Request.MESSAGE)){
			if(fromHeader==null){
				fromHeader=(FromHeader) arg0.getRequest().getHeader(FromHeader.NAME);
			}
			String fromAddress=fromHeader.getAddress().toString();
			int index1=fromAddress.indexOf(':');
			int index2=fromAddress.indexOf('@');
			System.out.println(fromAddress.substring(index1+1,index2));
			String title=fromAddress.substring(index1+1,index2)+" "+DateFormatUtil.getDateTime() ;
			messageProcessor.processRecevieMessage(title,Color.RED,true);
			try {
				messageProcessor.processRecevieMessage(new String(request.getRawContent(),"utf-8"), Color.BLACK, false);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		else if(method.equals(Request.BYE)){
			processBye(serverTransaction,arg0);
		}
		else if(method.equals(Request.CANCEL)){
			processCancel(serverTransaction,arg0);
		}
//		else {
//			messageProcessor.processError("error method:"+method);
//			return;
//		}
		
		
	}
	private void rejectBusyCall(ServerTransaction serverTransaction, RequestEvent arg0) {
		Dialog d=serverTransaction.getDialog();
		try {
			Response response=messageFactory.createResponse(Response.BUSY_HERE, arg0.getRequest());
			setToTag(response, d);
			messageProcessor.showMessage(response.toString());
			serverTransaction.sendResponse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		
	}
	private void processCancel(ServerTransaction serverTransaction, RequestEvent arg0) {
		if(!serverTransaction.getDialog().getFirstTransaction().getRequest().getMethod().equals(Request.INVITE)){
			return;
		}
		System.out.println("服务器端处理Cancel");
		call.setState(Call.DISCONNECTED);
		call.autoCancel=false;
		op.getDialog().setVisible(false);
		op.getDialog().dispose();
		call=null;
		uacManager.setCall(null);
		try { 
			Response cancel=messageFactory.createResponse(Response.OK ,arg0.getRequest());
			setToTag(cancel, arg0.getDialog());
			messageProcessor.showMessage(cancel.toString());
			serverTransaction.sendResponse(cancel);
			clearText();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		new TipFrame("对方挂断了电话");
		
	}
	private void processBye(ServerTransaction serverTransaction, RequestEvent arg0) {
		Request byeRequest=arg0.getRequest();
		try {
			System.out.println(call.getOldState());
			call.setState(Call.DISCONNECTED);
			call=null;
			uacManager.setCall(null);
			Response byeOK=messageFactory.createResponse(Response.OK, byeRequest);
			setToTag(byeOK,serverTransaction.getDialog() );
			messageProcessor.showMessage(byeOK.toString());
			serverTransaction.sendResponse(byeOK);
			clearText();
			new TipFrame("对方挂断了电话");
			System.out.println("发送确认关闭响应");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		
	}
	private void processACK(ServerTransaction serverTransaction, RequestEvent arg0) {
		Call call=hashtable.get(serverTransaction.getDialog().getCallId());
		String to=fromHeader.getAddress().toString();
		sipProcessor.setDestination(to.substring(1,to.length()-1));
//		call.addCallListener(new TimerListener());
		call.setState(Call.CONNECTED);
		
	}
	public void processInvite(ServerTransaction serverTransaction,RequestEvent arg0){
		Request request=arg0.getRequest();
		Dialog dialog=serverTransaction.getDialog();
		fromHeader=(FromHeader) request.getHeader(FromHeader.NAME);
		
		call=new Call(request,dialog);
		uacManager.setCall(call);
		call.addCallListener(new GUIListener((ChatFrame)messageProcessor));
		System.out.println("haha");
		call.setState(Call.ALERTING);
		System.out.println("xixi");
		System.out.println(arg0.getRequest());
		System.out.println(arg0.getRequest().getRawContent());
		String str=new String(arg0.getRequest().getRawContent());
		System.out.println("str"+str);
		call.setRemoteDescription(new String(arg0.getRequest().getRawContent()));
		System.out.println("processing invite");
		hashtable.put(dialog.getCallId(), call);
		HashTableUtil.put(call,dialog);
		try {
			Response response=messageFactory.createResponse(Response.RINGING,request);
			setToTag(response,dialog);
			messageProcessor.showMessage(response.toString());
			serverTransaction.sendResponse(response);
			
			new Thread(new Runnable(){

				@Override
				public void run() {
					op=new OptionFrame();
					op.showOptionFrame(fromHeader.getAddress().toString()+"邀请你语音聊天");
					if(op.isResult()){
						answerCall(serverTransaction,arg0);
					}
					else if(call.autoCancel){
						System.out.println("自己挂断电话");
						rejectCall(serverTransaction,arg0);
					}
				}
				
			}).start();
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected void rejectCall(ServerTransaction serverTransaction, RequestEvent arg0) {
		Dialog dialog=serverTransaction.getDialog();
		Response response;
		try {
			response = messageFactory.createResponse(Response.BUSY_HERE,arg0.getRequest());
			setToTag(response,dialog);
			messageProcessor.showMessage(response.toString());
			serverTransaction.sendResponse(response);
			call.setState(Call.DISCONNECTED);
			setCall(null);
			uacManager.setCall(null);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		
	}
	private void answerCall(ServerTransaction serverTransaction, RequestEvent arg0) {
		try {
			System.out.println("answeringCall");
			Dialog dialog=serverTransaction.getDialog();
			Response response=messageFactory.createResponse(Response.OK,arg0.getRequest());
			setToTag(response,dialog);
			ContentTypeHeader contentTypeHeader=headerFactory.createContentTypeHeader("sdp", "application");
			SessionDescription sessionDescription = sessionDescriptionManager.getSessionDescription(null, 6070);
			call.setLocalDescription(sessionDescription.toString());
			call.setLocalPort(sessionDescriptionManager.getLocalPort());
			response.setContent(sessionDescription, contentTypeHeader);
			response.addHeader(getContactHeader());
			messageProcessor.showMessage(response.toString());
			serverTransaction.sendResponse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
	}
	private ContactHeader getContactHeader(){
		AddressFactory addressFactory = sipProcessor.getAddressFactory();
		SipURI contactURI=null;
		try {
			contactURI = addressFactory.createSipURI(userInfo.getUsername(), userInfo.getInetAddress().getHostAddress()+":"+userInfo.getPort());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Address contactAddress=addressFactory.createAddress(contactURI);
		ContactHeader contactHeader=headerFactory.createContactHeader(contactAddress);
		return contactHeader;
	}
	private void setToTag(Response response, Dialog dialog) {
		ToHeader to=(ToHeader) response.getHeader(ToHeader.NAME);
		if(to!=null){
			String tag=to.getTag();
			if(tag==null||tag.trim().length()==0){
				int temp=(int) ((dialog!=null)?dialog.hashCode():System.currentTimeMillis());
				try {
					to.setTag(Integer.toString(temp));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void setCall(Call call) {
		this.call=call;
	}
	public void clearText(){
		messageProcessor.clearToText();
	}
}

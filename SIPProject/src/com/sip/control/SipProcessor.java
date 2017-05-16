package com.sip.control;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
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

import com.sip.domain.Command;
import com.sip.domain.UserInfo;
import com.sip.gui.ChatFrame;
import com.sip.gui.OptionFrame;
import com.sip.sdp.SessionDescriptionManager;

public class SipProcessor implements SipListener{
	private MessageProcessor messageProcessor;
	private SipStack sipStack;
	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private MessageFactory messageFactory;
	private SipProvider sipProvider;
	UserInfo userinfo;
	UACManager uac;
	UASManager uas;
//	ChatFrame chat;
	SessionDescriptionManager sessionDescriptionManager;
	public SipProcessor(UserInfo userinfo,MessageProcessor messageProcessor){
		this.messageProcessor=messageProcessor;
		this.userinfo=userinfo;
		String username=userinfo.getUsername();
		sipFactory=SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties pro=new Properties();
		pro.setProperty("javax.sip.STACK_NAME", "ChatFrame");
		String ip=userinfo.getInetAddress().getHostAddress();
		int port=userinfo.getPort();
//		pro.setProperty("javax.sip.IP_ADDRESS", ip);
//		pro.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
//		pro.setProperty("gov.nist.javax.sip.SERVER_LOG",
//			"textclient.txt");
//		pro.setProperty("gov.nist.javax.sip.DEBUG_LOG",
//			"textclientdebug.log");

		try {
			sipStack=sipFactory.createSipStack(pro);
			addressFactory=sipFactory.createAddressFactory();
			headerFactory=sipFactory.createHeaderFactory();
			messageFactory=sipFactory.createMessageFactory();
			System.out.println("ip"+userinfo.getInetAddress().getLocalHost());
			ListeningPoint tcp=sipStack.createListeningPoint(ip,Integer.valueOf(port),"tcp");
			ListeningPoint udp=sipStack.createListeningPoint(ip,Integer.valueOf(port),"udp");
			sipProvider=sipStack.createSipProvider(tcp);
			sipProvider.addSipListener(this);
			sipProvider=sipStack.createSipProvider(udp);
			sipProvider.addSipListener(this);
			this.sessionDescriptionManager = new SessionDescriptionManager(this);
			
			uac=new UACManager(this);
			uas=new UASManager(this);
			
			this.setSessionDescriptionManager(sessionDescriptionManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SessionDescriptionManager getSessionDescriptionManager() {
		return sessionDescriptionManager;
	}

	public void setSessionDescriptionManager(SessionDescriptionManager sessionDescriptionManager) {
		this.sessionDescriptionManager = sessionDescriptionManager;
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		
	}

	@Override
	public void processRequest(RequestEvent arg0) {
		System.out.println("recieved");
		uas.processRequest(arg0);
		
	}

	@Override
	public void processResponse(ResponseEvent arg0) {
//		Response response=arg0.getResponse();
//		int status=response.getStatusCode();
//		if(status>=200&&status<300){
//			messageProcessor.processInfo("sent");
//		}
//		else{
//			messageProcessor.processInfo(" sending message failed!"+status);
//		}
		uac.processResponse(arg0);
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		messageProcessor.processError("error:timeout");
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		
	}

	public void setMessageProcessor(MessageProcessor messageProcessor) {
		this.messageProcessor=messageProcessor;
	}

	public void sendMessage(String to,String message) {
		String toUsername=to.substring(to.indexOf(':')+1,to.indexOf('@'));
		String address=to.substring(to.indexOf('@')+1);
		System.out.println("address"+address);
		try {
			SipURI toURI=addressFactory.createSipURI(toUsername, address);
			SipURI fromURI=addressFactory.createSipURI(userinfo.getUsername(),getHost()+":"+getPort());
			Address fromAddress=addressFactory.createAddress(fromURI);
			Address toAddress=addressFactory.createAddress(toURI);
			FromHeader fromHeader=headerFactory.createFromHeader(fromAddress, "haha1");
			ToHeader toHeader=headerFactory.createToHeader(toAddress, null);
			SipURI sipRequestURI=addressFactory.createSipURI(toUsername, address);
			sipRequestURI.setTransportParam("udp");
			ArrayList<ViaHeader> arrayList = new ArrayList<ViaHeader>();
			ViaHeader viaHeader=headerFactory.createViaHeader(getHost(),getPort(),"udp","branch1");
			arrayList.add(viaHeader);
			CSeqHeader cseqHeader=headerFactory.createCSeqHeader(1l, Request.MESSAGE);
			MaxForwardsHeader maxForwardsHeader=headerFactory.createMaxForwardsHeader(70);
			CallIdHeader callIdHeader=sipProvider.getNewCallId();
			Request request=messageFactory.createRequest(sipRequestURI, Request.MESSAGE, callIdHeader, cseqHeader, fromHeader, toHeader, arrayList, maxForwardsHeader);
			SipURI sipURI=addressFactory.createSipURI(userinfo.getUsername(), getHost());
			sipURI.setPort(getPort());
			System.out.println();
			Address contactAddress=addressFactory.createAddress(sipRequestURI);
			ContactHeader contactHeader=headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);
			ContentTypeHeader contentTypeHeader=headerFactory.createContentTypeHeader("application","sdp");
			request.setContent(message, contentTypeHeader);
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
			sipProvider.sendRequest(request);
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}
		
	}
	public String getHost(){
		return sipProvider.getListeningPoint("udp").getIPAddress();
	}
	public int getPort(){
		return sipProvider.getListeningPoint("udp").getPort();
	}
	
	public void processAction(String to,String action,String message){
		
		uac.processAction( to, action, message);
	}

	public SipStack getSipStack() {
		return sipStack;
	}

	public void setSipStack(SipStack sipStack) {
		this.sipStack = sipStack;
	}

	public SipFactory getSipFactory() {
		return sipFactory;
	}

	public void setSipFactory(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	public AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public void setAddressFactory(AddressFactory addressFactory) {
		this.addressFactory = addressFactory;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public void setHeaderFactory(HeaderFactory headerFactory) {
		this.headerFactory = headerFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public SipProvider getSipProvider() {
		return sipProvider;
	}

	public void setSipProvider(SipProvider sipProvider) {
		this.sipProvider = sipProvider;
	}

	public UACManager getUac() {
		return uac;
	}

	public void setUac(UACManager uac) {
		this.uac = uac;
	}

	public UASManager getUas() {
		return uas;
	}

	public void setUas(UASManager uas) {
		this.uas = uas;
	}

	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public UserInfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(UserInfo userinfo) {
		this.userinfo = userinfo;
	}

	public void setDestination(String to) {
		messageProcessor.setDesdination(to);
	}

	public void appendSendingMessage(String message) {
		
		
	}

	public void processNewPort(int port) {
		sessionDescriptionManager.setLocalPort(port);
	}

	public  void processWindowClose() {
		uac.processWindowClose();
	}

	
}

package com.sip.sdp;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.TimeDescription;
import javax.sdp.Version;

import com.sip.control.SipProcessor;


public class SessionDescriptionManager {
	SipProcessor sipProcessor;
	String[] formats;
	SdpFactory sdpFactory;
	int localPort=8024;
	public void setLocalPort(int localPort){
		this.localPort=localPort;
	}
	public int getLocalPort(){
		return localPort;
	}
	public SessionDescriptionManager(SipProcessor sipProcessor ){
		formats=new String[]{Integer.toString(SdpConstants.PCMA),Integer.toString(SdpConstants.PCMU)};
		this.sipProcessor=sipProcessor;
		try {
			sdpFactory= SdpFactory.getInstance();
		} catch (SdpException e) {
			e.printStackTrace();
		}
	}
	public SessionDescription getSessionDescription(SessionDescription offer,int localPort){
		
		System.out.println("sdpFactory:"+sdpFactory);
		SessionDescription sessionDescription=null;
		System.out.println("sessionDescription:"+sessionDescription);

		try {
			System.out.println("sessionDescription2:"+sessionDescription);

			sessionDescription=sdpFactory.createSessionDescription();
			System.out.println("sessionDescription3:"+sessionDescription);

			Version v=sdpFactory.createVersion(0);
			InetAddress inetAddress=sipProcessor.getUserinfo().getInetAddress();
			String ipType="IP4";
			if(inetAddress instanceof Inet6Address){
				ipType="IP6";
				System.out.println("ipType:"+ipType);
			}
			System.out.println("ipType:"+ipType);

			Origin o=sdpFactory.createOrigin("user1", 0, 0, "IN", ipType, inetAddress.getHostAddress());
			SessionName s=sdpFactory.createSessionName("-");
			Connection c=sdpFactory.createConnection("IN", ipType, inetAddress.getHostAddress());
			TimeDescription timeDescription = sdpFactory.createTimeDescription();
			Vector<TimeDescription> t = new Vector<TimeDescription>();
			t.add(timeDescription);
			MediaDescription mediaDescription = sdpFactory.createMediaDescription("audio",getLocalPort(), 1, "RTP/AVP", formats);
			Vector<MediaDescription> m = new Vector<MediaDescription>();
			m.add(mediaDescription);
			sessionDescription.setVersion(v);
			sessionDescription.setOrigin(o);
			sessionDescription.setSessionName(s);
			sessionDescription.setConnection(c);
			sessionDescription.setTimeDescriptions(t);
			sessionDescription.setMediaDescriptions(m);
		} catch (SdpException e) {
			System.out.println("“Ï≥£");
			e.printStackTrace();
		}
		return sessionDescription;
	}
	public SdpFactory getSdpFactory() {
		return sdpFactory;
	}
	public void setSdpFactory(SdpFactory sdpFactory) {
		this.sdpFactory = sdpFactory;
	}
	public String parseData(String sdpData) {
//		StringBuffer buffer = new StringBuffer();
		SessionDescription sdp=null;
		System.out.println("sdpData"+sdpData);
		try {
			sdp=sdpFactory.createSessionDescription(sdpData);
//			Vector<MediaDescription> mediaDescriptions = sdp.getMediaDescriptions(true);
//			for(MediaDescription mediaDescription:mediaDescriptions){
//				Media media=mediaDescription.getMedia();
//				String mediaType=media.getMediaType();
//				String port=
//			}
		} catch (SdpParseException e) {
			System.out.println("≥ˆ¥Ì¡À");
			e.printStackTrace();
		} 
		return sdp.toString();
	}
}

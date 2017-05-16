package com.sip.sdp;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.TimeDescription;
import javax.sdp.Version;

public class SDPTest {

	public static void main(String[] args) {
		
			String[] formats=new String[]{Integer.toString(SdpConstants.PCMA),Integer.toString(SdpConstants.PCMU)};
			int localPort=8087;
			SdpFactory sdpFactory;
			try {
				sdpFactory = SdpFactory.getInstance();
				System.out.println("sdpFactory:"+sdpFactory);
				SessionDescription sessionDescription=null;
				System.out.println("sessionDescription:"+sessionDescription);

				sessionDescription=sdpFactory.createSessionDescription();
				System.out.println("sessionDescription3:"+sessionDescription);

				Version v=sdpFactory.createVersion(0);
				InetAddress inetAddress;
				
					inetAddress = InetAddress.getLocalHost();
					String ipType="IP4";
					if(inetAddress instanceof Inet6Address){
						ipType="IP6";
					}
					System.out.println("ipType:"+ipType);

					Origin o=sdpFactory.createOrigin("user1", 0, 0, "IN", ipType, inetAddress.getHostAddress());
					SessionName s=sdpFactory.createSessionName("-");
					Connection c=sdpFactory.createConnection("IN", ipType, inetAddress.getHostAddress());
					TimeDescription timeDescription = sdpFactory.createTimeDescription();
					Vector<TimeDescription> t = new Vector<TimeDescription>();
					t.add(timeDescription);
					MediaDescription mediaDescription = sdpFactory.createMediaDescription("audio",localPort, 1, "RTP/AVP", formats);
					Vector<MediaDescription> m = new Vector<MediaDescription>();
					m.add(mediaDescription);
					sessionDescription.setVersion(v);
					sessionDescription.setOrigin(o);
					sessionDescription.setSessionName(s);
					sessionDescription.setConnection(c);
					sessionDescription.setTimeDescriptions(t);
					sessionDescription.setMediaDescriptions(m);
					System.out.println("sessionDescription:"+sessionDescription.toString());
                     SdpFactory instance = SdpFactory.getInstance();
					SessionDescription sessionDescription2 = instance.createSessionDescription(sessionDescription.toString());
					System.out.println("sessionDescription2:"+sessionDescription2.toString());
			} catch (SdpException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			
			
		
		
	}}
















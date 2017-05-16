package com.sip.media;

import java.util.ArrayList;
import java.util.Vector;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;

import com.sip.call.Call;

public class MediaManager {
	Call call;
	JMFSender jmfSender;
	JMFReceiver jmfReceiver;
	public MediaManager(Call call) {
		this.call=call;
		openStream();
	}
	private void openStream() {
		try {
			SdpFactory factory=SdpFactory.getInstance();
			javax.sdp.SessionDescription sessionDescription = factory.createSessionDescription(call.getRemoteDesString());
			Connection con=sessionDescription.getConnection();
			if(con!=null){
				
				String ipAddress=con.getAddress();
			}
			Vector<MediaDescription> mediaDescriptions = sessionDescription.getMediaDescriptions(true);
			ArrayList mediaTypeList=new ArrayList();
			ArrayList portList=new ArrayList();
			ArrayList addressList=new ArrayList();
			ArrayList formatList=new ArrayList();
			ArrayList mediaFormats = null;
			if(mediaDescriptions!=null){
				for(MediaDescription mediaDescription:mediaDescriptions){
					Media media = mediaDescription.getMedia();
					int mediaPort=media.getMediaPort();
					String mediaType=media.getMediaType();
					Vector sdpFormats = media.getMediaFormats(true);
					mediaFormats=changeToMediaFormat(sdpFormats);
					String remoteAddress;
					Connection mediaCon=mediaDescription.getConnection();
					if(mediaCon!=null){
						remoteAddress=mediaCon.getAddress();
					}
					else{
						if(con!=null){
							remoteAddress=con.getAddress();
						}
						else{
							throw new RuntimeException("conÎª¿Õ");
						}
					}
					if(isCorrectMediaType(mediaType)){
						mediaTypeList.add(media);
						portList.add(mediaPort);
						addressList.add(remoteAddress);
						mediaFormats.add(mediaFormats);
					}
					else{
						continue;
					}
				}
			}
			startTransmitter(addressList,portList,mediaFormats);
			startRecevier(mediaTypeList,addressList,portList);
		} catch (SdpParseException e) {
			e.printStackTrace();
		} catch (SdpException e) {
			e.printStackTrace();
		}
		
	}
	
	private void startRecevier(ArrayList mediaTypeList, ArrayList addressList,ArrayList portList) {
		jmfReceiver = new JMFReceiver(mediaTypeList,addressList,portList);
		jmfReceiver.setPort(call.getLocalPort());
		jmfReceiver.start();
	}
	private void startTransmitter(ArrayList addressList, ArrayList portList, ArrayList mediaFormats) {
		jmfSender=new JMFSender(addressList,portList,mediaFormats);
		jmfSender.startTransmitter();
	}
	private ArrayList changeToMediaFormat(Vector sdpFormats) {
		ArrayList array=new ArrayList();
		for(int i=0;i<sdpFormats.size();i++){
			String format=(String) sdpFormats.get(i);
			String str=findCorrespondFormat(format);
			if(str!=null){
				array.add(str);
			}
		}
		return array;
		
	}
	private String findCorrespondFormat(String format) {
		switch(Integer.parseInt(format)){
		case SdpConstants.PCMU:
			 return AudioFormat.ULAW_RTP;
		 case SdpConstants.PCMA:
             return AudioFormat.ALAW;
		case SdpConstants.GSM:
			return AudioFormat.GSM_RTP;
		case SdpConstants.JPEG:
			return VideoFormat.JPEG_RTP;
		default:return null;
		}
		
	}
	private boolean isCorrectMediaType(String mediaType) {
		if(mediaType.equalsIgnoreCase("audio")||mediaType.equalsIgnoreCase("video")){
			return true;
		}
		else{
			return false;
		}
	}
	public void stop() {
		
		jmfReceiver.stopRecevie();
		jmfSender.stopTransmitter();
	}
	

}

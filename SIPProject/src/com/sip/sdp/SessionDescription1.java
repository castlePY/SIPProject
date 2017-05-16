package com.sip.sdp;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;

public class SessionDescription1 {
	/*private long id;
	private long version;
	private String name;
	private String username;
//	private List code;
	private InetAddress inetAddress;
	private Hashtable<String,String> attributes;
	private List<MediaDescription> mediaDescriptions;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Hashtable<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Hashtable<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<MediaDescription> getMediaDescriptions() {
		return mediaDescriptions;
	}

	public void setMediaDescriptions(List<MediaDescription> mediaDescriptions) {
		this.mediaDescriptions = mediaDescriptions;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		//v是sdp版本号
		sb.append("v=0/r/n");
		//o是包含会话id，ip版本号和本地ip信息
		sb.append("o=").append(username).append(" ").append(id).append(" ").append(version);
		int ipversion;
		if(inetAddress instanceof Inet4Address){
			ipversion=4;
		}
		else if(inetAddress instanceof Inet6Address){
			ipversion=6;
		}
		else{
			throw new RuntimeException("unknown host"+inetAddress);
		}
		sb.append(" IN IP").append(ipversion).append(" ");
		String ip=inetAddress.getHostAddress();
		sb.append(ip).append("/r/n");
		//s表示会话主题
		sb.append("s=").append(name).append("/r/n");
		//c用于表示本端用于传输媒体流的ip
		sb.append("c=").append("IN IP").append(ipversion).append(" ").append(ip).append("/r/n");
		//会话时间，一般由其它信令控制，所以填0
		sb.append("t=0 0/r/n");
		if(attributes!=null){
			for(String name:attributes.keySet()){
				String value=attributes.get(name);
				sb.append("a=").append(name);
				if(value!=null&&!"".equals(value.trim())){
					sb.append(":").append(value).append("/r/n");
				}
			}
		}
		if(mediaDescriptions!=null){
			for(MediaDescription media:mediaDescriptions){
				sb.append(media.toString());
			}
		}
		
	}*/

}

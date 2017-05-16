package com.sip.domain;

import java.net.InetAddress;

public class UserInfo {
private String username;
private int port;
private InetAddress inetAddress;
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public int getPort() {
	return port;
}
public void setPort(int port) {
	this.port = port;
}
public InetAddress getInetAddress() {
	return inetAddress;
}
public void setInetAddress(InetAddress inetAddress) {
	this.inetAddress = inetAddress;
}

}

package com.sip.control;

import java.awt.Color;

public interface MessageProcessor {
 
public void processRecevieMessage(String str,Color color,boolean bold);
public void processError(String error);
public void processInfo(String info);
public void setDesdination(String to);
public void processSendingMessage(String str,Color color,boolean bold);
public void clearSendingText();
public void clearToText();
 
}

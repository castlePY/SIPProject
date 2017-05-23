package com.sip.call;

import java.util.Timer;
import java.util.TimerTask;

import com.sip.gui.ChatFrame;
import com.sip.gui.TipFrame;
import com.sip.util.HashTableUtil;

public class GUIListener implements CallListener{
	ChatFrame chat;
	Timer timer;
	Call call;
	Task task;
	public GUIListener(ChatFrame chat) {
		this.chat=chat;
	}
	@Override
	public void callEvent(CallEvent event) {
		System.out.println("����GUIListener��");
		call=event.getSource();
		if(call.getOldState()!=call.getState()){
			if(!call.getState().equals(Call.DISCONNECTED)){
				chat.getRight_jb2().setEnabled(false);
				
			}
			else{
				chat.getRight_jb2().setEnabled(true);
				chat.getMyLabel().stopTimer();
				if(timer!=null){
					System.out.println("canceling");
					timer.cancel();
					
				}
				}
			if(call.getState().equals(Call.CONNECTED)||call.getState().equals(Call.RINGING)){
				chat.getRight_jb3().setEnabled(true);
			}
			else{
				chat.getRight_jb3().setEnabled(false);
			}
			
			if(call.getState().equals(Call.CONNECTED)){
				if(call.getOldState().equals(Call.RINGING)){
					
					chat.getMyLabel().stopTimer();
					timer.cancel();
//					task.cancel();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("ֹͣ��ʱ");
				}
				System.out.println("��ʼ��ʱ");
				chat.getMyLabel().setVisible(true);
				chat.getMyLabel().startTimer();
			}
			else if(call.getState().equals(Call.RINGING)){
				System.out.println("ִ��������");
				timer=new Timer();
				task=new Task();
				timer.schedule(task,60*1000);
				chat.getMyLabel().startTimer();
				chat.getMyLabel().setVisible(true);
			}
			
		}
		
		
	}
	class Task extends TimerTask{

		@Override
		public void run() {
			if(call!=null){
				System.out.println("ִ��һ��");
				timer.cancel();
				chat.sipProcessor.getUac().processRealCancel(HashTableUtil.getCallId(call));
				chat.getMyLabel().stopTimer();
				new TipFrame("ʱ�䳬ʱ");
			}
		}
		
	}
	
}

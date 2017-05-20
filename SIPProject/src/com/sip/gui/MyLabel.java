package com.sip.gui;
import java.text.DecimalFormat;

import javax.swing.JLabel;

public class MyLabel extends JLabel{
	
	boolean flag=false;
	DecimalFormat df;
	int ps=0;
	int second=0;
	int min=0;
	int hour=0;
	public void startTimer(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				df=new DecimalFormat("00");
				flag=true;
				while(flag){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ps++;
					if(ps>=100){
						ps=0;
						second++;
						if(second>=60){
							second=0;
							min++;
							if(min>=60){
								min=0;
								hour++;
							}
						}
						if(hour<10){
							
							MyLabel.this.setText(df.format(hour)+":"+df.format(min)+":"+df.format(second));
						}
						else{
							MyLabel.this.setText(hour+":"+df.format(min)+":"+df.format(second));
						}
					}
				}
				
				
			}
			
		}).start();
		
	}
	public void stopTimer(){
		flag=false;
		MyLabel.this.setVisible(false);
//		new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				flag=false;
//				MyLabel.this.setVisible(false);
//				
//			}
//			
//		});
//	}
}
}
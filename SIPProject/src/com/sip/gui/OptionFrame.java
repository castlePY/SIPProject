package com.sip.gui;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class OptionFrame {
	boolean result;

	JDialog jDialog;

	public OptionFrame(){
		
	}
	public void showOptionFrame(String message) {
		
		JOptionPane j=new JOptionPane();
		j.setMessage(message);
		j.setMessageType(JOptionPane.QUESTION_MESSAGE);
		j.setOptionType(JOptionPane.YES_NO_OPTION);
		JDialog dialog = j.createDialog(message);
		this.jDialog=dialog;
		System.out.println("jDialog1"+jDialog);
		dialog.setTitle("ÌבÊ¾");
		dialog.setVisible(true);
		
		if(j.getValue()!=null&&j.getValue() instanceof Integer){
			int i=(Integer)j.getValue();
			if(i==JOptionPane.YES_OPTION){
				setResult(true);
			}
		}
		
		
	}

	public boolean isResult() {
		return result;

	}
	public JDialog getDialog(){
		return jDialog;
	}
	public void setResult(boolean result) {
		this.result = result;
	}

}

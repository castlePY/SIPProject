package com.sip.gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class MessageFrame extends JFrame {

	private JPanel contentPane;
	JScrollPane js;
	JTextArea jt;
	
	public JTextArea getJt() {
		return jt;
		
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageFrame frame = new MessageFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MessageFrame() {
		
		jt=new JTextArea();
		jt.setColumns(20);
		jt.setFont(new Font("TimesRoman",Font.PLAIN,20));
		jt.setLineWrap(true);
		System.out.println("width:"+jt.getWidth());
		System.out.println(jt.getColumns());
		js=new JScrollPane(jt);
		getContentPane().add(js);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 589, 403);
		this.setResizable(false);
		this.setVisible(false);
	
		
	}

	

}

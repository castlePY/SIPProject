package com.sip.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.sip.control.SipProcessor;
import com.sip.domain.UserInfo;

public class LoginFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private JFrame this;
	private JTextField textField;
	private JTextField textField_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e)
						{
						System.exit(0);	
					}
							 
						});
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the this.
	 */
	private void initialize() {

		this.setIconImage((new ImageIcon("resource/QQ.png")).getImage());
		this.setBounds(100, 100, 303, 319);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("username:");
		lblNewLabel.setBounds(33, 85, 64, 15);
		this.getContentPane().add(lblNewLabel);
//		this.getContentPane().setBackground(new Color(245, 255, 251));
		this.getContentPane().setBackground(new Color(185, 220, 237));

		textField = new JTextField();
		textField.setBounds(107, 82, 97, 21);
		this.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblPort = new JLabel("port:");
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setVerticalAlignment(SwingConstants.TOP);
		lblPort.setBounds(33, 134, 54, 15);
		this.getContentPane().add(lblPort);

		textField_1 = new JTextField();
		textField_1.setBounds(107, 131, 97, 21);
		this.getContentPane().add(textField_1);
		textField_1.setColumns(10);

		JButton btnNewButton = new JButton("Sign In");
		btnNewButton.setBounds(91, 185, 93, 23);
		this.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);
		int width=(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height=(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int x=(width-this.getWidth())/2;
		int y=(height-this.getHeight())/2;
		this.setLocation(x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UserInfo userInfo = new UserInfo();
		String username = textField.getText();
		String port = textField_1.getText();
		userInfo.setUsername(username);
		userInfo.setPort(Integer.valueOf(port));
		new ChatFrame(userInfo);
		this.dispose();
	}
}

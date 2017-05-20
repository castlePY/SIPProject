package com.sip.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.sip.control.MessageProcessor;
import com.sip.control.SipProcessor;
import com.sip.domain.Command;
import com.sip.domain.UserInfo;

public class ChatFrame extends JFrame implements ActionListener, MessageProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JSplitPane splitPane = new JSplitPane();
	private JTextPane textPane;
	JTextField right_jtf;
	JScrollPane right_js;
	JButton right_jb1, right_jb2, right_jb3;
	JPanel right_jp1, right_jp2, right_jp3, right_jp4,right_jp5;
	private JTextField textField_1;
	public SipProcessor sipProcessor;
	private JTextField textField;
	private StyledDocument doc=null;
	JMenuBar jm;
	MyLabel myLabel;
	
	/**
	 * Launch the application.
	 */

	public MyLabel getMyLabel() {
		return myLabel;
	}

	/**
	 * Create the application.
	 */
	public ChatFrame(UserInfo userinfo) {
		getContentPane().setForeground(Color.RED);
		try {
			userinfo.setInetAddress(InetAddress.getLocalHost());
			// String ip=.getHostAddress();
			sipProcessor = new SipProcessor(userinfo, this);
			initialize(userinfo);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					sipProcessor.processWindowClose();
					System.exit(0);
				}
			});

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialize the contents of the this.
	 */
	private void initialize(UserInfo userinfo) {
		this.setIconImage(new ImageIcon("resource/QQ.png").getImage());
		jm=new JMenuBar();
		JMenu jmenu1=new JMenu("设置(S)");
		JMenuItem jmenuItem=new JMenuItem("修改音频端口");
		jmenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String str=InputFrame.showInputFrame("请输入新的音频端口");
				if(str!=null){
					int port=Integer.valueOf(str);
					sipProcessor.processNewPort(port);
				}
				
			}
		});
		jmenu1.add(jmenuItem);
		
		jmenu1.setMnemonic(KeyEvent.VK_S);
		jmenu1.addActionListener(this
			
			);
		JMenu jmenu2=new JMenu("关于");
		jm.add(jmenu1);
		jm.add(jmenu2);
		this.setJMenuBar(jm);
		
		String username = userinfo.getUsername();
		int port = userinfo.getPort();
		this.setBounds(100, 100, 636, 426);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splitPane.setForeground(Color.RED);
//		splitPane.setResizeWeight(0.3);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(185, 220, 237));
		panel.setForeground(Color.RED);
		splitPane.setLeftComponent(panel);
		panel.setLayout(null);

		JLabel lblPort = new JLabel("from:");
		lblPort.setFont(new Font("宋体", Font.PLAIN, 13));
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setBounds(10, 134, 54, 15);
		panel.add(lblPort);

		JLabel lblTo = new JLabel("to:");
		lblTo.setFont(new Font("宋体", Font.PLAIN, 13));
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setBounds(10, 159, 54, 15);
		panel.add(lblTo);

		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.LEFT);
		textField_1.setBounds(73, 131, 203, 21);
		textField_1.setEditable(false);// 不可编辑
		textField_1.setBackground(Color.WHITE);
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String text = "sip:" + username + "@" + ip + ":" + port;
		textField_1.setText(text);
		panel.add(textField_1);
		
		textField = new JTextField();
		textField.setBounds(74, 159, 202, 21);
		panel.add(textField);
		textField.setColumns(10);
		textPane=new JTextPane();
		doc=textPane.getStyledDocument();
		right_js = new JScrollPane(textPane);
		right_jtf = new JTextField(15);
		right_jtf.setPreferredSize(new Dimension(20, 30));// 设置JTextField的宽度和高度
		right_jtf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					String command = Command.Text;
					String to = textField.getText();
					String message = right_jtf.getText();
					sipProcessor.processAction(to, command, message);
				}
			}
		});
		right_jb1 = new JButton("发送");
		right_jb1.setActionCommand(Command.Text);
		right_jb1.getSize();
		right_jb1.addActionListener(this);
		right_jp1 = new JPanel();
		right_jp1.setBackground(new Color(185, 220, 237));

//		right_jp1.setBackground(new Color(102, 205, 170));
		right_jp1.add(right_jtf);
		right_jp1.add(right_jb1);
		right_jb2 = new JButton(new ImageIcon("resource/call.png"));
		// right_jb2.setIcon();
		// right_jb2.setOpaque(false);
		right_jb2.setBorderPainted(false);
		right_jb2.setContentAreaFilled(false);
		right_jb2.setActionCommand(Command.CALL);
		right_jb2.addActionListener(this

		);
		right_jb3 = new JButton(new ImageIcon("resource/hangup.png"));
		right_jb3.setBorderPainted(false);
		right_jb3.setContentAreaFilled(false);
		right_jb3.setActionCommand(Command.CANCEL);
		right_jb3.setEnabled(false);
		right_jb3.addActionListener(this);
		// right_jb3.setOpaque(false);
		//计时器部分
		myLabel=new MyLabel();
		myLabel.setBounds(5, 5, 82, 31);
		myLabel.setText("00:00:00");
        myLabel.setBorder(new LineBorder(Color.BLACK));
        myLabel.setFont(new Font("TimesRoman",Font.BOLD,20));
        myLabel.setHorizontalAlignment(0);
        myLabel.setVisible(false);
//        right_jp5=new JPanel();
//        right_jp5.setLayout(null);
//        right_jp5.add(myLabel);
		right_jp2 = new JPanel();
		right_jp2.setBackground(new Color(185, 220, 237));
		right_jp2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		right_jp2.add(right_jb2);
		right_jp2.add(right_jb3);
		right_jp2.add(myLabel);
		right_jp4 = new JPanel();
		right_jp4.setForeground(new Color(135, 206, 235));
		right_jp4.setBackground(new Color(135, 206, 235));
		right_jp4.setLayout(new GridLayout(2, 1));
		right_jp4.add(right_jp1);
		right_jp4.add(right_jp2);
		right_jp3 = new JPanel();
		right_jp3.setForeground(new Color(135, 206, 235));
		right_jp3.setLayout(new BorderLayout());
		right_jp3.add(right_js);
		right_jp3.add(right_jp4, BorderLayout.SOUTH);
		right_jp3.setBackground(Color.RED);
		splitPane.setRightComponent(right_jp3);
		int width=(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height=(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int x=(width-this.getWidth())/2;
		int y=(height-this.getHeight())/2;
		this.setLocation(x, y);
		this.getContentPane().setBackground(Color.RED);
		this.setVisible(true);
		splitPane.setDividerLocation(0.48);

	}

	public JButton getRight_jb2() {
		return right_jb2;
	}

	public JButton getRight_jb3() {
		return right_jb3;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("配置端口");
		String command = e.getActionCommand();
		String to = textField.getText();
		String message = right_jtf.getText();
		sipProcessor.processAction(to, command, message);
		// sipProcessor.sendMessage(to,message);
		System.out.println(to);
		System.out.println(message);
		System.out.println("sending");

	}

	@Override
	public void processRecevieMessage(String str,Color color,boolean bold){
		MutableAttributeSet attributeSet=new SimpleAttributeSet();
		StyleConstants.setForeground(attributeSet,color);
		StyleConstants.setBold(attributeSet,bold);
		StyleConstants.setFontFamily(attributeSet,"Consolas");
		StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_LEFT);
		insertText(str,attributeSet);
	}
	private void insertText(String str, MutableAttributeSet attributeSet) {
		str+="\n";
		textPane.setStyledDocument(doc);
		doc.setParagraphAttributes(doc.getLength(), str.length(),attributeSet ,false);
		try {
			doc.insertString(doc.getLength(), str, attributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void processSendingMessage(String str,Color color,boolean bold){
		MutableAttributeSet attributeSet=new SimpleAttributeSet();
		StyleConstants.setForeground(attributeSet,color);
		StyleConstants.setBold(attributeSet,bold);
		StyleConstants.setFontFamily(attributeSet,"Consolas");
		StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_RIGHT);
		insertText(str,attributeSet);
	}

//	@Override
//	public void processError(String error) {
//		this.textPane.append("error:" + error + "/r/n");
//	}

//	@Override
//	public void processInfo(String info) {
//		this.textPane.append("info:" + info + "/r/n");
//	}

	@Override
	public void setDesdination(String to) {
		textField.setText(to);
	}

	@Override
	public void processError(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processInfo(String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearSendingText() {
		right_jtf.setText("");
	}

	@Override
	public void clearToText() {
		textField.setText("");
		textPane.setText("");
	}
}

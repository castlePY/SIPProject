package com.sip.media;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JMFReceiver implements ControllerListener, ReceiveStreamListener, SessionListener {
	RTPManager rtpManager;
	Player player;
	Component visualComponent;
	Component controlComponent;
	Object sync=new Object();
	SessionAddress remoteAddress;
	boolean received=false;
	boolean autoStop=false;
	private int localport;
	ArrayList mediaTypeList; 
	ArrayList addressList;
	ArrayList portList;
	ArrayList localPortList;
	public JMFReceiver(ArrayList mediaTypeList, ArrayList addressList, ArrayList portList) {
		this.mediaTypeList=mediaTypeList;
		this.addressList=addressList;
		this.portList=portList;
	}
//	public static void main(String[] args) {
//		try {
//			jmfReceiver.start(InetAddress.getByName(addressList.get(0)), 8087);
//			System.out.println("listening");
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//	}
	public void start() {
		
			
				new Thread(new Runnable() {
					public void run() {
						initial();
					}
				}).start();	
				
				
		
//		JButton jb2=new JButton("stop Recevie");
//		jb2.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				stopRecevie();
//			}
//		});
//		JPanel jp=new JPanel();
//		jp.add(jb1);
//		jp.add(jb2);
//		j.add(jp);
//		j.setSize(200,150);
//		j.setVisible(true);
//		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	

	protected void stopRecevie() {
		if(player!=null){
			player.stop();
			player.close();
			player=null;
		}
		if(rtpManager!=null){
			try {
				rtpManager.removeTarget(remoteAddress, "disconnection");
				rtpManager.dispose();
				rtpManager=null;
			} catch (InvalidSessionAddressException e) {
				e.printStackTrace();
			}
		}
		autoStop=true;
		System.out.println("stoping");
	}
	private void initial() {
		String sendIP=(String) addressList.get(0);
		int port=(int) portList.get(0);
		rtpManager = RTPManager.newInstance();
		rtpManager.addReceiveStreamListener(this);
		rtpManager.addSessionListener(this);
		try {
			System.out.println("localport"+localport);
			SessionAddress localAddress = new SessionAddress(InetAddress.getLocalHost(), localport);
			remoteAddress= new SessionAddress(InetAddress.getByName(sendIP), port);
			rtpManager.initialize(localAddress);
			BufferControl control = (BufferControl) rtpManager.getControl("javax.media.control.BufferControl");
			control.setBufferLength(350);
			rtpManager.addTarget(remoteAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InvalidSessionAddressException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		synchronized(sync){
			while(!received&&!autoStop){
				System.out.println("waiting source");
				try {
					sync.wait(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		player.start();
	}

	@Override
	public void update(SessionEvent arg0) {
		
	}

	@Override
	public void update(ReceiveStreamEvent arg0) {
		ReceiveStream receiveStream = arg0.getReceiveStream();
		if (arg0 instanceof NewReceiveStreamEvent) {
			receiveStream=((NewReceiveStreamEvent)arg0).getReceiveStream();
			DataSource dataSource = receiveStream.getDataSource();
			RTPControl con=(RTPControl)dataSource.getControl("javax.media.rtp.RTPControl");
			if(con!=null){
				System.out.println("recevied RTPStream"+con.getFormat());
			}
			else{
				System.out.println("recevied RTPStream");
			}
			try {
				 player = Manager.createPlayer(dataSource);
				 player.addControllerListener(this);
				 player.realize();
				synchronized(sync){
					received =true;
					sync.notifyAll();
				}
			} catch (NoPlayerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void controllerUpdate(ControllerEvent arg0) {
		if(arg0 instanceof RealizeCompleteEvent){
			player.prefetch();
		}
		else if(arg0 instanceof PrefetchCompleteEvent){
			JFrame  j1=new JFrame();
			j1.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					if(player!=null){
						player.close();
					}
				}
			});
			System.out.println("palyer"+player);
			visualComponent=player.getVisualComponent();
			System.out.println(visualComponent);
			Dimension visualSize=null;
			Dimension controlSize=null;
			if(visualComponent!=null){
				visualSize= visualComponent.getPreferredSize();
				j1.add(visualComponent);
			}
			controlComponent=player.getControlPanelComponent();
			if(controlComponent!=null){
				controlSize = controlComponent.getPreferredSize();
				j1.add(controlComponent,BorderLayout.SOUTH);
			}
//			
//			j1.setSize((int)(visualSize.getWidth()+20),(int)(visualSize.getHeight()+controlSize.getHeight()+10));
//			j1.validate();
		}
	}
	public void setPort(int localPort) {
		this.localport=localPort;
	}
	

}

package com.sip.media;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JMFSender {
	CaptureDeviceInfo audioDeviceInfo;
	Processor audioProcessor;
	DataSource audioSource;
	RTPManager audioRTPManager;
	SendStream audioSendStream;
	SessionAddress remoteAddress;
	private static final int AUDIOPORT = 8054;
	Object sync = new Object();
	boolean failed = false;
	JFrame j;
	ArrayList addressList;
	ArrayList portList;
	ArrayList mediaFormats;

	public JMFSender() {

		j = new JFrame();
		JPanel jp = new JPanel();
		JButton jb1 = new JButton("start");
		// jb1.addActionListener(new ActionListener() {

		// public void actionPerformed(ActionEvent e) {
		// try {
		// startTransmitter(InetAddress.getLocalHost().getHostAddress(), 8082);
		// } catch (UnknownHostException e1) {
		// e1.printStackTrace();
		// }
		// }
		// });
		JButton jb2 = new JButton("stop");
		jb2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopTransmitter();
			}
		});
		jp.add(jb1);
		jp.add(jb2);
		j.add(jp);
		j.setSize(200, 150);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JMFSender(ArrayList addressList, ArrayList portList, ArrayList mediaFormats) {
		this.addressList = addressList;
		this.portList = portList;
		this.mediaFormats = mediaFormats;
		this.getCaptureDeviceInfo();
		this.setAudioDataSource();
	}

	public static void main(String[] args) {
		JMFSender jmfSender = new JMFSender();
		System.out.println("first ended");

	}

	private void setAudioDataSource() {
		try {
			DataSource audioDataSource = Manager.createDataSource(audioDeviceInfo.getLocator());
			audioProcessor = Manager.createProcessor(audioDataSource);
			// audioProcessor.configure();
			waitForState(audioProcessor, Processor.Configured);
			while (true) {
				if (audioProcessor.getState() == Processor.Configured) {
					break;
				}
			}
			audioProcessor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
			TrackControl[] trackControls = audioProcessor.getTrackControls();
			boolean first = false;
			for (TrackControl trackControl : trackControls) {
				if (!first && trackControl instanceof FormatControl) {
					if (((FormatControl) trackControl)
							.setFormat(new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1)) != null) {
						first = true;
					} else {
						trackControl.setEnabled(false);
					}
				} else {
					trackControl.setEnabled(false);
				}
			}
			// audioProcessor.realize();
			// while (true) {
			// if (audioProcessor.getState() == Processor.Realized) {
			// break;
			// }
			// }
			waitForState(audioProcessor, Processor.Realized);
			audioSource = audioProcessor.getDataOutput();

		} catch (NoDataSourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoProcessorException e) {
			e.printStackTrace();
		}
		System.out.println("setAudioDataSource Success");

	}

	private void getCaptureDeviceInfo() {

		Vector deviceList = CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR, 44100, 16, 2));
		if (deviceList.size() > 0) {
			audioDeviceInfo = (CaptureDeviceInfo) deviceList.elementAt(0);
		} else {
			System.err.println("initialize failure");
			System.exit(-1);
		}
		System.out.println("getCaptureDeviceInfo Success");
	}

	public void startTransmitter() {
		for (int i = 0; i < addressList.size(); i++) {
			System.out.println("start");
			audioRTPManager = RTPManager.newInstance();
			SessionAddress localAddress = null;
			localAddress = new SessionAddress();
			try {
				audioRTPManager.initialize(localAddress);
				System.out.println("remotePort"+portList.get(i));
				remoteAddress = new SessionAddress(InetAddress.getByName((String) addressList.get(i)),
						(Integer) portList.get(i));
				audioRTPManager.addTarget(remoteAddress);

				try {
					audioSendStream = audioRTPManager.createSendStream(audioSource, 0);
				} catch (UnsupportedFormatException e) {
					e.printStackTrace();
				}
				audioProcessor.start();
				audioSendStream.start();

				System.out.println("starting");
			} catch (InvalidSessionAddressException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				e.printStackTrace();
			}
		}
	}

	public void stopTransmitter() {
		try {
			audioSendStream.close();
			audioSendStream.stop();
			audioRTPManager.removeTarget(remoteAddress, "disconnection");
			audioRTPManager.dispose();
			audioProcessor.stop();
			audioProcessor.close();
			audioSource.disconnect();
			System.out.println("stoping");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidSessionAddressException e) {
			e.printStackTrace();
		}
	}

	boolean first = true;

	public synchronized boolean waitForState(Processor p, int state) {
		if (first) {
			p.addControllerListener(new StateListener());
			first = false;
		}
		if (state == Processor.Configured) {
			p.configure();
		} else if (state == Processor.Realized) {
			p.realize();
		}
		while (p.getState() < state && !failed) {
			synchronized (sync) {
				try {
					sync.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (failed) {
			return false;
		} else {
			return true;
		}

	}

	class StateListener implements ControllerListener {

		@Override
		public void controllerUpdate(ControllerEvent arg0) {
			if (arg0 instanceof EndOfMediaEvent) {
				setFailed();
			} else if (arg0 instanceof ControllerEvent) {
				synchronized (sync) {
					sync.notifyAll();
				}
			}
		}

	}

	public void setFailed() {
		failed = true;
	}
}

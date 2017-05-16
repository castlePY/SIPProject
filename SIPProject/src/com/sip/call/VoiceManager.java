package com.sip.call;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.sip.util.URLUtil;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class VoiceManager extends JFrame {
	boolean loop1 = false;
	boolean loop = true;
	Hashtable<String, ContinuousAudioDataStream> hashtable = new Hashtable<String, ContinuousAudioDataStream>();

	@SuppressWarnings({ "restriction", "resource" })
	public static void main(String[] args) {
//		try {
//			AudioStream audioStream = new AudioStream(URLUtil.getURL("sound/ringing.wav"));
//			new Thread(new Runnable(){
//				public void run(){
//					AudioPlayer.player.start(audioStream);
//				}
//			}).start();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		// new VoiceManager().startAlert("sound/ringing.wav");
//		catch (IOException e) {
//			e.printStackTrace();
//		}
		
		new VoiceManager().startAlert("sound/ringing.wav");
	}

	public void startAlert(String url) {
		//--------------------使用AudioClip播放音频文件---------------
//		AudioClip audioClip = getAudioClip(url, true);
//		if (audioClip != null) {
//			if (loop1) {
//				audioClip.loop();
//			} else {
//				System.out.println("play");
//				while (loop) {
//					audioClip.play();
//				}
//			}
//		}
		//--------------------使用AudioStream播放音频文件---------------
		System.out.println("进来了");
		ContinuousAudioDataStream conAudioStream=getAudioStream(url,true);
		System.out.println("url"+url);
		if(conAudioStream!=null){
			AudioPlayer.player.start(conAudioStream);
		}
	}
	
	//--------------------使用AudioStream播放音频文件---------------

	private ContinuousAudioDataStream getAudioStream(String url,boolean create) {
		ContinuousAudioDataStream conAudioStream=hashtable.get(url);
		if(conAudioStream==null&&create){
			try {
				System.out.println(URLUtil.getURL(url));
//				AudioStream audioStream=new AudioStream(URLUtil.getURL(url));
				FileInputStream fis=new FileInputStream(url);
				AudioStream audioStream=new AudioStream(fis);

				System.out.println("audioStream");
				conAudioStream=new ContinuousAudioDataStream(audioStream.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
			hashtable.put(url, conAudioStream);
		}
		return conAudioStream;
	}
	

	public void stopAlert(String url) {
//		AudioClip audioClip = getAudioClip(url, false);
//		if (audioClip != null) {
//			loop = false;
//			audioClip.stop();
//		}
		ContinuousAudioDataStream conAudioStream = getAudioStream(url,false);
		if(conAudioStream!=null){
			AudioPlayer.player.stop(conAudioStream);
		}
	}

	//--------------------使用AudioClip播放音频文件---------------
//	private AudioClip getAudioClip(String url, boolean create) {
//		AudioClip audioClip = (AudioClip) hashtable.get(url);
//		if (audioClip == null && create) {
//			System.out.println("haha");
//			System.out.println(URLUtil.getURL(url).toString());
////			audioClip = Applet.newAudioClip(URLUtil.getURL(url));
//			System.out.println("audioClip" + audioClip);
//			if (audioClip != null) {
//				hashtable.put(url, audioClip);
//			}
//
//		}
//		return audioClip;
//	}

}

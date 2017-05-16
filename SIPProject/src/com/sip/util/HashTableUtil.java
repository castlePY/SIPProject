package com.sip.util;

import java.util.Hashtable;

import javax.sip.Dialog;

import com.sip.call.Call;

public class HashTableUtil {
	static Hashtable<Call, Dialog> hash = new Hashtable<Call, Dialog>();

	public static void put(Call call, Dialog callIdHeader) {
		hash.put(call, callIdHeader);
	}

	public static Dialog getCallId(Call call) {
		return hash.get(call);
	}

	public static void remove(Call source) {
		hash.remove(source);
	}
}

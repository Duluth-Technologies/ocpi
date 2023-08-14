package com.duluthtechnologies.ocpi.core.context;

public class SecurityContext {

	private static final ThreadLocal<String> cpoKey = new ThreadLocal<>();

	private static final ThreadLocal<String> emspKey = new ThreadLocal<>();

	private SecurityContext() {
	}

	public static String getCPOKey() {
		return cpoKey.get();
	}

	public static void setCPOKey(String key) {
		cpoKey.set(key);
	}

	public static String getEMSPKey() {
		return emspKey.get();
	}

	public static void setEMSPKey(String key) {
		emspKey.set(key);
	}

	public static void clear() {
		cpoKey.remove();
		emspKey.remove();
	}

}

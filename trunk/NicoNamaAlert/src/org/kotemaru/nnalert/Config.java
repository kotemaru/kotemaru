package org.kotemaru.nnalert;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

public class Config {
	private static final String UTF8 = "utf8";

	public static String configFile = "/var/run/NicoNamaAlert/NicoNamaAlert.properties";
	public static Properties config = new Properties();
	public static Map<String, UserInfo> users = new HashMap<String, UserInfo>();
	public static UserInfo[] userArray;;

	public static class UserInfo {
		String command;
		String regId;
		String mail;
		Set<String> communities;
	}

	public static void init(String file) throws IOException {
		configFile = file;
		try {
			loadConfig();
			Log.logLevel = Integer.parseInt(config.getProperty("logLevel"));
		} catch (IOException e) {
			// ignore.
		}
	}

	public static String getApiKey() {
		return config.getProperty("apiKey");
	}

	public static String getRegistrationDir() {
		return config.getProperty("registrationDir");
	}
	
	public static boolean isTestMode() {
		String str = config.getProperty("testMode");
		if (str == null) return false;
		return Boolean.parseBoolean(str);
	}

	public static void loadConfig() throws IOException {
		FileReader reader = new FileReader(configFile);
		try {
			config.load(reader);
		} finally {
			reader.close();
		}

		synchronized (users) {
			users.clear();
			File dir = new File(getRegistrationDir());
			for (String fileName : dir.list()) {
				if (fileName.indexOf('@') > 0) {
					UserInfo uinfo = parseUserInfo(new File(dir, fileName));
					users.put(uinfo.mail, uinfo);
					Log.i("Load user " + uinfo.mail);
				}
			}
			updateUserArray();
		}
	}

	private static UserInfo parseUserInfo(File file) throws XMLParseException, IOException {
		InputStream in = new FileInputStream(file);
		try {
			UserInfo uinfo = parseUserInfo(in);
			return uinfo;
		} finally {
			in.close();
		}
	}

	public static UserInfo parseUserInfo(InputStream in) throws XMLParseException, IOException {
		XMLElement parser = new XMLElement();
		InputStreamReader reader = new InputStreamReader(in, UTF8);
		parser.parseFromReader(reader);
		Xml xml = new Xml(parser);

		UserInfo uinfo = new UserInfo();
		uinfo.command = xml.getContent("command");
		uinfo.regId = xml.getContent("regId");
		uinfo.mail = xml.getContent("mail");
		uinfo.communities = new HashSet<String>(xml.getContents("communities/community_id"));

		if (uinfo.regId == null || uinfo.mail == null) {
			return null;
		}
		return uinfo;
	}

	public static void saveUserInfo(UserInfo uinfo) throws IOException {
		File file = new File(getRegistrationDir() + "/" + uinfo.mail);

		String xml = "<?xml version='1.0' encoding='utf-8'>\n"
				+ "<request_register>\n"
				+ "<regId>" + uinfo.regId + "</regId>\n"
				+ "<mail>" + uinfo.mail + "</mail>\n"
				+ "<communities>\n" + toXml(uinfo.communities) + "</communities>\n"
				+ "</request_register>";
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(xml.getBytes(UTF8));
		} finally {
			out.close();
		}
		synchronized (users) {
			users.put(uinfo.mail, uinfo);
			updateUserArray();
		}
	}

	private static String toXml(Set<String> communities) {
		StringBuilder sbuf = new StringBuilder();
		for (String commId : communities) {
			sbuf.append("<community_id>").append(commId).append("</community_id>\n");
		}
		return sbuf.toString();
	}

	public static void removeUserInfo(UserInfo uinfo) throws IOException {
		File file = new File(getRegistrationDir() + "/" + uinfo.mail);
		file.delete();
		synchronized (users) {
			users.remove(uinfo.mail);
			updateUserArray();
		}
	}

	public static UserInfo[] getUserArray() {
		synchronized (users) {
			return userArray;
		}
	}

	private static void updateUserArray() throws IOException {
		synchronized (users) {
			userArray = new UserInfo[users.size()];
			int i = 0;
			for (Map.Entry<String, UserInfo> ent : users.entrySet()) {
				UserInfo uinfo = ent.getValue();
				userArray[i++] = uinfo;
			}
		}
	}

}
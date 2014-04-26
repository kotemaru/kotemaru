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
	public static Map<String,UserInfo> users = new HashMap<String,UserInfo>();

	public static class UserInfo {
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
	public static Map<String,UserInfo> getUsers() {
		return users;
	}

	public static String getApiKey() {
		return config.getProperty("apiKey");
	}

	public static String getRegistrationDir() {
		return config.getProperty("registrationDir");
	}

	public static void loadConfig() throws IOException {
		FileReader reader = new FileReader(configFile);
		try {
			config.load(reader);
		} finally {
			reader.close();
		}

		users.clear();
		File dir = new File(getRegistrationDir());
		for (String fileName : dir.list()) {
			if (fileName.indexOf('@') > 0) {
				UserInfo uinfo = parseUserInfo(new File(dir, fileName));
				users.put(uinfo.mail, uinfo);
			}
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
		users.put(uinfo.mail, uinfo);
	}

	private static String toXml(Set<String> communities) {
		StringBuilder sbuf = new StringBuilder();
		for (String commId : communities) {
			sbuf.append("<community_id>").append(commId).append("</community_id>\n");
		}
		return sbuf.toString();
	}


}
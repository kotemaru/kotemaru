package org.kotemaru.blog;
import java.util.Date;

import org.kotemaru.gae.storedbean.StoredBean;


public class CommentBean implements StoredBean {

	private static final long serialVersionUID = 1L;
	
	private Long key;
	private String page;
	private String name;
	private String email;
	private String ipAddr;
	private String passwd;
	private Date date;
	private String body;
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getKey() {
		return key;
	}
	public void setKey(Long key) {
		this.key = key;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	

}

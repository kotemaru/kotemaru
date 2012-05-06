package org.kotemaru.gae.bcoro;
import java.util.Date;

import org.kotemaru.gae.storedbean.StoredBean;


public class FileBean implements StoredBean {

	private static final long serialVersionUID = 1L;
	
	private String owner;
	private String nickName;
	private String lastName;
	private String parentName;
	private String contentType;
	private Date lastModified;
	private Long length;
	private Boolean directory;
	private byte[] body;

	
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public Boolean getDirectory() {
		return directory;
	}
	public void setDirectory(Boolean directory) {
		this.directory = directory;
	}


	
}

package org.kotemaru.gae.bcoro;
import org.kotemaru.gae.storedbean.StoredBean;


public class UserBean implements StoredBean {

	private static final long serialVersionUID = 1L;
	
	private String email;
	private String nickName;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}



}

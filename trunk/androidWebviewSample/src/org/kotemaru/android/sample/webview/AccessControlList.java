/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.sample.webview;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * アクセス制限リスト。
 * - ホワイトリスト方式でアクセス許可するホストを管理する。
 * - 内部クラスの AccessControl を継承するクラスをリストに登録できる。
 * @author inou
 *
 */
public class AccessControlList {

	private List<AccessControl> _accessControlList = new LinkedList<AccessControl>();
	
	public AccessControlList() {
	}
	
	/**
	 * アクセス制御をリストの最後に追加する。
	 * @param accessControl アクセス制御
	 */
	public void addAccessControl(AccessControl accessControl) {
		_accessControlList.add(accessControl);
	}

	/**
	 * 簡易記法でアクセス許可するホストをリストの最後に追加する
	 * - 書式はスキーマとホスト名のみのURLでホストに"*"でワイルドカードを指定できる。
	 * - 例:
	 * -- http://www.google.com
	 * -- https://*.google.com
	 * -- http://192.168.0.1
	 * -- http://192.168.0.*
	 * @param accessControl アクセス制御
	 */
	public void addAllowAccess(String urlMask) throws URISyntaxException {
		String[] parts = urlMask.split("://");
		if (parts.length != 2) {
			throw new URISyntaxException("Require {scheme}://{domain-pattern}", urlMask);
		}
		String scheme = parts[0].toLowerCase(Locale.US);
		String pattern = parts[1].replaceFirst("/$","").toLowerCase(Locale.US);
		if (pattern.matches("^[*][.]")) {
			addAccessControl(new SubDomain(true, scheme, pattern));
		} else if (pattern.matches("[.][*]$")) {
			addAccessControl(new IpMaskEasy(true, scheme, pattern));
		} else {
			addAccessControl(new Host(true, scheme, pattern));
		}
	}
	
	/**
	 * アクセス可能なURIかチェックする。
	 * @param uri チェック対象URI
	 * @throws IOException アクセス禁止の場合、発生。
	 */
	public void check(URI uri) throws IOException {
		if (!isAllow(uri)) {
			throw new IOException("AccessControlList check is DENY: "+uri);
		}
	}
	/**
	 * アクセス可能なURIかチェックする。
	 * - リストを上から順番にチェックして最初に当たったものを返す。
	 * - すべて一致しなければ不許可を返す。
	 * @param uri チェック対象URI
	 * @return true=アクセス許可。
	 */
	public boolean isAllow(URI uri) {
		// host==null: ベースURLなので常にtrue.
		if (uri.getHost() == null) return true;
		
		for (AccessControl domain : _accessControlList) {
			if (domain.isMatch(uri)) {
				return domain.isAllow();
			}
		}
		return false;
	}
	
	/**
	 * アクセス制御の基底クラス。
	 */
	public static abstract class AccessControl {
		protected boolean _isAllow = true;
		protected String _scheme;
		protected String _domainName;
		public AccessControl(boolean isAllow, String scheme, String domainName) {
			_isAllow = isAllow;
			_scheme = scheme.toLowerCase(Locale.US);
			_domainName = domainName;
		}
		/**
		 * URIが一致した場合の許可/不許可を返す。
		 * - ホワイトリスト方式なので基本的にtrueとなる。
		 * @return true=アクセス許可
		 */
		public boolean isAllow() {
			return _isAllow;
		}
		protected boolean isMatchScheme(URI uri) {
			String scheme = uri.getScheme().toLowerCase(Locale.US);
			return _scheme.equals(scheme);
		}
		/**
		 * URIがこのアクセス制御に一致する場合はtrue。
		 * - 許可/不許可は isAllow() で判断するので単純にURIの一致のみを検証する。
		 * @param uri 検証対象のURI
		 * @return true=一致
		 */
		public abstract boolean isMatch(URI uri);
	}

	public static class SubDomain extends AccessControl{
		public SubDomain(boolean isAllow, String scheme, String domainName) {
			super(isAllow, scheme, domainName.replaceFirst("^[*]", ""));
		}
		@Override
		public boolean isMatch(URI uri) {
			String host = uri.getHost();
			return host != null && host.endsWith(_domainName) && isMatchScheme(uri);
		}
	}
	public static class Host extends AccessControl{
		public Host(boolean isAllow, String scheme, String domainName) {
			super(isAllow, scheme, domainName);
		}
		@Override
		public boolean isMatch(URI uri) {
			String host = uri.getHost();
			return host != null && host.equals(_domainName) && isMatchScheme(uri);
		}
	}
	public static class IpMaskEasy extends AccessControl{
		public IpMaskEasy(boolean isAllow, String scheme, String domainName) {
			super(isAllow, scheme, domainName.replaceFirst("[*]$", ""));
		}
		@Override
		public boolean isMatch(URI uri) {
			String host = uri.getHost();
			try {
				String addr = InetAddress.getByName(host).getHostAddress();
				return addr.startsWith(_domainName) && isMatchScheme(uri);
			} catch (UnknownHostException e) {
				return false;
			}
		}
	}

	
}

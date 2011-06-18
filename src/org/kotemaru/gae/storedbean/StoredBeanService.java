/****************************************************************************
 * Copyright 2011 kotemaru@kotemaru.org
 * Apache license 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 ****************************************************************************/
package org.kotemaru.gae.storedbean;

import java.util.*;
import java.lang.reflect.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

/**
DataSotreの低レベルAPIを利用しBeanを簡単に永続化するサービス。
<p/>
永続化可能なBeanは以下の制限を持つ。 
<ul>
<li>{@link StoredBean} を実装する。
<li>int,long,boolean,String,byte[],List,Set,{@link StoredBean} 型のみを項目として持つ。
<li>項目は Bean の仕様に従った setter/getter を持つ。
<li>List,Setは Integer,Long,Boolean,String,{@link StoredBean} 型のみを項目として持つ。
<li>{@link StoredBean} の再帰は DataStore の制限まで可能。
</ul>

<p/>
その他の機能、挙動。
<ul>
<li>永続化 Bean への参照は Memcache を使用したキャッシュを行う事ができる。デフォルト有効。
<li>List,Set が復元されると実体として元の型とはならず ArrayList,HashSet となる。
</ul>

<p/>
内部実装
<ul>
<li>１つの{@link StoredBean}は１つのEntityに変換する。
<li>Beanの項目はそのままの名前でEntityのプロパティに変換する。
<li>項目名 "_class_" はBeanのクラス名を設定する。
<li>項目名 "_path_" はBeanのパス名を設定する。パス名は項目名を"."で繋いだ物。管理画面からの検索用。
<li>再帰する {@link StoredBean} の実体は別の Entity となるのでEntityプロパティの値として Key を設定する。
<li>再帰する {@link StoredBean} のKeyはDataStore上の親子関係を持ちトランザクションにより一括処理する。
<li>byte[] の項目は Blob に変換する。
</ul>

<p/>
使用例：
<xmp style="left-margin:2em;background:lightgray;">
class TestBean implements StoredBean {
	private int item01;
	private String item02;

	public int getItem01() {
		return item01;
	}
	public void setItem01(int item01) {
		this.item01 = item01;
	}
	public String getItem02() {
		return item02;
	}
	public void setItem02(String item02) {
		this.item02 = item02;
	}
}

StoredBeanService sbs = new StoredBeanService("StoredBean");

TestBean bean = new TestBean();
bean.setItem01(123);
bean.setItem02("abc");

sbs.put("test", bean);

TestBean restoreBean = (TestBean) sbs.get("test");
</xmp>

@author kotemaru@kotemaru.org
*/
public class StoredBeanService {
	private static final MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(
			StoredBeanService.class.getName()
	);

	static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService();
	static final String CLASS = "_class_";
	static final String PATH = "_path_";

	private String kind;
	private boolean isMemcacheEnable = true;

	/**
	 * コンストラクタ。
	 * <li>Beanはここで指定したkindに保存する。
	 * @param k DataStoreのkind
	 */
	public StoredBeanService(String k) {
		this.kind = k;
	}

	/**
	 * Memcache の有効／無効の設定。デフォルトは有効。
	 * @param b 有効／無効
	 */
	public void setMemcacheEnable(boolean b) {
		isMemcacheEnable = b;
	}

	/**
	 * Memcache の有効／無効の取得。
	 * @return 有効／無効
	 */
	public boolean isMemcacheEnable() {
		return isMemcacheEnable;
	}

	/**
	 * Beanの保存。
	 * @param name keyの名前
	 * @param bean 永続化するBean
	 * @return Key
	 */
	public Key put(String name, StoredBean bean) {
		Transaction tx = DS.beginTransaction();
		try {
			return put(tx, KeyFactory.createKey(kind, name), bean);
		} finally {
			tx.commit();
			// TODO: rollback
		}
	}

	/**
	 * Beanの保存。
	 * <li>トランザクションやKeyにIDを使いたい場合に使用する。
	 * @param tx   トランザクション
	 * @param key  key
	 * @param bean 永続化するBean
	 * @return Key
	 */
	public Key put(Transaction tx, Key key, StoredBean bean) {
		try {
			Key key0 = puts(tx, key, bean);
			if (isMemcacheEnable) {
				memcache.put(key0, bean);
			}
			return key0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Beanの取得。
	 * @param name keyの名前
	 * @return 取得したBean
	 * @throw EntityNotFoundException 内包する {@link StoredBean} が見付からない場合も発生する。
	 */
	public StoredBean get(String name) throws EntityNotFoundException {
		Transaction tx = DS.beginTransaction();
		try {
			return get(tx, KeyFactory.createKey(kind, name));
		} finally {
			tx.commit();
			// TODO:rollback
		}
	}

	/**
	 * Beanの取得。
	 * @param tx   トランザクション
	 * @param key Key
	 * @return 取得したBean
	 * @throw EntityNotFoundException 内包する {@link StoredBean} が見付からない場合も発生する。
	 */
	public StoredBean get(Transaction tx, Key key) throws EntityNotFoundException {
		try {
			if (isMemcacheEnable) {
				StoredBean bean = (StoredBean) memcache.get(key);
				if (bean != null) return bean;
				bean = gets(tx, key);
				memcache.put(key, bean);
				return bean;
			} else {
				return gets(tx, key);
			}
		} catch (EntityNotFoundException e) {
			//return null;
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}



	/**
	 * Beanの保存の本体。
	 * <li>メソッド一覧からgetterのみを取り出して戻り値をEntityに詰め替える。
	 * <li>項目値がStoredBeanの場合は再帰的に自身を呼び出し戻り値のKeyを項目値とする。
	 * <li>項目値がList,Setの場合は各項目毎に詰め替えを行う。
	 * <li>項目値がbyte[]の場合はBlobでラップして項目値とする。
	 * <li>全ての項目を設定後、DataStoreにputする。
	 * @param tx   トランザクション
	 * @param key  key
	 * @param bean 永続化するBean
	 * @return Key
	 */
	protected Key puts(Transaction tx, Key key, StoredBean bean) 
			throws InvocationTargetException, IllegalAccessException {
		Class cls = bean.getClass();
		Entity entity = new Entity(key);
		entity.setProperty(CLASS, cls.getName());
		entity.setProperty(PATH, getPath(key));

		Method[] methods = cls.getMethods();
		for (int i=0; i<methods.length; i++) {
			Method m = methods[i];
			String pname = getterName(m);
			if (pname != null) {
				Object val = m.invoke(bean);

				if (val instanceof StoredBean) {
					Key subKey = puts(tx, key(key,pname), (StoredBean)val);
					entity.setProperty(pname, subKey);
				} else if (val instanceof Collection) {
					Collection coll = putsCollection(tx, key, (Collection)val, pname);
					entity.setProperty(pname, coll);
				} else if (val instanceof byte[]) {
					entity.setProperty(pname, new Blob((byte[])val));
				} else {
					entity.setProperty(pname, val);
				}
			}
		}
		DS.put(tx, entity);
		return key;
	}

	/**
	 * 保存用List,Setの詰め替え。
	 * <li>項目値がStoredBeanの場合は再帰的にputsを呼び出し戻り値のKeyを項目値とする。
	 * <li>その場合のKey名は "項目名[n]" とする。
	 * @param tx   トランザクション
	 * @param key  key
	 * @param src  元のList or Set
	 * @param pname  Beanの項目名
	 * @param bean 永続化するBean
	 * @return 詰め替え後の List or Set。
	 */
	private Collection putsCollection(Transaction tx, Key key, Collection src, String pname)
			throws InvocationTargetException, IllegalAccessException
	{
		Collection dst = (src instanceof List)
			? new ArrayList(src.size())
			: new HashSet(src.size());

		int n = 0;
		for (Object item : src) {
			if (item instanceof StoredBean) {
				Key subKey = puts(tx, key(key,pname+"["+n+"]"), (StoredBean)item);
				dst.add(subKey);
			} else {
				dst.add(item);
			}
			n++;
		}
		return dst;
	}

	/**
	 * Beanの保存の本体。
	 * <li>DataSotreからkeyのEntityを取得する。
	 * <li>メソッド一覧からsetterのみを取り出しEntityプロパティから詰め替える。
	 * <li>項目値がKeyの場合は再帰的に自身を呼び出し戻り値のStoredBeanを項目値とする。
	 * <li>項目値がCollectionの場合は各項目毎に詰め替えを行う。
	 * <li>EntityプロパティはSetで保存してもListで取れるのでsetterの型を見て詰め替え先をArrayList,HashSetを選択する。
	 * <li>項目値がBlobの場合はbyte[]を取り出して項目値とする。
	 * <li>Entityプロパティはintで保存してもlongで取れるのでsetterの型を見てintに変換する。
	 * @param tx   トランザクション
	 * @param key  key
	 * @return 復元したBean
	 * @throw EntityNotFoundException 内包する {@link StoredBean} が見付からない場合も発生する。
	 */
	protected StoredBean gets(Transaction tx, Key key) 
				throws EntityNotFoundException, Exception
	{
		Entity entity = DS.get(tx, key);
		Class cls = Class.forName((String)entity.getProperty(CLASS));
		StoredBean bean = (StoredBean)cls.newInstance();

		Method[] methods = cls.getMethods();
		for (int i=0; i<methods.length; i++) {
			Method m = methods[i];
			String pname = setterName(m);
			Class[] types = m.getParameterTypes();
			if (pname != null) {
				Object val = entity.getProperty(pname);
				if (isChild(key, val)) {
					StoredBean subVal = gets(tx, (Key)val);
					m.invoke(bean, subVal);
				} else if (val instanceof Collection) {
					// 保存する時にDataStoreが Set->List 変換するので逆変換。
					Collection src = (Collection)val;
					Collection dst = (types[0].isAssignableFrom(List.class))
						? new ArrayList(src.size())
						: new HashSet(src.size());
					getsCollection(tx, key, src, dst);
					m.invoke(bean, dst);
				} else if (val instanceof Blob) {
					m.invoke(bean, ((Blob)val).getBytes());
				} else if (val != null) {
					// 保存する時にDataStoreが int->long 変換するので逆変換。
					if (int.class.equals(types[0])) {
						m.invoke(bean, ((Long)val).intValue() );
					} else {
						m.invoke(bean, val);
					}
				}
			}
		}
		return bean;
	}

	/**
	 * 復元用List,Setの詰め替え。
	 * <li>項目値がKeyの場合は再帰的にgetsを呼び出し戻り値のStoredBeanを項目値とする。
	 * @param tx   トランザクション
	 * @param key  key
	 * @param src  詰め替え元のList or Set
	 * @param dst  詰め替え先のList or Set
	 * @return 復元したBean
	 * @throw EntityNotFoundException 内包する {@link StoredBean} が見付からない場合も発生する。
	 */
	private Collection getsCollection(Transaction tx, Key key, Collection src, 
			Collection dst) throws  Exception {
		//Collection dst = (src instanceof List)
		//	? new ArrayList(src.size())
		//	: new HashSet(src.size());

		int n = 0;
		for (Object item : src) {
			if (isChild(key, item)) {
				StoredBean subVal = gets(tx, (Key)item);
				dst.add(subVal);
			} else {
				dst.add(item);
			}
		}
		return dst;
	}



	/**
	 * Keyの生成。
	 * <li>kindはこのインスタンスのkind。
	 * @param parent 親キー
	 * @param name   名前
	 * @return Key
	 */
	private Key key(Key parent, String name) {
		return KeyFactory.createKey(parent, kind, name);
	}
	/**
	 * Keyのパスを返す。
	 * <li>親キーをたどってキー名を"."で繋ぎフルパスを生成する。
	 * @param key キー
	 * @return パス名。
	 */
	private String getPath(Key key) {
		StringBuffer sbuf = new StringBuffer();
		while (key != null) {
			if (key.getName() != null) {
				sbuf.insert(0, key.getName());
			} else {
				sbuf.insert(0, Long.toString(key.getId()));
			}
			key = key.getParent();
			if (key != null) sbuf.insert(0, ".");
		}
		return sbuf.toString();
	}

	/**
	 * 再帰的 StoredBean のチェック。
	 * <li>Entityプロパティの値の型がKeyで親キーと親子ならtrueを返す。
	 * @param key   親キー
	 * @param val   Entityプロパティの値。
	 * @return true=StoredBean
	 */
	private boolean isChild(Key key, Object val) {
		if (!(val instanceof Key)) return false;
		Key parent = ((Key)val).getParent();
		return key.equals(parent);
	}

	/**
	 * getterメソッドチェック。
	 * <li>メソッドがgetterかチェックしgetterならばBean項目名を返す。
	 * <li>メソッド名が /^get[A-Z]/ or /^is[A-Z]/ のパターンに一致し引数が無い場合getterである。
	 * <li>但し、getClass は除外する。
	 * <li>項目名はメソッド名から /^get/ or /^is/ を取り除いた物である。
	 * @param m   メソッド
	 * @return Bean項目名。getterでなければ null を返す。
	 */
	private String getterName(Method m) {
		if (m.getParameterTypes().length != 0) return null;
		String name = m.getName();
		if (name.equals("getClass")) return null;
		if (name.startsWith("get")) {
			if (name.length()<4) return null;
			char ch4 = name.charAt(3);
			if ('A' <= ch4 && ch4 <= 'Z') return toItemName(ch4,name,3);
		} else if (name.startsWith("is")) {
			if (name.length()<3) return null;
			char ch3 = name.charAt(2);
			if ('A' <= ch3 && ch3 <= 'Z') return toItemName(ch3,name,2);
		}
		return null;
	}
	private String toItemName(char ch, String name, int pos) {
		if (name.length() <= pos) {
			return ""+(char)(ch+0x20);
		}
		return ""+(char)(ch+0x20)+name.substring(pos+1);
	}

	/**
	 * setterメソッドチェック。
	 * <li>メソッドがsetterかチェックしsetterならばBean項目名を返す。
	 * <li>メソッド名が /^set[A-Z]/ のパターンに一致し引数が１つの場合setterである。
	 * <li>項目名はメソッド名から /^set/ を取り除いた物である。
	 * @param m   メソッド
	 * @return Bean項目名。setterでなければ null を返す。
	 */
	private String setterName(Method m) {
		if (m.getParameterTypes().length != 1) return null;
		String name = m.getName();
		if (name.length()<4) return null;
		if (!name.startsWith("set")) return null; 
		char ch4 = name.charAt(3);
		if ('A' <= ch4 && ch4 <= 'Z') return toItemName(ch4,name,3);
		return null;
	}

}

package org.kotemaru.xmlbean;

import java.lang.reflect.Type;

import org.w3c.dom.Element;

public interface XBBuilder {
	/**
	 * XML要素からインスタンスを生成する。
	 * @param elem 当該XML要素
	 * @param type 要求されるクラス
	 * @param subTypes typeのGenerics。ClassからGenericsは引けないので呼び元で生成する。
	 * @return インスタンス
	 * @throws Exception
	 */
	public Object toInstance(Element elem, Class<?> type, Type[] subTypes) throws Exception;


	/**
	 * インスタンスを文字列に変換する。
	 * @param val インスタンス
	 * @return 文字列
	 * @throws Exception
	 */
	public String toString(Object val) throws Exception;

}

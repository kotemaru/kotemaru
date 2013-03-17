package org.kotemaru.xmlbean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReflectUtil {
	
	private static final Class<?>[] NIL_PARAM = new Class[]{};
	private static final Class<?>[] VALUEOF_PARAM = new Class[]{String.class};

	private static final Map<Class<?>,Class<?>> wrapper = new HashMap<Class<?>,Class<?>>();
	private static final Map<Class<?>,Class<?>> wrapperRv = new HashMap<Class<?>,Class<?>>();
	static {
		wrapper.put(boolean.class, Boolean.class);
		wrapper.put(int.class,     Integer.class);
		wrapper.put(char.class,    Character.class);
		wrapper.put(byte.class,    Byte.class);
		wrapper.put(short.class,   Short.class);
		wrapper.put(double.class,  Double.class);
		wrapper.put(long.class,    Long.class);
		wrapper.put(float.class,   Float.class);
		
		for (Map.Entry<Class<?>,Class<?>> ent : wrapper.entrySet()) {
			wrapperRv.put(ent.getValue(), ent.getKey());
		}
	}
	
	public static Class<?> getPropertyType(Object obj, String name) {
		Method getter = getterMethod(obj, name);
		if (getter == null) return null;
		return getter.getReturnType();
	}
	public static Type[] getPropertyGenericTypes(Object obj, String name) {
		Method getter = getterMethod(obj, name);
		if (getter == null) return null;
		Type type = getter.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			return paramType.getActualTypeArguments();
		}
		return null;
	}
	
	public static Method getterMethod(Object obj, String name) {
		String Name = Character.toUpperCase(name.charAt(0))+name.substring(1);
		try {
			return obj.getClass().getMethod("get"+Name, NIL_PARAM);
		} catch (NoSuchMethodException e) {
			try {
				return obj.getClass().getMethod("is"+Name, NIL_PARAM);
			} catch (NoSuchMethodException e1) {
				return null;
			}
		}
	}

	public static Method setterMethod(Object obj, String name, Class<?> type) {
		String Name = Character.toUpperCase(name.charAt(0))+name.substring(1);
		try {
			//Class<?> primType = wrapperRv.get(type);
			//type = primType != null ? primType : type;
			return obj.getClass().getMethod("set"+Name, type);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static void setProperty(Object obj, String name, Class<?> type, Object val) {
		Method setter = setterMethod(obj, name, type);
		if (setter == null) {
			throw new RuntimeException("Not found setter "+name+" type="+val.getClass());
		}
		try {
			setter.invoke(obj, new Object[]{val});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	public static Object valueOf(Class<?> type, String val) {
		try {
			Class<?> _type = type.isPrimitive() ? wrapper.get(type) : type;
			Method valueOf = _type.getMethod("valueOf", VALUEOF_PARAM);
			return valueOf.invoke(null, new Object[]{val});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class PropertyInfo {
		public String name;
		public Class<?> type;
		public Method getter;
		public Method setter;
		public PropertyInfo(String name) {
			this.name = name;
		}
	}
	
	public static Map<String,PropertyInfo> getPropertyMap(Object obj) {
		Map<String,PropertyInfo> map = new HashMap<String,PropertyInfo>();

		Method[] methods = obj.getClass().getMethods();
		for (int i=0; i<methods.length; i++) {
			String gname = getterName(methods[i]);
			String sname = setterName(methods[i]);
			String name = gname==null ? sname : gname;
			if (name != null) {
				PropertyInfo info = map.get(name);
				if (info == null) {
					info = new PropertyInfo(name);
					map.put(name, info);
				}
				if (gname != null) info.getter = methods[i];
				if (sname != null) info.setter = methods[i];
			}
		}
		
		Iterator<Map.Entry<String,PropertyInfo>> ite = map.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String,PropertyInfo> ent = ite.next();
			PropertyInfo info = ent.getValue();
			if (info.getter == null || info.setter == null) {
				ite.remove();
			} else {
				Class<?> type = info.getter.getReturnType();
				info.type = type;
			}
		}
		
		return map;
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
	private static String getterName(Method m) {
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
	private static String toItemName(char ch, String name, int pos) {
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
	private static String setterName(Method m) {
		if (m.getParameterTypes().length != 1) return null;
		String name = m.getName();
		if (name.length()<4) return null;
		if (!name.startsWith("set")) return null; 
		char ch4 = name.charAt(3);
		if ('A' <= ch4 && ch4 <= 'Z') return toItemName(ch4,name,3);
		return null;
	}
	
	public static boolean isWrapperType(Class<?> clazz) {
		return clazz.equals(Boolean.class) || 
			clazz.equals(Integer.class) ||
			clazz.equals(Character.class) ||
			clazz.equals(Byte.class) ||
			clazz.equals(Short.class) ||
			clazz.equals(Double.class) ||
			clazz.equals(Long.class) ||
			clazz.equals(Float.class);
	}
}

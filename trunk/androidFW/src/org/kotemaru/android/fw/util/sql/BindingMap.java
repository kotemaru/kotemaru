package org.kotemaru.android.fw.util.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class BindingMap implements Map<String, Object> {
	private static final String TAG = NamedStatement.class.getSimpleName();

	public interface BindingConvertor {
		public String toString(Object value);
	}

	private Map<String, Binding> mInnerMap = new HashMap<String, Binding>();
	private SQLiteStatement mStatement;
	private BindingConvertor mBindingConvertor;

	public BindingMap(SQLiteStatement statement) {
		mStatement = statement;
	}

	@Override
	public Object put(String key, Object value) {
		Binding binding = get(key);
		if (binding == null) {
			Log.i(TAG, "Unknown binding name " + key);
		}
		for (int i = 0; i < binding.mIndexsSize; i++) {
			if (value == null) {
				mStatement.bindNull(binding.mIndexs[i]);
			} else if (value instanceof String) {
				mStatement.bindString(binding.mIndexs[i], value.toString());
			} else if (value instanceof Long) {
				mStatement.bindLong(binding.mIndexs[i], (Long) value);
			} else if (value instanceof Integer) {
				mStatement.bindLong(binding.mIndexs[i], ((Integer) value).longValue());
			} else if (value instanceof Short) {
				mStatement.bindLong(binding.mIndexs[i], ((Short) value).longValue());
			} else if (value instanceof Double) {
				mStatement.bindDouble(binding.mIndexs[i], (Double) value);
			} else if (value instanceof Boolean) {
				mStatement.bindLong(binding.mIndexs[i], (Boolean) value ? 1L : 0L);
			} else if (value instanceof byte[]) {
				mStatement.bindBlob(binding.mIndexs[i], (byte[]) value);
			} else if (mBindingConvertor != null) {
				mStatement.bindString(binding.mIndexs[i], mBindingConvertor.toString(value));
			} else {
				throw new RuntimeException("Bad binding value type: " + value);
			}
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ?> map) {
		for (Map.Entry<? extends String, ?> ent : map.entrySet()) {
			put(ent.getKey(), ent.getValue());
		}
	}

	public Binding getBinding(String name) {
		Binding binding = mInnerMap.get(name);
		if (binding == null) throw new RuntimeException("Unknown binding name " + name);
		return binding;
	}

	public BindingConvertor getBindingConvertor() {
		return mBindingConvertor;
	}

	public void setBindingConvertor(BindingConvertor bindingConvertor) {
		mBindingConvertor = bindingConvertor;
	}

	// ---------------------------------
	// Delegate methods
	public void clear() {
		mInnerMap.clear();
	}

	public boolean containsKey(Object key) {
		return mInnerMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return mInnerMap.containsValue(value);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object object) {
		return mInnerMap.equals(object);
	}

	public Binding get(Object key) {
		return mInnerMap.get(key);
	}

	public int hashCode() {
		return mInnerMap.hashCode();
	}

	public boolean isEmpty() {
		return mInnerMap.isEmpty();
	}

	public Set<String> keySet() {
		return mInnerMap.keySet();
	}

	public Binding remove(Object key) {
		return mInnerMap.remove(key);
	}

	public int size() {
		return mInnerMap.size();
	}

	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

}

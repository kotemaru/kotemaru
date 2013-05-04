package org.kotemaru.eclipse.umldraw;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;

public class Preference extends AbstractPreferenceInitializer {

	public static final String BALLOON = "directionsBalloon";
	public static final String ROUTE_DEFAULT = "lineRouteDefault";
	
	static class Item {
		public String key;
		public Class<?> type;
		public Object defaultValue;
		
		public Item(String k, Class<?> t, Object v) {
			key = k;
			type = t;
			defaultValue = v;
		}
	}
	
	public static final Item[] ITEMS = {
		new Item(BALLOON, Boolean.class, true),
		new Item(ROUTE_DEFAULT, String.class, "N"),
	};
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(BALLOON, StringConverter.asString(true));
		store.setDefault(ROUTE_DEFAULT, "N");
	}
}
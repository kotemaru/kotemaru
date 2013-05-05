package org.kotemaru.eclipse.umldraw;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;

public class Preference extends AbstractPreferenceInitializer {

	public static final String BALLOON = "directionsBalloon";
	public static final String ROUTE_DEFAULT = "lineRouteDefault";
	public static final String FONT_FAMILY = "fontFamily";
	public static final String FONT_FAMILY_P = "fontFamilyP";
	public static final String FONT_FAMILY_M = "fontFamilyM";
	
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
		new Item(FONT_FAMILY, String.class, "arial,sans-serif"),
		new Item(FONT_FAMILY_P, String.class, "arial,sans-serif"),
		new Item(FONT_FAMILY_M, String.class, "monospace"),
	};
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		for (int i=0; i<ITEMS.length; i++) {
			if (ITEMS[i].type == String.class) {
				store.setDefault(ITEMS[i].key, (String)ITEMS[i].defaultValue);
			} else if (ITEMS[i].type == Boolean.class) {
				store.setDefault(ITEMS[i].key, StringConverter.asString((Boolean)ITEMS[i].defaultValue));
			} else if (ITEMS[i].type == Integer.class) {
				store.setDefault(ITEMS[i].key, StringConverter.asString((Integer)ITEMS[i].defaultValue));
			}
		}
	}
}
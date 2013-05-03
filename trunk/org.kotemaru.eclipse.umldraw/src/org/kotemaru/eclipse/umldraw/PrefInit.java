package org.kotemaru.eclipse.umldraw;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;

public class PrefInit extends AbstractPreferenceInitializer {

	public static final String BALLOON = "directionsBalloon";
	public static final String ROUTE_DEFAULT = "lineRouteDefault";
	
	public static final String[] KEYS = {
		BALLOON, ROUTE_DEFAULT
	};
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(BALLOON, StringConverter.asString(true));
		store.setDefault(ROUTE_DEFAULT, "N");
	}
}
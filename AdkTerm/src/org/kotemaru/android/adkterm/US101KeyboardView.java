package org.kotemaru.android.adkterm;

import java.util.List;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;

public class US101KeyboardView extends KeyboardView {

	private static final int KEYCODE_CTRL = -100;
	
	private Keyboard keyboard;
	private Key shiftKey;
	private Key ctrlKey;
	private boolean isCapsLock = false;
	private boolean isShiftLongPress = false;

	public US101KeyboardView(Context context) {
		super(context, null);
		keyboard = new Keyboard(context, R.xml.us101);
		super.setKeyboard(keyboard);
		super.setPreviewEnabled(false);

		List<Key> keys = keyboard.getKeys();
		for (Key key : keys) {
			if (key.codes[0] == Keyboard.KEYCODE_SHIFT) shiftKey = key;
			if (key.codes[0] == KEYCODE_CTRL) ctrlKey = key;
		}
	}

	@Override
	public boolean isShifted() {
		return shiftKey.on;
	}
	@Override
	public boolean setShifted(boolean b) {
		shiftKey.on = b;
		super.invalidateAllKeys();
		return b;
	}
	
	public boolean isCtrled() {
		return ctrlKey.on;
	}
	public boolean setCtrled(boolean b) {
		ctrlKey.on = b;
		super.invalidateAllKeys();
		return b;
	}
	
	
	@Override
	public boolean onLongPress(Key key) {
		if (key == shiftKey) {
			isShiftLongPress = true;
		}
		return false;
	}

	public void onPressShift() {
	}

	public void onReleaseShift() {
		if (isShiftLongPress) {
			isCapsLock = isShifted();
		} else {
			isCapsLock = false;
		}
		isShiftLongPress = false;
	}


	public boolean isCapsLock() {
		return isCapsLock;
	}
	
	public static abstract class OnKeyboardListener implements OnKeyboardActionListener {
		protected US101KeyboardView keyboardView;
		
		public OnKeyboardListener(US101KeyboardView self) {
			keyboardView = self;
		}
		
		public abstract void onChar(char unicodeChar);

		@Override
		public void onPress(int paramInt) {
			if (paramInt == Keyboard.KEYCODE_SHIFT) {
				keyboardView.onPressShift();
			}
		}

		@Override
		public void onRelease(int paramInt) {
			if (paramInt == Keyboard.KEYCODE_SHIFT) {
				keyboardView.onReleaseShift();
			}
		}

		@Override
		public void onKey(int paramInt, int[] paramArrayOfInt) {
			if (paramInt > 0) {
				char ch = (char) paramArrayOfInt[0];
				if (paramArrayOfInt[0] == -1) ch = (char)paramInt;
				
				if (keyboardView.isCtrled()) {
					if (Character.isLetter(ch)) {
						ch = (char) (paramInt - 0x60);
					}
					keyboardView.setCtrled(false);
				} else if (keyboardView.isShifted()) {
					if (Character.isLetter(ch)) {
						ch = (char) (paramInt - 0x20);
					} else if (paramArrayOfInt[1] != -1) {
						ch = (char) paramArrayOfInt[1];
					}
					if (!keyboardView.isCapsLock()) {
						keyboardView.setShifted(false);
					}
				}
				onChar(ch);
			}
		}
		

		@Override
		public void onText(CharSequence paramCharSequence) {}

		@Override
		public void swipeLeft() {}

		@Override
		public void swipeRight() {}

		@Override
		public void swipeDown() {}

		@Override
		public void swipeUp() {}

		
	}

}

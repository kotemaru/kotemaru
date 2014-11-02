package org.kotemaru.android.postit;

/**
 * 定数定義。
 * @author kotemaru.org
 */
public interface PostItConst {
	/** 付箋背景色の定義。 */
	public interface PostItColor {
		public static final int BLUE = 0;
		public static final int GREEN = 1;
		public static final int YELLOW = 2;
		public static final int PINK = 3;
		public static final int RED = 4;
	}

	/** 付箋フォントサイズの定義。単位はsp。 */
	public interface PostItFontSize {
		public static final int SMALL = 8;
		public static final int MIDDLE = 12;
		public static final int LAGE = 16;
		public static final int HUGE = 24;
	}

	/** 付箋サイズの定義。単位はdp。 */
	public interface PostItShape {
		public static final int W_SHORT = 90;
		public static final int W_LONG = 160;
		public static final int H_SMALL = 32;
		public static final int H_LAGE = 64;
	}
}

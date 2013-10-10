package org.kotemaru.android.adkterm;

import org.kotemaru.android.adkterm.vt100.EscSeqParser;
import org.kotemaru.android.logicasync.annotation.Logic;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class ConsoleView extends View {
	private static final char CR = 13;
	private static final char LF = 10;
	private static final char BS = 8;
	private static final char TAB = 9;
	private static final char ESC = 27;
	private static final char SPC = ' ';

	private Paint paint = new Paint();
	private int charWidth;
	private int lineHeight;
	private int rowSize;
	private int columnSize;
	private int cursorX = 0;
	private int cursorY = 0;
	private EscSeqParser escSeqParser = new EscSeqParser(this);
	private MainActivity activity;
	private ConsoleLog consoleLog;
    private GestureDetector gestureDetector; 
    
	public ConsoleView(Context context) {
		super(context);
		init(context);
	}

	public ConsoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		Config.init(context);
		gestureDetector = new GestureDetector(context, gestureListener); 
		paint.setTypeface(Typeface.MONOSPACE);
		paint.setTextSize((float)Config.getFontsize());
		paint.setAntiAlias(true);
		consoleLog = new ConsoleLog(Config.getLogsize());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		float[] widths = new float[1];
		paint.getTextWidths("W", widths);
		charWidth = (int) widths[0];
		lineHeight = (int) ((-paint.ascent() + paint.descent()) * 1.2);

		columnSize = (int) Math.floor(w / charWidth);
		rowSize =  (int) Math.floor(h / lineHeight);
		setScrollArea(0, rowSize);
		
		consoleLog.init(columnSize, rowSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float accent = paint.ascent();

		for (int lno = 0; lno < rowSize; lno++) {
			StringBuilder row = consoleLog.getViewRow(lno);
			canvas.drawText(row, 0, row.length(), 0,
					(int) (lineHeight * lno - accent), paint);
		}

		if (consoleLog.getOffset() == 0) {
			Xfermode mode = paint.getXfermode();
			// paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
			paint.setXfermode(new PixelXorXfermode(Color.WHITE));
			float x1 = getCursorX() * charWidth;
			float y1 = getCursorY() * lineHeight;
			float x2 = x1 + charWidth;
			float y2 = y1 + lineHeight * 0.85F;
			canvas.drawRect(x1, y1, x2, y2, paint);
			paint.setXfermode(mode);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;//super.onTouchEvent(event);
	}

	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
			consoleLog.moveOffset((int)(4*distanceY/lineHeight));
			invalidate();
			return true;
		}
	};

	public void append(CharSequence text) {
		consoleLog.setOffset(0);
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (escSeqParser.isAlive()) {
				if (escSeqParser.post(ch)) {
					escSeqParser.exec();
				}
			} else if (ch < 0x20) {
				ctrlChar(ch);
			} else {
				putChar(ch);
			}
		}
		this.invalidate();
	}

	private void putChar(char ch) {
		StringBuilder row = consoleLog.getRow(cursorY);
		row.setCharAt(cursorX++, ch);
		if (cursorX >= columnSize) {
			cursorX = 0;
			lineFeed();
		}
	}
	private boolean ctrlChar(char ch) {
		if (ch == LF) return lineFeed();
		if (ch == CR) return carriageReturn();
		if (ch == BS) return backSpace();
		if (ch == TAB) return tab();
		if (ch == ESC) return escSeqParser.post(ch);
		return false;
	}

	private boolean tab() {
		int cnt = 8 - (cursorX % 8); // TODO: tab size.
		for (int i=0; i<cnt; i++) putChar(SPC);
		return true;
	}

	private boolean carriageReturn() {
		cursorX = 0;
		return true;
	}

	private boolean backSpace() {
		--cursorX;
		if (cursorX < 0) {
			cursorX = columnSize-1;
			setCursorY(cursorY - 1);
		}
		//StringBuilder row = vram[cursorY];
		//row.setCharAt(cursorX, SPC);
		return true;
	}

	private boolean lineFeed() {
		setCursorY(cursorY+1);
		return true;
	}


	public void clearLine(int mode) {
		if (mode == 0) consoleLog.clear(cursorY, cursorX, columnSize);
		if (mode == 1) consoleLog.clear(cursorY, 0, cursorX);
		if (mode == 2) consoleLog.clear(cursorY, 0, columnSize);
	}
	public void clearScreen(int mode) {
		for (int i = 0; i < rowSize; i++) {
			if (i < cursorY) {
				if (mode == 1 || mode == 2) consoleLog.clear(i, 0, columnSize);
			} else if (i == cursorY) {
				clearLine(mode);
			} else if (i>cursorY) {
				if (mode == 0 || mode == 2) consoleLog.clear(i, 0, columnSize);
			}
		}
	}
	
	
	public int getCursorX() {
		return cursorX;
	}

	public void setCursorX(int x) {
		if (x < 0)	x = 0;
		if (x >= columnSize) x = columnSize-1;
		this.cursorX = x;
	}

	public int getCursorY() {
		return cursorY;
	}

	public void setCursorY(int y) {
		this.cursorY = consoleLog.autoScroll(y);
	}

	public void input(String text) {
		activity.input(text);
	}

	public void setScrollArea(int pt, int pb) {
		consoleLog.setScrollArea(pt,pb);
	}


	public MainActivity getActivity() {
		return activity;
	}

	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}

}

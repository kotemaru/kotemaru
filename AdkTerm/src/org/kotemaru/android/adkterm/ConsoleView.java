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
	private StringBuilder[] vram;
	private int cursorX = 0;
	private int cursorY = 0;
	private EscSeqParser escSeqParser = new EscSeqParser(this);
	private int scrollRowTop;
	private int scrollRowBottom;

	public ConsoleView(Context context) {
		super(context);
	}

	public ConsoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTypeface(Typeface.MONOSPACE);
		paint.setTextSize(14);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		float[] widths = new float[1];
		paint.getTextWidths("W", widths);
		charWidth = (int) widths[0];
		lineHeight = (int) ((-paint.ascent() + paint.descent()) * 1.2);

		columnSize = w / charWidth;
		rowSize = h / lineHeight;

		vram = new StringBuilder[rowSize];
		for (int i = 0; i < vram.length; i++) {
			vram[i] = new StringBuilder(columnSize + 4);
			for (int x = 0; x < columnSize; x++) {
				vram[i].append(SPC);
			}
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float accent = paint.ascent();

		for (int lno = 0; lno < vram.length; lno++) {
			StringBuilder row = vram[lno];
			canvas.drawText(row, 0, row.length(), 0,
					(int) (lineHeight * lno - accent), paint);
		}

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

	
	
	
	public void append(CharSequence text) {
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
		StringBuilder row = vram[cursorY];
		row.setCharAt(cursorX++, ch);
		if (cursorX > columnSize) {
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
		append("        "); // TODO
		return true;
	}

	private boolean carriageReturn() {
		cursorX = 0;
		return true;
	}

	private boolean backSpace() {
		--cursorX;
		if (cursorX < 0) {
			cursorX = columnSize;
			setCursorY(cursorY - 1);
		}
		StringBuilder row = vram[cursorY];
		row.setCharAt(cursorX, SPC);
		return true;
	}

	private boolean lineFeed() {
		cursorY++;
		if (cursorY > rowSize) {
			scrollUp();
			cursorY--;
		}
		return true;
	}

	private void scrollUp() {
		StringBuilder bottom = vram[0];
		for (int i = 1; i < vram.length; i++) {
			vram[i - 1] = vram[i];
		}
		vram[vram.length] = bottom;
		clear(bottom, 0, columnSize);
	}

	private void scrollDown() {
		StringBuilder top = vram[vram.length];
		for (int i = vram.length - 1; i >= 0; i--) {
			vram[i + 1] = vram[i];
		}
		vram[0] = top;
		clear(top, 0, columnSize);
	}

	private void clear(StringBuilder row, int start, int end) {
		for (int x = start; x < end; x++) {
			row.setCharAt(x, SPC);
		}
	}

	
	public void clearLine(int mode) {
		StringBuilder row = vram[cursorY];
		if (mode == 0) clear(row, cursorX, columnSize);
		if (mode == 1) clear(row, 0, cursorX);
		if (mode == 2) clear(row, 0, columnSize);
	}
	public void clearScreen(int mode) {
		for (int i = 0; i < vram.length; i++) {
			StringBuilder row = vram[i];
			if (i < cursorY) {
				if (mode == 1 || mode == 2) clear(row, 0, columnSize);
			} else if (i == cursorY) {
				clearLine(mode);
			} else if (i>cursorY) {
				if (mode == 0 || mode == 2) clear(row, 0, columnSize);
			}
		}
	}
	
	
	public int getCursorX() {
		return cursorX;
	}

	public void setCursorX(int x) {
		if (x < 0)
			x = 0;
		if (x > columnSize)
			x = columnSize;
		this.cursorX = x;
	}

	public int getCursorY() {
		return cursorY;
	}

	public void setCursorY(int y) {
		if (y < 0)
			y = 0;
		if (y > rowSize)
			y = rowSize;
		this.cursorY = y;
	}

	public void input(String string) {
		// TODO:
	}

	public void setScrollArea(int pt, int pb) {
		scrollRowTop = pt;
		scrollRowBottom = pb;
	}

	public void setConsoleLog(ConsoleLog consoleData) {
		// TODO Auto-generated method stub
		
	}

}

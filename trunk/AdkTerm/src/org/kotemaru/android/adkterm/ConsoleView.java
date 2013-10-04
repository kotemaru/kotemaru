package org.kotemaru.android.adkterm;

import java.util.LinkedList;

import org.kotemaru.android.logicasync.annotation.Logic;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

@Logic
public class ConsoleView extends View {

	
	private Paint paint = new Paint();
	private int charWidth;
	private int lineHeight;
	private ConsoleData consoleData;

	public ConsoleView(Context context) {
		super(context);
	}

	public ConsoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTypeface(Typeface.MONOSPACE);
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float[] widths = new float[1];
		paint.getTextWidths("W", widths);
		charWidth = (int) widths[0];
		lineHeight = (int)((-paint.ascent() + paint.descent()) * 1.2);
		int w = charWidth * consoleData.getColmunSize();
		int h = lineHeight * consoleData.getMaxLineSize();
		this.setMeasuredDimension(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float accent = paint.ascent();
		int lineCnt = 0;
		LinkedList<StringBuilder> lines = consoleData.getLines();
		for (StringBuilder line : lines) {
			canvas.drawText(line, 0, line.length(), 0, (int)(lineHeight*lineCnt-accent), paint);
			lineCnt++;
		}
		Xfermode mode = paint.getXfermode();
		//paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		paint.setXfermode(new PixelXorXfermode(Color.WHITE));
		float x1 = consoleData.getCursorX()*charWidth;
		float y1 = consoleData.getCursorY()*lineHeight;
		float x2 = x1 + charWidth;
		float y2 = y1 + lineHeight*0.8F;
		canvas.drawRect(x1, y1, x2, y2, paint);
		paint.setXfermode(mode);
	}


	public void append(CharSequence text) {
		consoleData.append(text);
		this.invalidate();
	}

	public ConsoleData getConsoleData() {
		return consoleData;
	}

	public void setConsoleData(ConsoleData consoleData) {
		this.consoleData = consoleData;
	}
	
}

package com.example.andtoidtesthandler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ObiTextView extends TextView {
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	
	private int obiColor;
	private Object mode;
	private Path path = new Path();
	private float fontSizeRate;

	public ObiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ObiTextView);
		this.mode = a.getString(R.styleable.ObiTextView_mode);
		this.obiColor = a.getColor(R.styleable.ObiTextView_color, Color.LTGRAY);
		this.fontSizeRate = a.getFloat(R.styleable.ObiTextView_fontSizeRate, 0.06F);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int sc = canvas.save();

		Paint p = getPaint();
		p.setTextSize(getWidth() * this.fontSizeRate);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		FontMetrics fontMetrics = p.getFontMetrics();
		String text = getText().toString();
		float textWidth = p.measureText(text);
		
		//canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		if (LEFT.equals(mode)) {
			// 左上斜め
			float baseX = getWidth()/2-textWidth/2;
			float baseY = getHeight()*0.05F;
			canvas.rotate(-45, getWidth()/2, getHeight()/2);
			p.setColor(obiColor);
			canvas.drawRect(0, baseY+fontMetrics.top, getWidth(), baseY+fontMetrics.bottom, p);
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		} else if (RIGHT.equals(mode)) {
			// 右上
			final float scale = getContext().getResources().getDisplayMetrics().density;
			float paddX = (4.0F * scale);
			float baseX = getWidth()-textWidth-paddX * 3;
			float baseY = getHeight()*0.3F;
			p.setColor(obiColor);
			canvas.drawRect(baseX-paddX, baseY+fontMetrics.top, getWidth()-paddX, baseY+fontMetrics.bottom, p);

			// 折り返しの三角形。
			p.setColor(toDarkColor(obiColor));
			p.setStyle(Paint.Style.FILL);
			path.reset();
			path.moveTo(getWidth()-paddX, baseY+fontMetrics.bottom);
			path.lineTo(getWidth()-paddX*2, baseY+fontMetrics.bottom);
			path.lineTo(getWidth()-paddX*2, baseY+fontMetrics.bottom+paddX);
			canvas.drawPath(path,p);
			
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		}
		
		canvas.restoreToCount(sc);
	}
	
	private int toDarkColor(int color) {
		int a = (color >> 24) & 0x0ff;
		int r = (int)(((color >> 16) & 0x0ff) * 0.7);
		int g = (int)(((color >> 8) & 0x0ff)  * 0.7);
		int b = (int)(((color >> 0) & 0x0ff) * 0.7);
		
		return a<<24 | r<<16 | g << 8 | b;
	}
	

}

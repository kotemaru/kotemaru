package com.example.andtoidtesthandler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.TextView;

public class ObiTextView extends TextView {
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	
	private int obiColor;
	private Object mode;

	public ObiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ObiTextView);
		this.mode = a.getString(R.styleable.ObiTextView_mode);
		this.obiColor = a.getColor(R.styleable.ObiTextView_color, Color.LTGRAY);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int sc = canvas.save();

		Paint p = getPaint();
		FontMetrics fontMetrics = p.getFontMetrics();
		String text = getText().toString();
		float textWidth = p.measureText(text);
		
		//canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		if (LEFT.equals(mode)) {
			// 左上斜め
			float baseX = getWidth()/2-textWidth/2;
			float baseY = getHeight()*0.1F;
			canvas.rotate(-45, getWidth()/2, getHeight()/2);
			p.setColor(obiColor);
			canvas.drawRect(0, baseY+fontMetrics.top, getWidth(), baseY+fontMetrics.bottom, p);
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		} else if (RIGHT.equals(mode)) {
			// 右上
			float paddX = getWidth()*0.05F;
			float baseX = getWidth()-textWidth-paddX;
			float baseY = getHeight()*0.3F;
			p.setColor(obiColor);
			canvas.drawRect(baseX-paddX, baseY+fontMetrics.top, getWidth(), baseY+fontMetrics.bottom, p);

			// 折り返しの三角形。
			final float scale = getContext().getResources().getDisplayMetrics().density;
			final float dp8 = (8.0F * scale); 
			p.setColor(toDarkColor(obiColor));
			p.setStyle(Paint.Style.FILL);
			Path path = new Path();
			path.moveTo(getWidth(), baseY+fontMetrics.bottom);
			path.lineTo(getWidth()-dp8, baseY+fontMetrics.bottom);
			path.lineTo(getWidth()-dp8, baseY+fontMetrics.bottom+dp8/2);
			canvas.drawPath(path,p);
			
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		}
		
		canvas.restoreToCount(sc);
	}
	
	private int toDarkColor(int color) {
		int a = (color >> 24) & 0x0ff;
		int r = (color >> 16) & 0x0ff / 2;
		int g = (color >> 8) & 0x0ff / 2 ;
		int b = (color >> 0) & 0x0ff / 2;
		
		return a<<24 | r<<16 | g << 8 | b;
	}
	

}

package com.example.andtoidtesthandler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.widget.TextView;

public class ObiTextView extends TextView {
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	
	private int objColor;
	private Object mode;

	public ObiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ObiTextView);
		this.mode = a.getString(R.styleable.ObiTextView_mode);
		this.objColor = a.getColor(R.styleable.ObiTextView_color, Color.LTGRAY);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
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
			p.setColor(objColor);
			canvas.drawRect(0, baseY+fontMetrics.top, getWidth(), baseY+fontMetrics.bottom, p);
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		} else {
			// 右上
			float paddX = getWidth()*0.05F;
			float baseX = getWidth()-textWidth-paddX;
			float baseY = getHeight()*0.3F;
			p.setColor(objColor);
			canvas.drawRect(baseX-paddX, baseY+fontMetrics.top, getWidth(), baseY+fontMetrics.bottom, p);
			p.setColor(this.getCurrentTextColor());
			canvas.drawText(text, baseX, baseY, p);
		}
	}

}

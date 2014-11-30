package org.kotemaru.android.postit.data;

import org.kotemaru.android.postit.PostItConst.PostItShape;


/**
 * 付箋データBean。
 * @author kotemaru.org
 */
public class PostItData {
	private long id;
	private int enabled;
	private int color; // 1-5
	private int posX; // px
	private int posY; // px
	private int width; // dp
	private int height; // dp
	private int fontSize; // sp
	private String memo;

	public PostItData() {
	}

	public PostItData(long id, int color, int posX, int posY) {
		this(id, 1, color, posX, posY, PostItShape.W_LONG, PostItShape.H_SMALL, 12, "");
	}

	public PostItData(long id, int enabled, int color, int posX, int posY, int width, int height, int fontSize, String memo) {
		this.id = id;
		this.enabled = enabled;
		this.color = color;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.fontSize = fontSize;
		this.memo = memo;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getEnabled() {
		return enabled;
	}
	public boolean isEnabled() {
		return enabled != 0;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled ? 1 : 0;
	}

	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

}

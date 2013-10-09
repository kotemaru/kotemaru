package org.kotemaru.android.adkterm;

public class ConsoleLog  {
	private static final char SPC = ' ';

	private StringBuilder[] vram;
	
	private int logSize;
	private int scrollRowTop;
	private int scrollRowBottom;
	private int viewRowTop;
	private int viewRowBottom;
	private int viewColumnTop = 0;
	private int viewColumnBottom;
	private int offset = 0;
	
	public ConsoleLog(int size) {
		this.logSize = size;
		this.vram = new StringBuilder[logSize];
	}
	
	public void init(int columnSize, int rowSize) {
		viewColumnBottom = columnSize;
		viewRowTop = logSize - rowSize;
		viewRowBottom = logSize;
		setScrollArea(viewRowTop, viewRowBottom);

		for (int i = 0; i < vram.length; i++) {
			vram[i] = new StringBuilder(columnSize + 4);
			for (int x = 0; x < columnSize; x++) {
				vram[i].append(SPC);
			}
		}
	}
	public void setOffset(int off) {
		offset = off;
		if (viewRowTop + offset < 0) {
			offset = logSize - viewRowTop;
		} else if (offset > 0) {
			offset = 0;
		}
	}
	public int getOffset() {
		return offset;
	}
	public void moveOffset(int delta) {
		setOffset(offset + delta);
	}
	public StringBuilder getViewRow(int y) {
		return vram[viewRowTop+offset+y];
	}
	
	public StringBuilder getRow(int y) {
		return vram[viewRowTop+y];
	}

	public void setScrollArea(int pt, int pb) {
		scrollRowTop = pt;
		scrollRowBottom = Math.min(pb+1, viewRowBottom);
	}
	
	public boolean isFullScrollArea() {
		return viewRowTop == scrollRowTop
				&& viewRowBottom == scrollRowBottom;
	}
	
	public void clear(int lno, int start, int end) {
		clear(getRow(lno), start, end);
	}
	
	public void clear(StringBuilder row, int start, int end) {
		for (int x = start; x < end; x++) {
			row.setCharAt(x, SPC);
		}
	}

	public void scrollUp() {
		int top = isFullScrollArea() ? 0 : scrollRowTop;
		
		StringBuilder raw = vram[top];
		for (int i = top+1; i < scrollRowBottom; i++) {
			vram[i - 1] = vram[i];
		}
		vram[scrollRowBottom-1] = raw;
		clear(raw, viewColumnTop, viewColumnBottom);
	}

	public void scrollDown() {
		StringBuilder raw = vram[scrollRowBottom-1];
		for (int i = scrollRowBottom-2; i >= scrollRowTop; i--) {
			vram[i + 1] = vram[i];
		}
		vram[scrollRowTop] = raw;
		clear(raw, viewColumnTop, viewColumnBottom);
	}

	public int autoScroll(int cursorY) {
		int lno = viewRowTop + cursorY;
				
		if (lno < scrollRowTop)	{
			scrollDown();
			return  scrollRowTop - viewRowTop;
		}
		if (lno >= scrollRowBottom) {
			scrollUp();
			return  scrollRowBottom - viewRowTop -1;
		}
		
		return cursorY;
	}

}

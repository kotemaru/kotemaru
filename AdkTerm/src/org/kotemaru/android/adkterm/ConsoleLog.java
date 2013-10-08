package org.kotemaru.android.adkterm;

public class ConsoleLog  {
	public ConsoleLog(int size) {
		
	}
/*
	private LinkedList<StringBuilder> lines = new LinkedList<StringBuilder>();
	private EscSeqParser escSeqParser = new EscSeqParser();

	private int maxLineSize = 300;
	private int colmunSize = 80;
	private int viewColumnSize;
	private int viewLineSize;
	private int cursorX = 0;
	private int cursorY = 0;
	private int textColor = Color.BLACK;
	
	
	
	public ConsoleData(int colmunSize, int maxLineSize) {
		this.colmunSize = colmunSize;
		this.maxLineSize = maxLineSize;
		this.setCursorX(0);
		this.setCursorY(maxLineSize-1);
		
		for (int i=0;i<maxLineSize; i++) {
			StringBuilder line = new StringBuilder(colmunSize+4);
			lines.add(line);
		}
		initEscSeqMap();
	}
	public void setViewSize(int w, int h) {
		viewColumnSize = w;
		viewLineSize = h;
	}

	public int getMaxLineSize() {
		return maxLineSize;
	}
	public int getColmunSize() {
		return colmunSize;
	}
	public LinkedList<StringBuilder> getLines() {
		return lines;
	}

	public void append(CharSequence text) {
		StringBuilder line = lines.getLast();
		for (int i=0; i<text.length(); i++) {
			char ch = text.charAt(i);
			int len = line.length();
			
			if (escSeqParser.isAlive()) {
				if (escSeqParser.post(ch)) {
					doEscSeq(escSeqParser);
				}
			} else if (ch < 0x20) {
				if (ch == LF) {
					line = lineFeed();
				} else if (ch == BS && len>0) {
					line.setLength(len-1);
				} else if (ch == TAB) {
					line.append("        "); //TODO:tab stop.
				} else if (ch == ESC) {
					escSeqParser.post(ch);
				}
			} else {
				if (len >= colmunSize) line = lineFeed();
				line.append(ch);
			}
		}
		cursorX = line.length();
	}


	private StringBuilder lineFeed() {
		StringBuilder line = lines.removeFirst();
		lines.addLast(line);
		line.setLength(0);
		return line;
	}
	
	public int getCursorX() {
		return cursorX;
	}
	public void setCursorX(int cursorX) {
		this.cursorX = cursorX;
	}
	public int getCursorY() {
		return cursorY;
	}
	public void setCursorY(int cursorY) {
		if (cursorX < 0) cursorX = 0;
		this.cursorY = cursorY;
	}
*/
}

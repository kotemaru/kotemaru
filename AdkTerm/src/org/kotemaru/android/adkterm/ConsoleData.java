package org.kotemaru.android.adkterm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.kotemaru.android.logicasync.annotation.Logic;

import android.graphics.Color;

@Logic
public class ConsoleData  {

	private static final char LF  = 10;
	private static final char BS  = 8;
	private static final char TAB = 9;
	private static final char ESC = 27;

	private LinkedList<StringBuilder> lines = new LinkedList<StringBuilder>();
	private EscSeqParser escSeqParser = new EscSeqParser();

	private int maxLineSize = 300;
	private int colmunSize = 80;
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

	private Map<Integer, EscSeqDriver> escSeqMap = new HashMap<Integer, EscSeqDriver>();
	private void initEscSeqMap() {
		escSeqMap.put(30000+'m', esc_m);
	}
	private void doEscSeq(EscSeqParser parser) {
		EscSeqDriver driver = escSeqMap.get(parser.getParsedCode());
		if (driver != null) {
			try {
				driver.exec(parser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}




	private class EscSeqParser {
		private static final String SET1   = "78=>DEHMZc";
		private static final String SET2   = "34568";
		private static final String PARAMS = "0123456789;";
		private static final String SET3   = "sABCDHfJKcghlmnqryx";
		
		private static final int NIL = 0;
		int mode = NIL;
		StringBuilder params = new StringBuilder(30);
		int parsedCode = -1;
		
		public boolean isAlive() {
			return mode != NIL;
		}
		private boolean next(int after) {
			mode = after;
			return false;
		}
		private boolean broken() {
			mode = NIL;
			return false;
		}
		private boolean finish(int no) {
			parsedCode = no;
			mode = NIL;
			return true;
		}
		public int getParsedCode() {
			return parsedCode;
		}
		
		public boolean post(char ch) {
			if (ch == ESC) {
				params.setLength(0);
				return next(ESC);
			}
			
			if (mode == ESC) {
				if (ch == '#') return next('#');
				if (ch == '[') return next('[');
				int idx = SET1.indexOf(ch);
				if (idx >= 0) return finish(10000+ch);
				return broken();
			} else if (mode == '#') {
				int idx = SET2.indexOf(ch);
				if (idx >= 0) return finish(20000+ch);
				return broken();
			} else if (mode == '[') {
				int idx = SET3.indexOf(ch);
				if (idx >= 0) return finish(30000+ch);
				idx = PARAMS.indexOf(ch);
				if (idx >= 0) {
					params.append(ch);
					return next('[');
				}
				return broken();
			}
			return broken(); // not arrival
		}
		
		public int[] getParams() {
			String[] parts = params.toString().split(";");
			int[] nums = new int[parts.length];
			for (int i=0; i<parts.length; i++) {
				nums[i] = Integer.valueOf(parts[i]);
			}
			return nums;
		}
		public String getLabel() {
			return "TODO:";
		}
	}
	
	interface EscSeqDriver {
		public void exec(EscSeqParser parser) throws Exception;
	}
	
	private int saveCursorX;
	private int saveCursorY;
	// ESC 7 : カーソル位置と属性を保存
	private EscSeqDriver esc7 = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			saveCursorX = cursorX;
			saveCursorY = cursorY;
		}
	};
	// ESC 8 : 保存したカーソル位置と属性を復帰
	private EscSeqDriver esc8 = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			setCursorX(saveCursorX);
			setCursorY(saveCursorY);
		}
	};
	// ESC = : アプリケーションキーパッドモードにセット
	// ESC > : 数値キーパッドモードにセット
	// ESC D : カーソルを一行下へ移動
	private EscSeqDriver escD = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			setCursorY(getCursorY()+1);
		}
	};
	
	// ESC E : 改行、カーソルを次行の最初へ移動
	private EscSeqDriver escE = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			setCursorX(0);
			setCursorY(getCursorY()+1);
		}
	};
	
	// ESC H : 現在の桁位置にタブストップを設定
	// ESC M : カーソルを一行上へ移動
	private EscSeqDriver escM = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			setCursorY(getCursorY()-1);
		}
	};
	
	// ESC Z : 端末IDシーケンスを送信
	
	// ESC c :	リセット
	// ESC # 3 : カーソル行を倍高、倍幅、トップハーフへ変更
	// ESC # 4 : カーソル行を倍高、倍幅、ボトムハーフへ変更
	// ESC # 5 : カーソル行を単高、単幅へ変更
	// ESC # 6 : カーソル行を単高、倍幅へ変更
	// ESC # 8 : 画面を文字‘E’で埋める

	// ESC [ n A		カーソルをPl行上へ移動
	// ESC [ n B		//	カーソルをPl行下へ移動
	// ESC [ Pc C		//	カーソルをPc桁右へ移動
	// ESC [ Pc D		//	カーソルをPc桁左へ移動
	// ESC [ Pl ; Pc H //		カーソルをPl行Pc桁へ移動
	// ESC [ Pl ; Pc f	//		カーソルをPl行Pc桁へ移動
	// ESC [ Ps J		//	画面を消去
	// ESC [ J			//カーソルから画面の終わりまでを消去
	// ESC [ 0 J
	// ESC [ 1 J	//	画面の始めからカーソルまでを消去
	// ESC [ 2 J	//	画面全体を消去
	// ESC [ Ps K	//		行を消去
	// ESC [ K		//カーソルから行の終わりまでを消去
	// ESC [ 0 K
	// ESC [ 1 K	//行の始めからカーソルまでを消去
	// ESC [ 2 K	//行全体を消去
	// ESC [ Pn c	//		装置オプションのレポート
	// ESC [ c		// オプションのレポート
	// ESC [ 0 c	//		レポートに対して次の応答が返る
	// ESC [ Ps g	// タブストップをクリア
	// ESC [ g		// カーソル位置のタブストップをクリア
	// ESC [ 0 g	//
	// ESC [ 3 g	// すべてのタブストップをクリア
	// ESC [ Ps ; …… ; Ps h //モードのセット
	// ESC [ Ps ; …… ; Ps l // モードのリセット
	// ESC [ Ps ; …… ; Ps m // 文字修飾の設定
	private EscSeqDriver esc_m = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int[] params = parser.getParams();
			int color = Color.BLACK;
			if (params.length == 2) {
				if (params[0] == 0) {
					if (params[1] == 30) color = Color.BLACK;
					if (params[1] == 34) color = Color.BLUE;
					if (params[1] == 32) color = Color.GREEN;
					if (params[1] == 36) color = Color.CYAN;
					if (params[1] == 31) color = Color.RED;
					if (params[1] == 35) color = 0xff880088;
					if (params[1] == 33) color = 0xffa52a2a;
					if (params[1] == 37) color = Color.LTGRAY;
				} else if (params[0] == 1) {
					if (params[1] == 30) color = Color.GRAY;
					if (params[1] == 34) color = 0xff000088;
					if (params[1] == 32) color = 0xff008800;
					if (params[1] == 36) color = 0xff008888;
					if (params[1] == 31) color = 0xff880000;
					if (params[1] == 35) color = 0xff800080;
					if (params[1] == 33) color = Color.YELLOW;
					if (params[1] == 37) color = Color.WHITE;
				}
			}
			setTextColor(color);
		}
	};
	
	// ESC [ Ps n // 端末状態のリポート
	// ESC [ Ps ; …… ; Ps ; q //LEDの設定
	// ESC [ Pt ; Pb r // スクロール範囲をPt行からPb行に設定
	// ESC [ 2 ; Ps y	// テスト診断を行う

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
		this.cursorY = cursorY;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
}

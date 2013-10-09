package org.kotemaru.android.adkterm.vt100;

import org.kotemaru.android.adkterm.ConsoleView;
import android.graphics.Color;


public class EscSeqDrivers {
	private static final char ESC = 27;

	private ConsoleView console;
	

	private int saveCursorX;
	private int saveCursorY;
	
	
	public EscSeqDrivers(ConsoleView consoleView) {
		console = consoleView;
	}

	
	
	// ESC 7 : カーソル位置と属性を保存
	public EscSeqDriver esc7 = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			saveCursorX = console.getCursorX();
			saveCursorY = console.getCursorY();
		}
	};
	// ESC 8 : 保存したカーソル位置と属性を復帰
	public EscSeqDriver esc8 = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(saveCursorX);
			console.setCursorY(saveCursorY);
		}
	};
	// ESC = : アプリケーションキーパッドモードにセット
	// ESC > : 数値キーパッドモードにセット
	// ESC D : カーソルを一行下へ移動
	public EscSeqDriver escD = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorY(console.getCursorY()+1);
		}
	};
	
	// ESC E : 改行、カーソルを次行の最初へ移動
	public EscSeqDriver escE = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(0);
			console.setCursorY(console.getCursorY()+1);
		}
	};
	
	// ESC H : 現在の桁位置にタブストップを設定
	// ESC M : カーソルを一行上へ移動
	public EscSeqDriver escM = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorY(console.getCursorY()-1);
		}
	};
	
	// ESC Z : 端末IDシーケンスを送信
	
	// ESC c :	リセット
	// ESC # 3 : カーソル行を倍高、倍幅、トップハーフへ変更
	// ESC # 4 : カーソル行を倍高、倍幅、ボトムハーフへ変更
	// ESC # 5 : カーソル行を単高、単幅へ変更
	// ESC # 6 : カーソル行を単高、倍幅へ変更
	// ESC # 8 : 画面を文字‘E’で埋める

	// ESC [ Pl A		カーソルをPl行上へ移動
	public EscSeqDriver esc_A = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorY(console.getCursorY()-p1(parser,1));
		}
	};
	
	// ESC [ Pl B		//	カーソルをPl行下へ移動
	public EscSeqDriver esc_B = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorY(console.getCursorY()+p1(parser,1));
		}
	};

	// ESC [ Pc C		//	カーソルをPc桁右へ移動
	public EscSeqDriver esc_C = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(console.getCursorX()-p1(parser,1));
		}
	};

	// ESC [ Pc D		//	カーソルをPc桁左へ移動
	public EscSeqDriver esc_D = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(console.getCursorX()+p1(parser,1));
		}
	};


	// ESC [ Pl ; Pc H //		カーソルをPl行Pc桁へ移動
	public EscSeqDriver esc_H = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(p2(parser,1)-1);
			console.setCursorY(p1(parser,1)-1);
		}
	};
	
	// ESC [ Pl ; Pc f	//		カーソルをPl行Pc桁へ移動
	public EscSeqDriver esc_f = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			console.setCursorX(p2(parser,1)-1);
			console.setCursorY(p1(parser,1)-1);
		}
	};
	
	// ESC [ Ps J		//	画面を消去
	// ESC [ J			//カーソルから画面の終わりまでを消去
	// ESC [ 0 J
	// ESC [ 1 J	//	画面の始めからカーソルまでを消去
	// ESC [ 2 J	//	画面全体を消去
	public EscSeqDriver esc_J = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int mode = p1(parser, 0);
			console.clearScreen(mode);
		}
	};
	
	// ESC [ Ps K	//		行を消去
	// ESC [ K		//カーソルから行の終わりまでを消去
	// ESC [ 0 K
	// ESC [ 1 K	//行の始めからカーソルまでを消去
	// ESC [ 2 K	//行全体を消去
	public EscSeqDriver esc_K = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int mode = p1(parser, 0);
			console.clearLine(mode);
		}
	};
	
	
	
	// ESC [ Pn c	//		装置オプションのレポート
	// ESC [ c		// オプションのレポート
	// ESC [ 0 c	//		レポートに対して次の応答が返る
	public EscSeqDriver esc_c = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int type = p1(parser, 0);
			console.input(ESC+"[?1;0c");
		}
	};

	
	// ESC [ Ps g	// タブストップをクリア
	// ESC [ g		// カーソル位置のタブストップをクリア
	// ESC [ 0 g	//
	// ESC [ 3 g	// すべてのタブストップをクリア
	
	// ESC [ Ps ; …… ; Ps h //モードのセット
	// ESC [ Ps ; …… ; Ps l // モードのリセット
	// ESC [ Ps ; …… ; Ps m // 文字修飾の設定
	public EscSeqDriver esc_m = new EscSeqDriver() {
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
			//console.setTextColor(color); TODO:
		}
	};
	
	// ESC [ Ps n // 端末状態のリポート
	public EscSeqDriver esc_n = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int type = p1(parser, 0);
			if (type == 5) {
				console.input(ESC+"[0n");
			} else if (type == 6) {
				int x = console.getCursorX()+1;
				int y = console.getCursorY()+1;
				console.input(ESC+"["+y+";"+x+"R");
			}
		}
	};	
	
	
	// ESC [ Ps ; …… ; Ps ; q //LEDの設定
	// ESC [ Pt ; Pb r // スクロール範囲をPt行からPb行に設定
	public EscSeqDriver esc_r = new EscSeqDriver() {
		public void exec(EscSeqParser parser) {
			int pt = p1(parser, 1);
			int pb = p2(parser, 9999);
			console.setScrollArea(pt-1, pb-1);
		}
	};	

	
	
	// ESC [ 2 ; Ps y	// テスト診断を行う


	/**
	 * 第一パラメータ取得。
	 * @param parser
	 * @param defo
	 * @return
	 */
	private int p1(EscSeqParser parser, int defo) {
		int[] params = parser.getParams();
		if (params.length>=1) return params[0];
		return defo;
	}
	/**
	 * 第二パラメータ取得
	 * @param parser
	 * @param defo
	 * @return
	 */
	private int p2(EscSeqParser parser, int defo) {
		int[] params = parser.getParams();
		if (params.length>=2) return params[1];
		return defo;
	}

	
}

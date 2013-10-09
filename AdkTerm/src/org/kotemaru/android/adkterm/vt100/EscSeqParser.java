package org.kotemaru.android.adkterm.vt100;

import org.kotemaru.android.adkterm.ConsoleView;

import android.util.Log;
import android.util.SparseArray;

public class EscSeqParser {
	private static final char ESC = 0x1b;

	private static final String SET1 = "78=>DEHMZc";
	private static final String SET2 = "34568";
	private static final String PARAMS = "0123456789;";
	private static final String SET3 = "sABCDHfJKcghlmnqryx";

	private static final int NIL = 0;
	private int mode = NIL;
	private StringBuilder params = new StringBuilder(30);
	private int parsedCode = -1;
	private EscSeqDrivers drivers;
	private StringBuilder log = new StringBuilder(30);

	public EscSeqParser(ConsoleView consoleView) {
		this.drivers = new EscSeqDrivers(consoleView);
		initEscSeqMap();
	}

	public boolean isAlive() {
		return mode != NIL;
	}

	private boolean next(int after) {
		mode = after;
		return false;
	}

	private boolean broken() {
		mode = NIL;
		log.setLength(0);
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
			log.setLength(0);
			return next(ESC);
		}

		log.append(ch);
		if (mode == ESC) {
			if (ch == '#') return next('#');
			if (ch == '[') return next('[');
			int idx = SET1.indexOf(ch);
			if (idx >= 0) return finish(10000 + ch);
			return broken();
		} else if (mode == '#') {
			int idx = SET2.indexOf(ch);
			if (idx >= 0) return finish(20000 + ch);
			return broken();
		} else if (mode == '[') {
			int idx = SET3.indexOf(ch);
			if (idx >= 0) return finish(30000 + ch);
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
		if (params.length() == 0) return new int[] {};
		String[] parts = params.toString().split(";");
		int[] nums = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			nums[i] = Integer.valueOf(parts[i]);
		}
		return nums;
	}

	public String getLabel() {
		return log.toString();
	}

	private SparseArray<EscSeqDriver> escSeqMap = new SparseArray<EscSeqDriver>();

	private void initEscSeqMap() {
		escSeqMap.put(10000 + '7', drivers.esc7);
		escSeqMap.put(10000 + '8', drivers.esc8);
		escSeqMap.put(10000 + 'D', drivers.escD);
		escSeqMap.put(10000 + 'E', drivers.escE);
		escSeqMap.put(10000 + 'M', drivers.escM);

		escSeqMap.put(30000 + 'A', drivers.esc_A);
		escSeqMap.put(30000 + 'B', drivers.esc_B);
		escSeqMap.put(30000 + 'C', drivers.esc_C);
		escSeqMap.put(30000 + 'D', drivers.esc_D);
		escSeqMap.put(30000 + 'H', drivers.esc_H);
		escSeqMap.put(30000 + 'f', drivers.esc_f);
		escSeqMap.put(30000 + 'J', drivers.esc_J);
		escSeqMap.put(30000 + 'K', drivers.esc_K);
		escSeqMap.put(30000 + 'c', drivers.esc_c);
		escSeqMap.put(30000 + 'm', drivers.esc_m);
		escSeqMap.put(30000 + 'n', drivers.esc_n);
		escSeqMap.put(30000 + 'r', drivers.esc_r);
	}

	public void exec() {
		EscSeqDriver driver = escSeqMap.get(getParsedCode());
		Log.d("EscSeqParser", "ESC:" + getLabel());
		broken();
		if (driver != null) {
			try {
				driver.exec(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

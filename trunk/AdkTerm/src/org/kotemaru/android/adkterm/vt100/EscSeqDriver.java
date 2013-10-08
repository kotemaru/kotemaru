package org.kotemaru.android.adkterm.vt100;

interface EscSeqDriver {
	public void exec(EscSeqParser parser) throws Exception;
}

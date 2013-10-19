package org.kotemaru.android.sample.nfc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "NFCSample";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView textView1 = (TextView) this.findViewById(R.id.textView1);

		// カードID取得。Activityはカード認識時起動に設定しているのでここで取れる。
		byte[] felicaIDm = new byte[]{0};
		Intent intent = getIntent();
		Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			felicaIDm = tag.getId();
		}
		
		
		NfcF nfc = NfcF.get(tag);
		try {
			nfc.connect();
			byte[] req = readWithoutEncryption(felicaIDm, 10);
			Log.d(TAG, "req:"+toHex(req));
			// カードにリクエスト送信
			byte[] res = nfc.transceive(req);
			Log.d(TAG, "res:"+toHex(res));
			nfc.close();
			// 結果を文字列に変換して表示
			textView1.setText(parse(res));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() , e);
			textView1.setText(e.toString());
		}
	}

	/**
	 * 履歴読み込みFelicaコマンドの取得。
	 * - Sonyの「Felicaユーザマニュアル抜粋」の仕様から。
	 * - サービスコードは http://sourceforge.jp/projects/felicalib/wiki/suica の情報から
	 * - 取得できる履歴数の上限は「製品により異なります」。
	 * @param idm カードのID
	 * @param size 取得する履歴の数
	 * @return Felicaコマンド
	 * @throws IOException
	 */
	private byte[] readWithoutEncryption(byte[] idm, int size)
			throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

		bout.write(0);			 // データ長バイトのダミー
		bout.write(0x06);		 // Felicaコマンド「Read Without Encryption」
		bout.write(idm);			 // カードID 8byte
		bout.write(1);			 // サービスコードリストの長さ(以下２バイトがこの数分繰り返す)
		bout.write(0x0f);		 // 履歴のサービスコード下位バイト
		bout.write(0x09);		 // 履歴のサービスコード上位バイト
		bout.write(size);		 // ブロック数
		for (int i = 0; i < size; i++) {
			bout.write(0x80);	 // ブロックエレメント上位バイト 「Felicaユーザマニュアル抜粋」の4.3項参照
			bout.write(i);		 // ブロック番号
		}

		byte[] msg = bout.toByteArray();
		msg[0] = (byte) msg.length;
		return msg;
	}

	/**
	 * 履歴Felica応答の解析。
	 * @param res Felica応答
	 * @return 文字列表現
	 * @throws Exception
	 */
	private String parse(byte[] res) throws Exception {
		// res[0] = データ長
		// res[1] = 0x07
		// res[2〜9] = カードID
		// res[10,11] = エラーコード。0=正常。 
		if (res[10] != 0x00) throw new RuntimeException("Felica error.");

		// res[12] = 応答ブロック数
		// res[13+n*16] = 履歴データ。16byte/ブロックの繰り返し。
		int size = res[12];
		String str = "";
		for (int i = 0; i < size; i++) {
			Rireki rireki = Rireki.parse(res, 13 + i * 16);
			str += rireki.toString() +"\n";
		}
		return str;
	}

	private String toHex(byte[] id) {
		StringBuilder sbuf = new StringBuilder();
		for (int i = 0; i < id.length; i++) {
			String hex = "0" + Integer.toString((int) id[i] & 0x0ff, 16);
			if (hex.length() > 2)
				hex = hex.substring(1, 3);
			sbuf.append(" " + i + ":" + hex);
		}
		return sbuf.toString();
	}

}

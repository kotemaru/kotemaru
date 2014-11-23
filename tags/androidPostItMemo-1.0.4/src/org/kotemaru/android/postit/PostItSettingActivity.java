package org.kotemaru.android.postit;

import org.kotemaru.android.postit.PostItConst.PostItColor;
import org.kotemaru.android.postit.PostItConst.PostItFontSize;
import org.kotemaru.android.postit.PostItConst.PostItShape;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.util.IntIntMap;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.widget.RadioLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * 付箋データの編集画面。
 * <li>メモ、形状、フォントサイズ、色の編集。
 * <li>Activityを終了すると自動的にデータを保存する。キャンセルはできない。
 * @author kotemaru.org
 */
public class PostItSettingActivity extends Activity {
	/** ラジオボタンと色コードのマップ。 */
	private static final IntIntMap sColorRadioMap = new IntIntMap(new int[][] {
			{ R.id.color_blue, PostItColor.BLUE, },
			{ R.id.color_green, PostItColor.GREEN, },
			{ R.id.color_yellow, PostItColor.YELLOW, },
			{ R.id.color_pink, PostItColor.PINK, },
			{ R.id.color_red, PostItColor.RED, },
	});
	/** ラジオボタンとフォントサイズのマップ */
	private static final IntIntMap sFontRadioMap = new IntIntMap(new int[][] {
			{ R.id.font_small, PostItFontSize.SMALL, },
			{ R.id.font_middle, PostItFontSize.MIDDLE, },
			{ R.id.font_lage, PostItFontSize.LAGE, },
			{ R.id.font_huge, PostItFontSize.HUGE, },
	});
	/** ラジオボタンと付箋サイズのマップ */
	private static final IntIntMap sShapeRadioMap = new IntIntMap(new int[][] {
			{ R.id.shape_shot, PostItShape.W_SHORT, PostItShape.H_SMALL },
			{ R.id.shape_long, PostItShape.W_LONG, PostItShape.H_SMALL },
			{ R.id.shape_lage, PostItShape.W_LONG, PostItShape.H_LAGE },
	});

	private PostItData mPostItData;
	private EditText mMemo;
	private RadioLayout mShapeRadioGroup;
	private RadioGroup mFontRadioGroup;
	private RadioLayout mColorRadioGroup;

	/**
	 * intetntパラメータ
	 * <li>| POST_IT_ID | long型 | 必須 | 付箋ID。|
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_it_setting_activity);

		Intent intent = getIntent();
		long postItId = intent.getLongExtra(Launcher.POST_IT_ID, -1);
		mPostItData = PostItDataProvider.getPostItData(this, postItId);

		mMemo = (EditText) findViewById(R.id.memo);
		mShapeRadioGroup = (RadioLayout) findViewById(R.id.shape_radio_group);
		mFontRadioGroup = (RadioGroup) findViewById(R.id.font_radio_group);
		mColorRadioGroup = (RadioLayout) findViewById(R.id.color_radio_group);
	}

	/**
	 * 付箋データから各Viewの値を設定。
	 */
	@Override
	public void onResume() {
		super.onResume();

		// restore settings.
		mMemo.setText(mPostItData.getMemo());
		mShapeRadioGroup.check(sShapeRadioMap.getFirst(mPostItData.getWidth(), mPostItData.getHeight()));
		mFontRadioGroup.check(sFontRadioMap.getFirst(mPostItData.getFontSize()));
		mColorRadioGroup.check(sColorRadioMap.getFirst(mPostItData.getColor()));
	}

	/**
	 * 各Viewの値から付箋データを更新。
	 */
	@Override
	public void onPause() {
		// save settings.
		mPostItData.setMemo(mMemo.getText().toString());

		int shapeResId = mShapeRadioGroup.getCheckedRadioButtonId();
		mPostItData.setWidth(sShapeRadioMap.getSecond(shapeResId));
		mPostItData.setHeight(sShapeRadioMap.getThird(shapeResId));

		int fontResId = mFontRadioGroup.getCheckedRadioButtonId();
		mPostItData.setFontSize(sFontRadioMap.getSecond(fontResId));

		int colorResId = mColorRadioGroup.getCheckedRadioButtonId();
		mPostItData.setColor(sColorRadioMap.getSecond(colorResId));

		// DBに保存
		PostItDataProvider.updatePostItData(this, mPostItData);
		Launcher.notifyChangeData(this);

		super.onPause();
	}

}

package org.kotemaru.android.postit;

import org.kotemaru.android.postit.data.PostItColor;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.util.IntIntMap;
import org.kotemaru.android.postit.widget.RadioLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;

public class PostItSettingActivity extends Activity {
	public static final String POST_IT_ID = "POST_IT_ID";
	private static final IntIntMap sColorRadioMap = new IntIntMap(new int[][] {
			{ R.id.color_blue, PostItColor.BLUE, },
			{ R.id.color_green, PostItColor.GREEN, },
			{ R.id.color_yellow, PostItColor.YELLOW, },
			{ R.id.color_pink, PostItColor.PINK, },
			{ R.id.color_red, PostItColor.RED, },
	});
	private static final IntIntMap sFontRadioMap = new IntIntMap(new int[][] {
			{ R.id.font_small, 8, },
			{ R.id.font_middle, 12, },
			{ R.id.font_lage, 16, },
			{ R.id.font_huge, 24, },
	});
	private static final IntIntMap sShapeRadioMap = new IntIntMap(new int[][] {
			{ R.id.shape_shot, PostItData.W_SHORT, PostItData.H_SMALL },
			{ R.id.shape_long, PostItData.W_LONG, PostItData.H_SMALL },
			{ R.id.shape_lage, PostItData.W_LONG, PostItData.H_LAGE },
	});

	private PostItData mPostItData;
	private EditText mMemo;
	private RadioLayout mShapeRadioGroup;
	private RadioGroup mFontRadioGroup;
	private RadioLayout mColorRadioGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_it_setting_activity);

		Intent intent = getIntent();
		long postItId = intent.getLongExtra(POST_IT_ID, -1);
		mPostItData = PostItDataProvider.getPostItData(this, postItId);

		mMemo = (EditText) findViewById(R.id.memo);
		mShapeRadioGroup = (RadioLayout) findViewById(R.id.shape_radio_group);
		mFontRadioGroup = (RadioGroup) findViewById(R.id.font_radio_group);
		mColorRadioGroup = (RadioLayout) findViewById(R.id.color_radio_group);
	}

	@Override
	public void onResume() {
		super.onResume();
		mMemo.setText(mPostItData.getMemo());

		// restore settings.
		mShapeRadioGroup.check(sShapeRadioMap.getFirst(mPostItData.getWidth(), mPostItData.getHeight()));
		mFontRadioGroup.check(sFontRadioMap.getFirst(mPostItData.getFontSize()));
		mColorRadioGroup.check(sColorRadioMap.getFirst(mPostItData.getColor()));
	}

	@Override
	public void onPause() {
		Log.e("DEBUG", "onPause()");
		mPostItData.setMemo(mMemo.getText().toString());

		// save settings.
		int shapeResId = mShapeRadioGroup.getCheckedRadioButtonId();
		mPostItData.setWidth(sShapeRadioMap.getSecond(shapeResId));
		mPostItData.setHeight(sShapeRadioMap.getThird(shapeResId));

		int fontResId = mFontRadioGroup.getCheckedRadioButtonId();
		mPostItData.setFontSize(sFontRadioMap.getSecond(fontResId));

		int colorResId = mColorRadioGroup.getCheckedRadioButtonId();
		mPostItData.setColor(sColorRadioMap.getSecond(colorResId));

		PostItDataProvider.updatePostItData(this, mPostItData);
		super.onPause();
	}

}

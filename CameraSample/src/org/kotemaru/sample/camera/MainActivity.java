package org.kotemaru.sample.camera;

import java.io.IOException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "CameraSample";

	//private int CAMERA_ID = CameraInfo.CAMERA_FACING_FRONT;
	private int CAMERA_ID = 0; // for Nexus7

	private Camera camera;
	private SurfaceView surfaceView;
	private CameraListener cameraListener = new CameraListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		surfaceView = new SurfaceView(this);
		setContentView(surfaceView);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(cameraListener);
	}

	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}

	private class CameraListener implements
			SurfaceHolder.Callback,
			AutoFocusCallback,
			Camera.PictureCallback,
			Camera.PreviewCallback
	{

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open(CAMERA_ID);
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e(TAG, e.toString(), e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {

			// カメラのプレビューサイズをViewに設定
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getSupportedPreviewSizes().get(0); // 0=最大サイズ 
			parameters.setPreviewSize(size.width, size.height);
			camera.setParameters(parameters);

			// 画面回転補正。
			ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
			if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				camera.setDisplayOrientation(90);
				layoutParams.width = size.height;
				layoutParams.height = size.width;
			} else {
				camera.setDisplayOrientation(0);
				layoutParams.width = size.width;
				layoutParams.height = size.height;
			}
			surfaceView.setLayoutParams(layoutParams);

			// オートフォーカス設定。
			camera.autoFocus(cameraListener);

			camera.startPreview();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.autoFocus(null);
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				Log.d(TAG, "focus");
				// プレビューのデータ取得。
				camera.setPreviewCallback(cameraListener);
				// フルサイズ画像はTODO
				//camera.takePicture(null,null,cameraListener);
			}
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// フルサイズ画像もやることは同じ。
			onPreviewFrame(data, camera);
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// 処理は１回なのでコールバック取り消し
			camera.setPreviewCallback(null);

			// 基礎データ取得
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(CAMERA_ID, info);
			int w = camera.getParameters().getPreviewSize().width;
			int h = camera.getParameters().getPreviewSize().height;
			//int w = camera.getParameters().getPictureSize().width;
			//int h = camera.getParameters().getPictureSize().height;
			boolean isMirror = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);

			// プレビュー画像の型変換
			PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
					data, w, h, 0, 0, w, h, isMirror);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// QRコード読み込み。
			Reader reader = new MultiFormatReader();
			try {
				Log.d(TAG, "decode");
				Result result = reader.decode(bitmap);
				String text = result.getText();
				
				Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
				Log.i(TAG, "result:" + text);
				camera.stopPreview();
				camera.autoFocus(null);
			} catch (Exception e) {
				// QRコード認識失敗でも例外発生する。
				Log.d(TAG, "decode-fail:" + e.toString());
				camera.autoFocus(cameraListener);
			}

		}

	};
}
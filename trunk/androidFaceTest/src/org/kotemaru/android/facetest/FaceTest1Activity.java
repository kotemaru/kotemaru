package org.kotemaru.android.facetest;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaceTest1Activity extends Activity {
	private static final String TAG = "FaceTest";

	private Camera camera;
	private SurfaceView preview;
	private SurfaceView overlay;
	private CameraListener cameraListener;
	private OverlayListener overlayListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facetest);

		preview = (SurfaceView) findViewById(R.id.preview);
		cameraListener = new CameraListener(preview);

		overlay = (SurfaceView) findViewById(R.id.overlay);
		overlayListener = new OverlayListener(overlay);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		preview.getHolder().addCallback(cameraListener);
		overlay.getHolder().addCallback(overlayListener);
	}

	private class CameraListener implements
			SurfaceHolder.Callback,
			Camera.FaceDetectionListener
	{
		private SurfaceView surfaceView;
		private SurfaceHolder surfaceHolder;
		private byte[] previewRawData;

		public CameraListener(SurfaceView surfaceView) {
			this.surfaceView = surfaceView;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			surfaceHolder = holder;
			try {
				int cameraId = -1;
				// フロントカメラを探す。
				Camera.CameraInfo info = new Camera.CameraInfo();
				for (int id = 0; id < Camera.getNumberOfCameras(); id++) {
					Camera.getCameraInfo(id, info);
					if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
						cameraId = id;
						break;
					}
				}
				camera = Camera.open(cameraId);
				camera.setPreviewDisplay(holder);
				camera.getParameters().setPreviewFpsRange(1, 20);
				camera.setDisplayOrientation(90); // portrate 固定
				// 顔認証機能サポートチェック。
				if (camera.getParameters().getMaxNumDetectedFaces() == 0) {
					throw new Error("Not supported face detected.");
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString(), e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			surfaceHolder = holder;
			camera.startPreview();
			camera.setFaceDetectionListener(cameraListener);
			camera.startFaceDetection();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.setFaceDetectionListener(null);
			camera.release();
			camera = null;
		}

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length == 0) return;
			Face face = faces[0];
			if (face.score < 30) return;
			if (previewRawData == null) return;
			
			overlayListener.drawFace(faceRect2PixelRect(face), Color.RED);
		}

		/**
		 * 顔認識範囲を描画用に座標変換する。
		 * - Face.rect の座標系はプレビュー画像に対し -1000～1000 の相対座標。
		 * - 座標(-1000,-1000)が左上、座標(0,0) が画像中心となる。
		 * - 座標系のプレビュー画像はlandscapeとなる。portraitの場合が90度回転が必要。
		 * @param face 顔認識情報
		 * @return 描画用矩形範囲
		 */
		private Rect faceRect2PixelRect(Face face) {
			int w = surfaceView.getWidth();
			int h = surfaceView.getHeight();
			Rect rect = new Rect();

			// フロントカメラなので左右反転、portraitなので座標軸反転
			rect.left = w * (-face.rect.top + 1000) / 2000;
			rect.right = w * (-face.rect.bottom + 1000) / 2000;
			rect.top = h * (face.rect.left + 1000) / 2000;
			rect.bottom = h * (face.rect.right + 1000) / 2000;
			//Log.d(TAG, "rect=" + face.rect + "=>" + rect);
			return rect;
		}

	}

	private class OverlayListener implements SurfaceHolder.Callback
	{
		private SurfaceView surfaceView;
		private SurfaceHolder surfaceHolder;

		private Paint paint = new Paint();

		public OverlayListener(SurfaceView surfaceView) {
			this.surfaceView = surfaceView;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			surfaceHolder = holder;
			surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(surfaceView.getWidth() / 100);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			surfaceHolder = holder;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// nop.
		}

		public void drawFace(Rect rect1, int color) {
			try {
				Canvas canvas = surfaceHolder.lockCanvas();
				if (canvas != null) {
					try {
						canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						paint.setColor(color);
						canvas.drawRect(rect1, paint);
					} finally {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			} catch (IllegalArgumentException e) {
				Log.w(TAG, e.toString());
			}
		}

	}
}

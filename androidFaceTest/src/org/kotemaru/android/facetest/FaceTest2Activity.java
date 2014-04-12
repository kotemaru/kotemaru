package org.kotemaru.android.facetest;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaceTest2Activity extends Activity {
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
			Camera.PreviewCallback
	{
		private SurfaceView surfaceView;
		private SurfaceHolder surfaceHolder;
		private Rect faceRect = new Rect();;

		public CameraListener(SurfaceView surfaceView) {
			this.surfaceView = surfaceView;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			surfaceHolder = holder;
			try {
				int cameraId = -1;
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
				List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
				camera.getParameters().setPreviewSize(sizes.get(0).width, sizes.get(0).height);
				camera.getParameters().setPreviewFpsRange(1, 20);
				camera.setDisplayOrientation(90); // portrate 固定
			} catch (Exception e) {
				Log.e(TAG, e.toString(), e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			surfaceHolder = holder;
			camera.setPreviewCallback(this);
			camera.startPreview();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Bitmap image = decodePreview(data);
			
			FaceDetector faceDetector = new FaceDetector(image.getWidth(), image.getHeight(), 1);
			FaceDetector.Face[] faces = new FaceDetector.Face[1];
			int n = faceDetector.findFaces(image, faces);

			if (n>0) {
				PointF midPoint = new PointF(0, 0);
				faces[0].getMidPoint(midPoint); // 顔認識結果を取得
				float eyesDistance = faces[0].eyesDistance(); // 顔認識結果を取得
				faceRect.left = (int) (midPoint.x - eyesDistance / 2);
				faceRect.top = (int) (midPoint.y - eyesDistance / 2);
				faceRect.right = (int) (midPoint.x + eyesDistance / 2);
				faceRect.bottom = (int) (midPoint.y + eyesDistance / 2);
			}
			overlayListener.drawFace(faceRect, Color.YELLOW, image);
		}
		
		
		private int[] rgb;
		private Bitmap tmpImage ;
		private Bitmap decodePreview(byte[] data) {
			int width = camera.getParameters().getPreviewSize().width;
			int height = camera.getParameters().getPreviewSize().height;
			if (rgb == null) {
				rgb = new int[width*height];
				tmpImage = Bitmap.createBitmap(height ,width , Bitmap.Config.RGB_565);
			}

			decodeYUV420SP(rgb, data, width, height);
			tmpImage.setPixels(rgb, 0, height, 0, 0, height, width);
			return tmpImage;
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

		public void drawFace(Rect rect1, int color, Bitmap previewImage) {
			try {
				Canvas canvas = surfaceHolder.lockCanvas();
				if (canvas != null) {
					try {
						//canvas.drawBitmap(previewImage,0,0, paint);
						canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						canvas.scale(
								(float)surfaceView.getWidth()/previewImage.getWidth(), 
								(float)surfaceView.getHeight()/previewImage.getHeight());
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

	// from https://code.google.com/p/android/issues/detail?id=823
    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;
    	
    	for (int j = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++) {
    			int srcp = j*width + i;
    			int y = (0xff & ((int) yuv420sp[srcp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			// 90度回転
    			int xx = height-j-1;
    			int yy = width-i-1;
    			int dstp = yy * height + xx;
    			rgb[dstp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }
}

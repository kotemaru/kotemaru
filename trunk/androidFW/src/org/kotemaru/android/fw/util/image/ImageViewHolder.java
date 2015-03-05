package org.kotemaru.android.fw.util.image;

import org.kotemaru.android.fw.util.image.ImageLoader.OnLoadImageListener;

import android.widget.ImageView;

class ImageViewHolder {
	public ImageView mImageView;
	public OnLoadImageListener mListener;
	public ImageViewHolder(ImageView imageView, OnLoadImageListener listener) {
		mImageView = imageView;
		mListener = listener;
	}
}

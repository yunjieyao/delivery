package com.app.imgcache;

import android.graphics.Bitmap;

public interface ImageBlock {
	
	public static final int NO_NET = 0;

	public static final int URL_FORMAT_ERROR = 1;

	public static final int CONN_TIME_OUT = 2;

	public static final int READ_TIME_OUT = 3;

    void onImageloaded(String url,Bitmap bitmap);

	void onDowndloadFailure(int type);

}

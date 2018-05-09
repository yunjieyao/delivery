package com.app.safe;

import android.content.Context;

public class SafeControl {
	private static final SafeControl SAFE_CONTROL = new SafeControl();

	@SuppressWarnings("unused")
	private Context mContext;

	private SafeControl() {
	}

	public static SafeControl getSafeControl() {
		return SAFE_CONTROL;
	}

	public void init(Context context) {
		this.mContext = context;
	}
}

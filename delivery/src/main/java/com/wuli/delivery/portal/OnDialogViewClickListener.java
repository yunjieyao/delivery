package com.wuli.delivery.portal;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public interface OnDialogViewClickListener {
    void onDialogViewClick(DialogFragment fragment, View view, Bundle extra);
}
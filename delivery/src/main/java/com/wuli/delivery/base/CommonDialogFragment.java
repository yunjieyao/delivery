package com.wuli.delivery.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.log.Log;
import com.wuli.delivery.App;
import com.wuli.delivery.R;
import com.wuli.delivery.portal.OnDialogViewClickListener;
import com.wuli.delivery.utils.AndroidUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用的对话框控件，支持一个按钮和两个按钮的显示<br/>
 * <p>
 * usage：
 * <In BaseActivity>
 * <p>
 * CommonDialogFragment.createWithConfirmAndCancelButton("Title", "message content!!!", null, null).show(this);
 * 当Title为空时，对话框将不显示标题
 * <p>
 */

public class CommonDialogFragment extends DialogFragment {

    private static final boolean DEFAULT_DIALOG_CANCELABLE = false;
    private static final int DEFAULT_BUTTON_LEFT_COLOR = Color.parseColor("#666666");
    private static final int DEFAULT_BUTTON_RIGHT_COLOR = Color.parseColor("#80af1e");
    private static final int MAX_LENGTH_SINGLELINE = 14;

    private CharSequence mTitle;
    private CharSequence mContentMsg;
    private CharSequence mSubContentMsg;
    private ButtonConfig mLeftBtnConfig;
    private ButtonConfig mRightBtnConfig;
    private boolean mIsGoodsNamesingleLine;
    private View.OnClickListener mCloseListener;
    private int mTitleDrawable = -1;
    private static final int INVALID = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(getActivity(), mTitle, mContentMsg, mSubContentMsg, mLeftBtnConfig, mRightBtnConfig,
                mCloseListener, mTitleDrawable, mIsGoodsNamesingleLine);
    }

    public static CommonDialogFragment create(CharSequence message, CharSequence subMessage,
                                              ButtonConfig leftButton, ButtonConfig rightButton) {
        return create(message, subMessage, leftButton, rightButton, DEFAULT_DIALOG_CANCELABLE);
    }

    public static CommonDialogFragment create(CharSequence message, CharSequence subMessage,
                                              ButtonConfig leftButton, ButtonConfig rightButton,
                                              boolean cancelable) {
        CommonDialogFragment dialog = new CommonDialogFragment();
        dialog.mTitle = null;
        dialog.mContentMsg = message;
        dialog.mSubContentMsg = subMessage;
        dialog.mLeftBtnConfig = leftButton;
        dialog.mRightBtnConfig = rightButton;
        dialog.setCancelable(cancelable);
        return dialog;
    }

    /**
     * 创建包含确认和取消按钮的对话框
     *
     * @param message
     * @return
     */
    public static CommonDialogFragment createWithConfirmAndCancelButton(
            CharSequence message,
            OnDialogViewClickListener leftListener,
            OnDialogViewClickListener rightListener) {
        Resources res = App.getContext().getResources();
        return createWithTwoButtons(message,
                res.getText(R.string.action_no), leftListener,
                res.getText(R.string.action_yes), rightListener);
    }

    public static CommonDialogFragment createWithOneButton(
            CharSequence message,
            CharSequence confirmText, OnDialogViewClickListener listener) {
        return create(message, null, null, new ButtonConfig(confirmText, DEFAULT_BUTTON_LEFT_COLOR, listener));
    }

    public static CommonDialogFragment createWithTwoButtons(
            CharSequence message,
            CharSequence leftText, OnDialogViewClickListener leftListener,
            CharSequence rightText, OnDialogViewClickListener rightListener) {
        return create(message, null,
                new ButtonConfig(leftText, DEFAULT_BUTTON_LEFT_COLOR, leftListener),
                new ButtonConfig(rightText, DEFAULT_BUTTON_RIGHT_COLOR, rightListener));
    }

    public static CommonDialogFragment createWithTwoButtonsAndSubMessage(
            CharSequence message, CharSequence subMessage,
            CharSequence leftText, OnDialogViewClickListener leftListener,
            CharSequence rightText, OnDialogViewClickListener rightListener) {
        return create(message, subMessage,
                new ButtonConfig(leftText, DEFAULT_BUTTON_LEFT_COLOR, leftListener),
                new ButtonConfig(rightText, DEFAULT_BUTTON_RIGHT_COLOR, rightListener));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public CommonDialogFragment setTitleDrawable(int drawableId) {
        mTitleDrawable = drawableId;
        return this;
    }

    public CommonDialogFragment setCloseListener(View.OnClickListener closeListener) {
        mCloseListener = closeListener;
        return this;
    }

    public CommonDialogFragment setGoodsNamesingleLine(boolean isGoodsNamesingleLine) {
        mIsGoodsNamesingleLine = isGoodsNamesingleLine;
        return this;
    }

    /**
     * tag 用做对话框的唯一标识，在展示的时候移除已存在的对话框
     *
     * @param activity
     * @param tag
     */
    public void show(FragmentActivity activity, String tag) {
        if (AndroidUtils.isValidContext(activity)) {
            show(activity.getSupportFragmentManager(), tag);
        }
    }

    /**
     * 创建通用型的对话框
     *
     * @param context
     * @param message       正文消息
     * @param closeListener
     * @return
     */
    private FixedDialog createDialog(Context context, CharSequence title, CharSequence message, CharSequence subMessage,
                                     final ButtonConfig leftButton,
                                     final ButtonConfig rightButton,
                                     final View.OnClickListener closeListener,
                                     final int titleDrawableId, boolean isGoodsNamesingleLine) {
        final FixedDialog dialog = new FixedDialog(context);
        dialog.applyTitleDrawable(titleDrawableId);

        if (leftButton == null || TextUtils.isEmpty(leftButton.label)) {
            dialog.mCancelView.setVisibility(View.GONE);
        } else {
            dialog.mCancelView.setText(leftButton.label);
//            dialog.mCancelView.setTextColor(leftButton.color);
            dialog.mCancelView.setEnabled(leftButton.enable);
            dialog.mCancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (leftButton.listener != null) {
                        leftButton.listener.onDialogViewClick(CommonDialogFragment.this, dialog.mCancelView, null);
                    }
                }
            });
        }


        if (rightButton == null || TextUtils.isEmpty(rightButton.label)) {
            dialog.mConfirmView.setVisibility(View.GONE);
        } else {
            dialog.mConfirmView.setText(rightButton.label);
//            dialog.mConfirmView.setTextColor(rightButton.color);
            dialog.mConfirmView.setEnabled(rightButton.enable);
            dialog.mConfirmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (rightButton.listener != null) {
                        rightButton.listener.onDialogViewClick(CommonDialogFragment.this, dialog.mConfirmView, null);
                    }
                }
            });
        }

        if (closeListener != null) {
            dialog.mCloseView.setVisibility(View.VISIBLE);
            dialog.mCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    closeListener.onClick(v);
                }
            });
        } else {
            dialog.mCloseView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(message)) {
            if (isGoodsNamesingleLine) {
                dialog.mMessageTv.setText(getFixedMessage(message));
            } else {
                dialog.mMessageTv.setText(message);
            }
        }

        if (!TextUtils.isEmpty(subMessage)) {
            dialog.mSubMessageTv.setText(subMessage);
            dialog.mSubMessageTv.setVisibility(View.VISIBLE);
        } else {
            dialog.mSubMessageTv.setVisibility(View.GONE);
        }


        return dialog;
    }

    public String getFixedMessage(CharSequence message) {

        StringBuilder sb = new StringBuilder();
        String[] messageArray = message.toString().split("\n", 5);

        int length = messageArray.length;

        if (length == 1) {
            return message.toString();
        } else {
            sb.append(messageArray[0]).append("\n");
            //中间是商品名称，当商品名称超过一行最大显示范围时
            for (int i = 1; i < length; i++) {
                if (i == length - 1) {
                    sb.append(messageArray[length - 1]);
                } else {
                    sb.append(getMaxLengthString(messageArray[i])).append("\n");
                }
            }

            return sb.toString();
        }

    }

    public String getMaxLengthString(String message) {
        int length = message.length();
        if (length > MAX_LENGTH_SINGLELINE) {
            return message.substring(0, MAX_LENGTH_SINGLELINE).concat("...");
        } else {
            return message.substring(0, length);
        }
    }

    public static class ButtonConfig {
        public CharSequence label;
        public int color;
        public boolean enable;
        public OnDialogViewClickListener listener;

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener, boolean enable) {
            this.listener = listener;
            this.label = label;
            this.color = color;
            this.enable = enable;
        }

        public ButtonConfig(CharSequence label, int color, OnDialogViewClickListener listener) {
            this(label, color, listener, true);
        }
    }

    public static class SimpleDismissListener implements OnDialogViewClickListener {

        @Override
        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
            if (fragment != null) {
//                fragment.dismiss();
            }
        }
    }

    class FixedDialog extends Dialog {

        private static final String TAG = "FixedDialog";

        @BindView(R.id.tv_message)
        TextView mMessageTv;

        @BindView(R.id.tv_sub_message)
        TextView mSubMessageTv;

        @BindView(R.id.tv_cancel)
        TextView mCancelView;

        @BindView(R.id.tv_confirm)
        TextView mConfirmView;

        @BindView(R.id.iv_close)
        View mCloseView;

        @BindView(R.id.root_layout)
        View mRootLayout;

        @BindView(R.id.iv_title_img)
        ImageView mTitleImage;

        @BindView(R.id.layout_message)
        LinearLayout mLayoutMessage;


        public FixedDialog(Context context) {
            this(context, R.style.CommonDialogStyle);
        }

        public FixedDialog(Context context, int theme) {
            super(context, theme);
            //ContainerFrameLayout container = new ContainerFrameLayout(context);
            //View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_frgament_common_layout, null);
            //container.addView(v);
            setContentView(R.layout.dialog_frgament_common_layout);
            ButterKnife.bind(this);
        }

        public void applyTitleDrawable(int drawableId) {
            // 不需要标题图片
            if (drawableId == INVALID) {
                mTitleImage.setVisibility(View.GONE);
                int paddingTop = 0;
                mRootLayout.setPadding(mRootLayout.getPaddingLeft(), paddingTop,
                        mRootLayout.getPaddingRight(), mRootLayout.getPaddingBottom());

                int marginTop = (int) getResources().getDimension(R.dimen.dialog_message_margin_top);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutMessage.getLayoutParams();
                params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin);
            } else {
                mTitleImage.setImageResource(drawableId);
            }
        }

    }

    private class ContainerFrameLayout extends FrameLayout {

        private boolean mAnimate;

        public ContainerFrameLayout(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            Log.debug("Ville", "onLayouts");
            View child = getChildAt(0);
            if (child != null && mAnimate) {
                mAnimate = false;
                Log.debug("Ville", "onLayout animation");
                animate()
                        .setInterpolator(new OvershootInterpolator())
                        .scaleX(1.0F)
                        .scaleY(1.0F)
                        .setDuration(200)
                        .start();
            }
        }
    }
}

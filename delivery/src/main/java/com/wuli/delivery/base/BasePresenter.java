package com.wuli.delivery.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class BasePresenter<T> {

    private Reference<T> mViewReference;

    protected void attachView(T t) {
        this.mViewReference = new WeakReference<T>(t);
    }

    /**
     * 在调用该方法之前，应该调用isViewAttached进行判断
     * @return
     */
    protected T getView() {
        return mViewReference.get();
    }

    protected Boolean isViewAttached() {
        return mViewReference != null && mViewReference.get() != null;
    }

    protected void detachView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }

}

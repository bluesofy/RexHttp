package com.byk.pandora.rexhttp.watcher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.CallSuper;

import com.byk.pandora.rexhttp.exception.ApiException;

import io.reactivex.disposables.Disposable;

/**
 * Created by Byk on 2017/12/15.
 *
 * @author Byk
 */
public class ProgressDialogWatcher<T> extends ResponseWatcher<T> {

    private IProgressDialog mDialogModel;
    private Dialog mDialog;

    private Disposable mDisposable;

    private boolean mIsShowProgress = true;

    public ProgressDialogWatcher(IProgressDialog dialogModel) {
        this.mDialogModel = dialogModel;
        init(false);
    }

    /**
     * @param dialogModel    Dialog Model
     * @param isShowProgress Show Progress
     * @param isCancel       Set Cancelable
     */
    public ProgressDialogWatcher(IProgressDialog dialogModel, boolean isShowProgress, boolean isCancel) {
        this.mDialogModel = dialogModel;
        this.mIsShowProgress = isShowProgress;
        init(isCancel);
    }

    private void init(boolean isCancel) {
        if (mDialogModel == null) {
            return;
        }

        mDialog = mDialogModel.getDialog();
        if (mDialog == null) {
            return;
        }

        mDialog.setCancelable(isCancel);
        if (isCancel) {
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelProgress();
                }
            });
        }
    }

    private void showProgress() {
        if (!mIsShowProgress) {
            return;
        }

        if (mDialog != null) {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }

    private void dismissProgress() {
        if (!mIsShowProgress) {
            return;
        }
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    @Override
    public void onStart() {
        showProgress();
    }

    @Override
    public void onCompleted() {
        dismissProgress();
    }

    @CallSuper
    @Override
    public void onError(ApiException e) {
        dismissProgress();
    }

    public void cancelProgress() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public void subscription(Disposable disposable) {
        this.mDisposable = disposable;
    }
}

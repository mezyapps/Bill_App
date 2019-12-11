package com.mezyapps.bill_app.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.mezyapps.bill_app.R;


public class ShowProgressDialog {
    private Dialog dialog;
    private Context context;

    public ShowProgressDialog(Context context) {
        this.context = context;
    }

    public ShowProgressDialog showDialog() {

        if (dialog != null) {
            dialog = null;
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show();
        return null;
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

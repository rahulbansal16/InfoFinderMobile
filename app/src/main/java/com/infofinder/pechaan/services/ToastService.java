package com.infofinder.pechaan.services;

import android.content.Context;
import android.widget.Toast;

public class ToastService {

    private Context context;

    public ToastService(Context context) {
        this.context = context;
    }

    public void showLongToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void showShortToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

package com.afollestad.silk.http;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface SilkHttpCallback {

    public void onComplete(SilkHttpResponse response);

    public void onError(SilkHttpException e);
}

package com.afollestad.silk.http;

import android.content.Context;
import android.os.Handler;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;

/**
 * A Apache HTTP Client wrapper that makes http faster and easier.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SilkHttpClient extends SilkHttpBase {

    private String mHost;

    /**
     * Initializes a new SilkHttpClient.
     */
    public SilkHttpClient(Context context) {
        super(context);
    }

    /**
     * Initializes a new SilkHttpClient.
     *
     * @param handler The handler used to post to a thread for async callbacks.
     */
    public SilkHttpClient(Context context, Handler handler) {
        super(context, handler);
    }

    private String getUrl(String url) {
        if (mHost != null) {
            if (!url.startsWith("/")) url = "/" + url;
            return mHost + url;
        }
        return url;
    }

    /**
     * Sets a host that is automatically put before every request's URL.
     */
    public SilkHttpClient setHost(String host) {
        if (host != null) {
            host = host.trim();
            if (host.isEmpty()) host = null;
            else {
                if (host.endsWith("/"))
                    host = host.substring(0, host.length() - 1);
                if (!host.startsWith("http://") && !host.startsWith("https://"))
                    host = "http://" + host;
            }
        }
        mHost = host;
        return this;
    }

    /**
     * Adds an HTTP header to the client, which will be used for the next request. Headers are cleared after a request is performed.
     */
    public SilkHttpClient addHeader(SilkHttpHeader header) {
        super.mHeaders.add(header);
        return this;
    }

    /**
     * Adds an HTTP header to the client, which will be used for the next request. Headers are cleared after a request is performed.
     */
    public SilkHttpClient addHeader(String name, String value) {
        addHeader(new SilkHttpHeader(name, value));
        return this;
    }

    /**
     * Makes a GET request on the calling thread.
     */
    public SilkHttpResponse get(String url) throws SilkHttpException {
        return performRequest(new HttpGet(getUrl(url)));
    }

    /**
     * Makes a POST request on the calling thread.
     */
    public SilkHttpResponse post(String url) throws SilkHttpException {
        return post(url, null);
    }

    /**
     * Makes a POST request on the calling thread, with a POST entity (body).
     */
    public SilkHttpResponse post(String url, SilkHttpBody body) throws SilkHttpException {
        HttpPost post = new HttpPost(getUrl(url));
        if (body != null) post.setEntity(body.getEntity());
        return performRequest(post);
    }

    /**
     * Makes a PUT request on the calling thread.
     */
    public SilkHttpResponse put(String url) throws SilkHttpException {
        return put(url, null);
    }

    /**
     * Makes a GET request on the calling thread, with a PUT entity (body).
     */
    public SilkHttpResponse put(String url, SilkHttpBody body) throws SilkHttpException {
        HttpPut post = new HttpPut(getUrl(url));
        if (body != null) post.setEntity(body.getEntity());
        return performRequest(post);
    }

    /**
     * Makes a DELETE request on the calling thread.
     */
    public SilkHttpResponse delete(String url) throws SilkHttpException {
        return performRequest(new HttpDelete(getUrl(url)));
    }

    // Async methods

    /**
     * Makes a GET request from a separate thread and posts the results to a callback.
     */
    public void getAsync(final String url, final SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        runOnPriorityThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SilkHttpResponse response = get(url);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(response);
                        }
                    });
                } catch (final SilkHttpException e) {
                    e.printStackTrace();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Makes a POST request from a separate thread and posts the results to a callback.
     */
    public void postAsync(String url, SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        postAsync(url, null, callback);
    }

    /**
     * Makes a POST request from a separate thread (with a POST entity, or body) and posts the results to a callback.
     */
    public void postAsync(final String url, final SilkHttpBody body, final SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        runOnPriorityThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SilkHttpResponse response = post(url, body);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(response);
                        }
                    });
                } catch (final SilkHttpException e) {
                    e.printStackTrace();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Makes a PUT request from a separate thread and posts the results to a callback.
     */
    public void putAsync(String url, SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        putAsync(url, null, callback);
    }

    /**
     * Makes a PUT request from a separate thread (with a PUT entity, or body) and posts the results to a callback.
     */
    public void putAsync(final String url, final SilkHttpBody body, final SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        runOnPriorityThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SilkHttpResponse response = put(url, body);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(response);
                        }
                    });
                } catch (final SilkHttpException e) {
                    e.printStackTrace();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Makes a DELETE request from a separate thread and posts the results to a callback.
     */
    public void deleteAsync(final String url, final SilkHttpCallback callback) {
        if (callback == null) throw new IllegalArgumentException("The callback cannot be null.");
        runOnPriorityThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SilkHttpResponse response = delete(url);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(response);
                        }
                    });
                } catch (final SilkHttpException e) {
                    e.printStackTrace();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }
}

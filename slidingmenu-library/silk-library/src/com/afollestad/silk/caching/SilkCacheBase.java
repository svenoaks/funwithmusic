package com.afollestad.silk.caching;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class SilkCacheBase<Item extends SilkComparable<Item>> extends SilkCacheBaseLimiter<Item> {

    public SilkCacheBase(Context context, String name, Class<?> cls) {
        this(context, name, cls, null);
    }

    public SilkCacheBase(Context context, String name, Class<?> cls, Handler handler) {
        super(context, name);
        mCls = cls;
        if (handler == null) mHandler = new Handler();
        else mHandler = handler;
    }

    private final static File CACHE_DIR = new File(Environment.getExternalStorageDirectory(), ".silk_cache");
    private final Handler mHandler;
    private List<Item> mBuffer;
    private boolean isChanged;
    private final Class<?> mCls;

    private Kryo getKryo() {
        Kryo mKryo = new Kryo();
        mKryo.register(mCls);
        return mKryo;
    }

    public final boolean isChanged() {
        return isChanged;
    }

    protected List<Item> getBuffer() {
        return mBuffer;
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected File getCacheFile() {
        return new File(CACHE_DIR, getName() + ".cache");
    }

    protected void markChanged() {
        isChanged = true;
    }

    protected void loadItems() {
        File cacheFile = getCacheFile();
        if (hasExpiration()) {
            long expiration = getExpiration();
            long now = Calendar.getInstance().getTimeInMillis();
            if (now >= expiration) {
                // Cache is expired
                cacheFile.delete();
                log("Cache has expired, re-creating...");
                setExpiration(-1);
            }
        }
        mBuffer = new ArrayList<Item>();
        if (cacheFile.exists()) {
            Input input = null;
            try {
                Kryo kryo = getKryo();
                input = new Input(new FileInputStream(cacheFile));
                while (true) {
                    final Object item = kryo.readObjectOrNull(input, mCls);
                    if (item != null) mBuffer.add((Item) item);
                    else break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("Error loading items -- " + e.getMessage());
            } finally {
                if (input != null) input.close();
                log("Read " + mBuffer.size() + " items from the cache file");
            }
        } else log("Cache file doesn't exist (" + cacheFile.getAbsolutePath() + ").");
    }

    public final long getExpiration() {
        SharedPreferences prefs = getContext().getSharedPreferences("[silk-cache-expiration]", Context.MODE_PRIVATE);
        return prefs.getLong(getName(), -1);
    }

    public final boolean hasExpiration() {
        return getExpiration() > -1;
    }

    public final void setExpiration(long dateTime) {
        SharedPreferences.Editor prefs = getContext().getSharedPreferences("[silk-cache-expiration]", Context.MODE_PRIVATE).edit();
        if (dateTime < 0)
            prefs.remove(getName());
        else prefs.putLong(getName(), dateTime);
        prefs.commit();
    }

    public final void setExpiration(int weeks, int days, int hours, int minutes) {
        long now = Calendar.getInstance().getTimeInMillis();
        now += (1000 * 60) * minutes; // 60 seconds in a minute
        now += (1000 * 60 * 60) * hours; // 60 minutes in an hour
        now += (1000 * 60 * 60 * 24) * days; // 24 hours in a day
        now += (1000 * 60 * 60 * 24 * 7) * weeks; // 7 days in a week
        setExpiration(now);
    }

    public final void setLimiter(SilkCacheLimiter limiter) {
        if (limiter == null) {
            getLimiterPrefs().edit().remove(getName()).commit();
        } else {
            getLimiterPrefs().edit().putString(getName(), limiter.toString()).commit();
            // Perform limiting if necessary
            if (atLimit(mBuffer)) {
                mBuffer = performLimit(mBuffer);
            }
        }
    }

    public final void commit() throws Exception {
        final File cacheFile = getCacheFile();
        if (!isChanged) {
            throw new IllegalStateException("The cache has not been modified since initialization or the last commit.");
        } else if (mBuffer.size() == 0) {
            if (cacheFile.exists()) {
                log("Deleting: " + cacheFile.getName());
                cacheFile.delete();
            }
            return;
        }

        // Perform limiting if necessary
        if (atLimit(mBuffer)) {
            mBuffer = performLimit(mBuffer);
        }

        CACHE_DIR.mkdirs();
        Kryo kryo = getKryo();
        Output output = new Output(new FileOutputStream(cacheFile));
        for (Item item : mBuffer)
            kryo.writeObject(output, item);
        output.close();
        log("Committed " + mBuffer.size() + " items.");
        isChanged = false;
    }

    public final void commit(final SilkCache.SimpleCommitCallback callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    commit();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null && callback instanceof SilkCache.CommitCallback)
                                ((SilkCache.CommitCallback) callback).onCommitted();
                        }
                    });
                } catch (final Exception e) {
                    log("Commit error: " + e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) callback.onError(e);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
}
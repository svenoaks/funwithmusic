package com.afollestad.silk.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import com.afollestad.silk.Silk;
import com.afollestad.silk.http.SilkHttpClient;
import com.afollestad.silk.http.SilkHttpResponse;
import com.afollestad.silk.utilities.DiskCache;

import java.io.*;
import java.net.URLEncoder;
import java.util.concurrent.*;

/**
 * <p>The most important class in the AImage library; downloads images, and handles caching them on the disk and in memory
 * so they can quickly be retrieved. Also allows you to download images to fit a certain width and height.</p>
 * <p/>
 * <p>If you're using AImage for displaying images in your UI, see {@link com.afollestad.silk.views.image.SilkImageView} and
 * the other variations of it for easy-to-use options.</p>
 */
public class SilkImageManager {

    public static final String SOURCE_FALLBACK = "aimage://fallback_image";
    protected static final int MEM_CACHE_SIZE_KB = (int) (Runtime.getRuntime().maxMemory() / 2 / 1024);
    protected static final int ASYNC_THREAD_COUNT = (Runtime.getRuntime().availableProcessors() * 4);
    private final Context context;
    private final DiskCache mDiskCache;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService mNetworkExecutorService = newConfiguredThreadPool();
    private final ExecutorService mDiskExecutorService = Executors.newCachedThreadPool(new LowPriorityThreadFactory());
    private int fallbackImageId;
    private boolean DEBUG = false;
    private LruCache<String, Bitmap> mLruCache = newConfiguredLruCache();

    public SilkImageManager(Context context) {
        this.context = context;
        mLruCache = new LruCache<String, Bitmap>(MEM_CACHE_SIZE_KB * 1024) {
            @Override
            public int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mDiskCache = new DiskCache(context);
    }

    private static ExecutorService newConfiguredThreadPool() {
        int corePoolSize = 0;
        int maximumPoolSize = ASYNC_THREAD_COUNT;
        long keepAliveTime = 60L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    private static LruCache<String, Bitmap> newConfiguredLruCache() {
        return new LruCache<String, Bitmap>(MEM_CACHE_SIZE_KB * 1024) {
            @Override
            public int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    protected void log(String message) {
        if (!DEBUG)
            return;
        Log.i("SilkImageManager", message);
    }

    public boolean isDebugEnabled() {
        return DEBUG;
    }

    public SilkImageManager setDebugEnabled(boolean enabled) {
        this.DEBUG = enabled;
        return this;
    }

    /**
     * Sets the directory that will be used to cache images.
     */
    public SilkImageManager setCacheDirectory(File cacheDir) {
        mDiskCache.setCacheDirectory(cacheDir);
        return this;
    }

    /**
     * Sets the resource ID of fallback image that is used when an image can't be loaded, or when you call
     * {@link com.afollestad.silk.views.image.SilkImageView#showFallback(SilkImageManager)} from the SilkImageView.
     */
    public SilkImageManager setFallbackImage(int resourceId) {
        this.fallbackImageId = resourceId;
        return this;
    }

    /**
     * Gets an image from a URI on the thread (Android doesn't allow this on the main UI thread) and returns the result.
     *
     * @param source The URI to get the image from.
     */
    public Bitmap get(String source, Dimension dimension) {
        if (source == null) {
            return null;
        }
        String key = Utils.getKey(source, dimension);
        Bitmap bitmap = mLruCache.get(key);
        if (bitmap == null) {
            bitmap = getBitmapFromDisk(key);
        } else {
            log("Got " + source + " from the memory cache.");
        }
        if (bitmap == null) {
            bitmap = getBitmapFromExternal(key, source, dimension, null);
            log("Got " + source + " from the external source.");
        } else {
            log("Got " + source + " from the disk cache.");
        }
        return bitmap;
    }

    /**
     * Gets an image from a URI on a separate thread and posts the results to a callback.
     *
     * @param source   The URI to get the image from.
     * @param callback The callback that the result will be posted to.
     * @param cache    Whether or not to load from the cache and save to the cache.
     */
    public void get(final String source, final ImageListener callback, final Dimension dimension, boolean cache) {
        if (cache) {
            get(source, callback, dimension);
            return;
        }
        // Caching disabled, load from external immediately
        final String key = Utils.getKey(source, dimension);
        mNetworkExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapFromExternal(key, source, dimension, new ProcessCallback() {
                    @Override
                    public Bitmap onProcess(Bitmap image) {
                        if (callback != null && callback instanceof AdvancedImageListener)
                            image = ((AdvancedImageListener) callback).onPostProcess(image);
                        return image;
                    }
                });
                log("Got " + source + " from external source.");
                postCallback(callback, source, bitmap);
            }
        });
    }

    /**
     * Gets an image from a URI on a separate thread and posts the results to a callback.
     *
     * @param source   The URI to get the image from.
     * @param callback The callback that the result will be posted to.
     */
    public void get(final String source, final ImageListener callback, final Dimension dimension) {
        if (!Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new RuntimeException("This must only be executed on the main UI Thread!");
        } else if (source == null) {
            return;
        }

        final String key = Utils.getKey(source, dimension);
        Bitmap bitmap = mLruCache.get(key);
        if (bitmap != null) {
            log("Got " + source + " from the memory cache.");
            postCallback(callback, source, bitmap);
            return;
        }

        mDiskExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapFromDisk(key);
                if (bitmap != null) {
                    log("Got " + source + " from the disk cache.");
                    postCallback(callback, source, bitmap);
                    return;
                }

                if (!Silk.isOnline(context) && source.startsWith("http")) {
                    log("Device is offline, image is not cached; getting fallback image...");
                    Bitmap fallback = get(SilkImageManager.SOURCE_FALLBACK, dimension);
                    if (callback != null && callback instanceof AdvancedImageListener)
                        fallback = ((AdvancedImageListener) callback).onPostProcess(bitmap);
                    postCallback(callback, source, fallback);
                    return;
                }

                mNetworkExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getBitmapFromExternal(key, source, dimension, new ProcessCallback() {
                            @Override
                            public Bitmap onProcess(Bitmap image) {
                                if (callback != null && callback instanceof AdvancedImageListener)
                                    image = ((AdvancedImageListener) callback).onPostProcess(image);
                                return image;
                            }
                        });
                        log("Got " + source + " from external source.");
                        postCallback(callback, source, bitmap);
                    }
                });
            }
        });
    }

    public File getCacheFile(String originalSource, Dimension dimen) {
        return mDiskCache.getFile(Utils.getKey(originalSource, dimen));
    }

    private void postCallback(final ImageListener callback, final String source, final Bitmap bitmap) {
        mHandler.post(new Runnable() {
            public void run() {
                if (callback != null)
                    callback.onImageReceived(source, bitmap);
            }
        });
    }

    private Bitmap getBitmapFromDisk(String key) {
        Bitmap bitmap = null;
        try {
            bitmap = mDiskCache.get(key);
            if (bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getBitmapFromExternal(String key, String source, Dimension dimension, ProcessCallback callback) {
        byte[] byteArray = sourceToBytes(source);
        if (byteArray == null) {
            source = SilkImageManager.SOURCE_FALLBACK;
            byteArray = sourceToBytes(source);
        }

        Bitmap bitmap = Utils.decodeByteArray(byteArray, dimension);
        if (source.equals(SilkImageManager.SOURCE_FALLBACK)) {
            if (callback != null && bitmap != null)
                bitmap = callback.onProcess(bitmap);
            return bitmap;
        }

        if (bitmap != null) {
            if (callback != null)
                bitmap = callback.onProcess(bitmap);
            if (!source.startsWith("content") && !source.startsWith("file")) {
                // If the source is already from the local disk, don't cache it locally again.
                try {
                    mDiskCache.put(key, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mLruCache.put(key, bitmap);
            return bitmap;
        }
        return null;
    }

    private byte[] inputStreamToBytes(InputStream stream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(stream, byteArrayOutputStream);
        } catch (IOException e) {
            IOUtils.closeQuietly(byteArrayOutputStream);
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] sourceToBytes(String source) {
        InputStream inputStream = null;
        byte[] toreturn = null;
        try {
            if (source.equals(SilkImageManager.SOURCE_FALLBACK)) {
                log("Loading fallback image...");
                if (fallbackImageId > 0)
                    inputStream = context.getResources().openRawResource(fallbackImageId);
                else return null;
            } else if (source.startsWith("content")) {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(source));
            } else if (source.startsWith("file")) {
                Uri uri = Uri.parse(source);
                inputStream = new FileInputStream(new File(uri.getPath()));
            } else {
                SilkHttpClient client = new SilkHttpClient(context);
                SilkHttpResponse response = client.get(source);
                inputStream = response.getContent().getContent();
            }
            toreturn = inputStreamToBytes(inputStream);
        } catch (Exception e) {
            log("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return toreturn;
    }

    private interface ProcessCallback {
        public Bitmap onProcess(Bitmap image);
    }

    public interface ImageListener {
        public abstract void onImageReceived(String source, Bitmap bitmap);
    }

    public interface AdvancedImageListener extends ImageListener {
        public abstract Bitmap onPostProcess(Bitmap image);
    }

    public static class Utils {

        public static int calculateInSampleSize(BitmapFactory.Options options, Dimension dimension) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > dimension.getHeight() || width > dimension.getWidth()) {

                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / (float) dimension.getHeight());
                final int widthRatio = Math.round((float) width / (float) dimension.getWidth());

                // Choose the smallest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return inSampleSize;
        }

        public static BitmapFactory.Options getBitmapFactoryOptions(Dimension dimension) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            if (dimension != null)
                options.inSampleSize = calculateInSampleSize(options, dimension);
            return options;
        }

        public static Bitmap decodeByteArray(byte[] byteArray, Dimension dimension) {
            try {
                BitmapFactory.Options bitmapFactoryOptions = getBitmapFactoryOptions(dimension);
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bitmapFactoryOptions);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        public static String getKey(String source, Dimension dimension) {
            if (source == null) return null;
            source = source.replace("http://", "").replace("https://", "");
            String ext = null;
            if (source.endsWith(".jpg") || source.endsWith(".jpeg"))
                ext = ".jpeg";
            else if (source.endsWith(".png"))
                ext = ".png";
            if (dimension != null)
                source += "_" + dimension.toString();
            try {
                return URLEncoder.encode(source, "UTF-8") + ext;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class IOUtils {

        public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

        public static void closeQuietly(InputStream input) {
            closeQuietly((Closeable) input);
        }

        public static void closeQuietly(OutputStream output) {
            closeQuietly((Closeable) output);
        }

        public static void closeQuietly(Closeable closeable) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }

        public static int copy(InputStream input, OutputStream output) throws IOException {
            long count = copyLarge(input, output);
            if (count > Integer.MAX_VALUE) {
                return -1;
            }
            return (int) count;
        }

        private static long copyLarge(InputStream input, OutputStream output) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }
    }
}
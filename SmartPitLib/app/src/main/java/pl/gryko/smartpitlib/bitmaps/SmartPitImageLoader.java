package pl.gryko.smartpitlib.bitmaps;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pl.gryko.smartpitlib.widget.Log;

/**
 * Created by piotr on 19.05.14.
 */
public class SmartPitImageLoader {


    private String TAG = SmartPitImageLoader.class.getName();

    private final RequestQueue mRequestQueue;

    private int mBatchResponseDelayMs = 100;

    private final SmartPitBitmapCache mCache;

    private final HashMap<String, BatchedImageRequest> mInFlightRequests =
            new HashMap<String, BatchedImageRequest>();

    private final HashMap<String, CacheTask> mInFlightCacheRequests =
            new HashMap<String, CacheTask>();

    private final HashMap<String, BatchedImageRequest> mBatchedResponses =
            new HashMap<String, BatchedImageRequest>();

    private final HashMap<String, CacheTask> mBatchedCacheResponses =
            new HashMap<String, CacheTask>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mHttpRunnable;
    private Runnable mCacheRunnable;

    public SmartPitImageLoader(RequestQueue queue, SmartPitBitmapCache imageCache) {

        mRequestQueue = queue;
        mCache = imageCache;
    }

    public class SmartImageContainer {

        private Bitmap mBitmap;
        private final SmartImagesListener mListener;

        private final String mCacheKey;

        private final String mRequestUrl;


        public SmartImageContainer(Bitmap bitmap, String requestUrl,
                                   String cacheKey, SmartImagesListener listener) {

            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mListener = listener;
        }


        public void cancelRequest() {
            if (mListener == null) {
                return;
            }
            BatchedImageRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                // check to see if it is already batched for delivery.
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }


        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getRequestUrl() {
            return mRequestUrl;
        }
    }


    private class CacheTask extends AsyncTask {

        private ArrayList<SmartImageContainer> containers;
        private String key;
        private Bitmap mResponseBitmap;
        private SmartImageContainer container;

        public CacheTask(String key, SmartImageContainer container) {
            this.key = key;

            containers = new ArrayList<SmartImageContainer>();
            containers.add(container);
        }


        @Override
        protected Object doInBackground(Object[] params) {


            Bitmap b = mCache.getBitmap(key);
            mResponseBitmap = b;

            return null;
        }

        protected void onPostExecute(Object result) {
            batchCacheResponse(key, CacheTask.this);

        }
    }


    public interface SmartImagesListener {
        public void onResponse(SmartImageContainer container, boolean flag);

        public void onErrorResponse(VolleyError error);
    }

    public SmartImageContainer get(String requestUrl, SmartImagesListener imageListener,
                                   int maxWidth, int maxHeight) {


        Log.d(TAG, "get method for " + requestUrl);
        throwIfNotOnMainThread();
        // final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
        final String cacheKey = requestUrl;
        // Try to look up the request in the cache of remote images.

        Bitmap tmp = mCache.isLocalCached(cacheKey);
        if (tmp != null) {
            Log.d(TAG,"bitmap is cached in local Memory");
            SmartImageContainer imageContainer =
                    new SmartImageContainer(tmp, requestUrl, cacheKey, imageListener);
            imageContainer.mListener.onResponse(imageContainer, true);
            return imageContainer;

        } else

        if (mCache.isDiskCached(cacheKey)) {

            CacheTask request = null;
            SmartImageContainer container = new SmartImageContainer(null, requestUrl, cacheKey, imageListener);


            request = mInFlightCacheRequests.get(cacheKey);
            if (request != null) {
                Log.d(SmartPitImageLoader.class.getName(), "cache reguest inflight!");
                request.containers.add(container);
                return container;

            }


            request = new CacheTask(cacheKey, container);
            mInFlightCacheRequests.put(cacheKey, request);
            request.execute();


            return container;

        }

        Log.d(TAG, "bitmap not cached!");

        // The bitmap did not exist in the cache, fetch it!
        SmartImageContainer imageContainer =
                new SmartImageContainer(null, requestUrl, cacheKey, imageListener);
        // Update the caller to let them know that they should use the default bitmap.
        //imageListener.onResponse(imageContainer, true);
        // Check to see if a request is already in-flight.
        BatchedImageRequest request = mInFlightRequests.get(cacheKey);
        if (request != null) {
            // If it is, add this request to the list of listeners.
            request.addContainer(imageContainer);
            return imageContainer;
        }
        // The request is not already in flight. Send the new request to the network and
        // track it.
        Request<?> newRequest =
                new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        onGetImageSuccess(cacheKey, response);
                    }
                }, maxWidth, maxHeight,
                        Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onGetImageError(cacheKey, error);
                    }
                }
                );
        mRequestQueue.add(newRequest);
        mInFlightRequests.put(cacheKey,
                new BatchedImageRequest(newRequest, imageContainer));
        return imageContainer;


    }

    private void onGetImageSuccess(String cacheKey, Bitmap response) {
        // cache the image that was fetched.
        mCache.putBitmap(cacheKey, response);
        // remove the request from the list of in-flight requests.
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);
        if (request != null) {
            // Update the response bitmap.
            request.mResponseBitmap = response;
            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }


    private void onGetImageError(String cacheKey, VolleyError error) {
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);
        if (request != null) {
            // Set the error for this request
            request.setError(error);
            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    private class BatchedImageRequest {

        private final Request<?> mRequest;

        private Bitmap mResponseBitmap;

        private VolleyError mError;

        private final LinkedList<SmartImageContainer> mContainers = new LinkedList<SmartImageContainer>();

        public BatchedImageRequest(Request<?> request, SmartImageContainer container) {
            mRequest = request;
            mContainers.add(container);
        }

        public void setError(VolleyError error) {
            mError = error;
        }

        public VolleyError getError() {
            return mError;
        }

        public void addContainer(SmartImageContainer container) {
            mContainers.add(container);
        }

        public boolean removeContainerAndCancelIfNecessary(SmartImageContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }


    private void batchCacheResponse(String cacheKey, final CacheTask task) {

        mBatchedCacheResponses.put(cacheKey, task);

        if (mCacheRunnable == null) {
            mCacheRunnable = new Runnable() {
                @Override
                public void run() {
                    for (CacheTask task : mBatchedCacheResponses.values()) {
                        for (SmartImageContainer container : task.containers) {

                            if (container.mListener == null) {
                                continue;
                            }
                            //if (bir.getError() == null) {
                            container.mBitmap = task.mResponseBitmap;
                            container.mListener.onResponse(container, false);

                            //   } else {
                            //      task.container.mListener.onErrorResponse(bir.getError());
                            // }

                        }
                        mInFlightCacheRequests.remove(task.key);

                    }

                    mBatchedCacheResponses.clear();
                    mCacheRunnable = null;
                }
            };
            // Post the runnable.
            mHandler.postDelayed(mCacheRunnable, mBatchResponseDelayMs);
        }

    }

    private void batchResponse(String cacheKey, BatchedImageRequest request) {
        mBatchedResponses.put(cacheKey, request);

        if (mHttpRunnable == null) {
            mHttpRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedImageRequest bir : mBatchedResponses.values()) {
                        for (SmartImageContainer container : bir.mContainers) {
                            // If one of the callers in the batched request canceled the request
                            // after the response was received but before it was delivered,
                            // skip them.
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.getError() == null) {
                                container.mBitmap = bir.mResponseBitmap;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onErrorResponse(bir.getError());
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mHttpRunnable = null;
                }
            };
            // Post the runnable.
            mHandler.postDelayed(mHttpRunnable, mBatchResponseDelayMs);
        }
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

/*
    private static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append(url).toString();
    }
*/
}

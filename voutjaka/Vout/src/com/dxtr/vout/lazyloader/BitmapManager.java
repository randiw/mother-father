package com.dxtr.vout.lazyloader;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxtr.vout.R;
import com.dxtr.vout.utils.ImageUtils;

public enum BitmapManager {
	INSTANCE;

	private final Map<String, SoftReference<Bitmap>> cache;
	private final ExecutorService pool;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	
	BitmapManager() {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5);
	}

	public void clearAllCache(){
		cache.clear();
	}
	
	public void clearCache(String urlKey){
		cache.remove(urlKey);
	}
	
	public Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url).get();
		}

		return null;
	}

	@SuppressLint("HandlerLeak")
	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height, final Bitmap defaultThumb, final TextView bindedTitle) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						if (bindedTitle != null)bindedTitle.setVisibility(View.GONE);
						setBitmapAnimated(imageView, (Bitmap) msg.obj);
					} else {
						if (bindedTitle != null)bindedTitle.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(defaultThumb);
						Log.d(null, "Image Fail " + url);
					}
				}
			}
		};

		pool.submit(new Runnable() {
			public void run() {
				final Bitmap bmp = downloadBitmap(url, width, height);
				Message message = Message.obtain();
				message.obj = bmp;
				handler.sendMessage(message);
			}
		});
	}
	
	private void setBitmapAnimated(final ImageView view, final Bitmap bitmap){
		Animation myFadeOutAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
		view.startAnimation(myFadeOutAnimation);
		new Handler().postDelayed(new Runnable() {			
			public void run() {
				view.setImageBitmap(bitmap);
				Animation myFadeInAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
				view.startAnimation(myFadeInAnimation);
			}
		}, 300);
	}

	public void loadBitmap(final String url, final ImageView imageView,
			final int width, final int height, Bitmap defaultThumb, TextView bindedTitle) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			if (bindedTitle != null)bindedTitle.setVisibility(View.GONE);
			imageView.setImageBitmap(ImageUtils.resizeImage(bitmap, width, height));
//			setBitmapAnimated(imageView, ImageUtils.resizeImage(bitmap, width, height));
		} else {
			if (bindedTitle != null)bindedTitle.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(defaultThumb);
			queueJob(url, imageView, width, height, defaultThumb, bindedTitle);
		}
	}

	private InputStream RetrieveStream(String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		} catch (IOException e) {
			getRequest.abort();
		}

		return null;
	}

	private Bitmap FetchBitmapFromURL(String url) {
		try {
			InputStream is = RetrieveStream(url);
			if (is == null)
				return null;
			Bitmap bitmap = BitmapFactory
					.decodeStream(new FlushInputStream(is));
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	private static class FlushInputStream extends FilterInputStream {

		public FlushInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}

			return totalBytesSkipped;
		}
	}

	private Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = FetchBitmapFromURL(url);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		cache.put(url, new SoftReference<Bitmap>(bitmap));
		return bitmap;
	}
}

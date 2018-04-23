/*
    Create by JunChangWook 2018.04.11
 */

package ka.com.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadImageThread extends Thread {
    final Handler handler = new Handler();
    private WeakReference<ImageView> imageViewReference;
    private Bitmap mBitmap;
    ImageAdapter.ViewHolder mHolder;
    private String mUrl = null;
    private String mPosition;
    private ImageView mImage;
    private Context mContext;


    public DownloadImageThread(Context mContext, ImageView imageView, String url, ImageAdapter.ViewHolder holder, String position) {
        imageViewReference = new WeakReference<>(imageView);
        this.mContext = mContext;
        this.mUrl = url;
        this.mHolder = holder;
        this.mPosition = position;
        this.mImage = imageView;
    }

    public void run() {
        try {
            URL url = new URL(this.mUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            mBitmap = BitmapFactory.decodeStream(is);


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ((MainActivity) this.mContext).addImageCache(mPosition, mBitmap);

        int numInt = Integer.parseInt(mPosition);

        if(((MainActivity) this.mContext).mFirstVisibleItem <= numInt && ((MainActivity) this.mContext).mVisibleItemCount >= numInt) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (imageViewReference != null && mBitmap != null) {
                        final ImageView imageView = imageViewReference.get();
                        if (imageView != null) {
                            imageView.setImageBitmap(mBitmap);
                            imageViewReference = new WeakReference<>(imageView);
                        }
                    }
                }
            });
        }
    }
}
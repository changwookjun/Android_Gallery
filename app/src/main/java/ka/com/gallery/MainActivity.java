/*
    Create by JunChangWook 2018.04.11
 */
package ka.com.gallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "http://www.gettyimagesgallery.com";
    private static final String SUBURL = "/collections/archive/slim-aarons.aspx";

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageAdapter mImageAdapter;
    private ArrayList<ImageItem> mImageData;
    private LruCache<String, Bitmap> mImageCache;

    public static int mFirstVisibleItem = 0;
    public static int mVisibleItemCount = 0;

    /*  totalMemory() 메소드는 자바 가상 머신(JVM)의 모든 메모리 양을 바이트 단위로 반환
        freeMemory() 는 자바 가상머신 내의 남은 메모리의 양을 바이트 단위로 반환
        maxMemory() 는, 가상머신이 사용하려고 시도했던 가장 큰 메모리 양  */

    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory());
    final static int cacheSize = maxMemory / 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncHttpTask().execute();
        mImageCache = new LruCache<String, Bitmap>(cacheSize / 1024) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()  / 1024 ;
            }
        };

        setContentView(R.layout.main_activity);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mImageData = new ArrayList<>();
        mImageAdapter = new ImageAdapter(this, R.layout.grid_item_layout, mImageData);
        mGridView.setAdapter(mImageAdapter);
        mProgressBar.setVisibility(View.VISIBLE);

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        //mImageAdapter.notifyDataSetChanged();
                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                    case OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mFirstVisibleItem = firstVisibleItem;
                mVisibleItemCount = mFirstVisibleItem+ visibleItemCount;
            }
        });

    }

    public void addImageCache(String key, Bitmap bitmap) {
        if (getImageCache(key) == null)  {
            mImageCache.put(key, bitmap);
        }
    }

    public Bitmap getImageCache(String key) {
        return mImageCache.get(key);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            StringBuilder sb = new StringBuilder(URL);
            sb.append(SUBURL);
            String imageUrl;
            ImageItem item;
            Integer result = 1;
            try {
                Document doc = Jsoup.connect(sb.toString()).get();
                Elements links = doc.getElementsByClass("gallery-item-group");
                for (Element link : links) {
                    sb.delete(0, sb.length());
                    sb.append(URL);
                    imageUrl = link.getElementsByTag("img").get(0).attr("src");
                    if(imageUrl != null) {
                        String[] arr = imageUrl.split("/");
                        imageUrl = arr[arr.length -1];
                        imageUrl = imageUrl.replace(".jpg", "");
                    }
                    sb.append(link.getElementsByTag("img").get(0).attr("src"));

                    if( (imageUrl != null && imageUrl.length() > 0) && (sb != null && sb.length() > 0)) {
                        item = new ImageItem();
                        item.setTitle(imageUrl);
                        item.setImage(sb.toString());
                        mImageData.add(item);
                    }
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            mImageAdapter.setGridData(mImageData);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}

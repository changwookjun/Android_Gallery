/*
    Create by JunChangWook 2018.04.11
 */
package ka.com.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<ImageItem> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<ImageItem> mImageData = new ArrayList<ImageItem>();

    public ImageAdapter(Context mContext, int layoutResourceId, ArrayList<ImageItem> mImageData) {
        super(mContext, layoutResourceId, mImageData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mImageData = mImageData;
    }

    public void setGridData(ArrayList<ImageItem> mImageItem) {
        this.mImageData = mImageItem;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;
        final String imageKey = String.valueOf(position);
        final Bitmap bitmap = ((MainActivity)this.mContext).getImageCache(imageKey);

        if (row == null) {
            LayoutInflater inflater = ((Activity) this.mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem item = mImageData.get(position);
        holder.titleTextView.setText(item.getTitle());

        if(bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setImageResource(R.drawable.loading);
            DownloadImageThread downloadImage = new DownloadImageThread(this.mContext, holder.imageView, item.getImage(), holder, imageKey);
            downloadImage.start();
        }
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}

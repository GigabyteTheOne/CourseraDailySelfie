package it.simakov.dailyselfie.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.simakov.dailyselfie.R;

/**
 * Created by ks_simakov on 29/11/14.
 */
public class PhotosListAdapter extends BaseAdapter {

    private ArrayList<File> list = new ArrayList<File>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public PhotosListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }


    public void updateItems() {
        list.clear();

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (root.isDirectory()) {
            String[] filesAndDirectories = root.list();
            for( String fileOrDirectory : filesAndDirectories) {
                File f = new File(root.getAbsolutePath() + "/" + fileOrDirectory);
                if (f.isFile()) {
                    list.add(f);
                }
            }
        }

        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = convertView;
        File file = list.get(position);

        ImageView previewImageView;
        TextView photoNameTextView;

        if (null == convertView) {
            newView = inflater.inflate(R.layout.photo_item_list_view, null);
        }
        previewImageView = (ImageView) newView.findViewById(R.id.preview);
        photoNameTextView = (TextView) newView.findViewById(R.id.photo_name);

        Bitmap photoImage = BitmapFactory.decodeFile(file.getAbsolutePath());
        previewImageView.setImageBitmap(photoImage);

        Date date = new Date(file.lastModified());
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        photoNameTextView.setText(timeStamp);

        return newView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public File getItem(int position) {
        return list.get(position);
    }




}

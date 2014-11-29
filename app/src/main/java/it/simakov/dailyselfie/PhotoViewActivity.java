package it.simakov.dailyselfie;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import it.simakov.dailyselfie.fragments.PhotoViewFragment;


public class PhotoViewActivity extends Activity {

    public static String EXTRA_PHOTO_PATH = "EXTRA_PHOTO_PATH";

    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        if (intent != null) {
            mPhotoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);
        }

        if (mPhotoPath.length() > 0) {
            PhotoViewFragment photoViewFragment = PhotoViewFragment.newInstance(mPhotoPath);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, photoViewFragment)
                    .commit();
        }
        else {
            finish();
        }
    }

}

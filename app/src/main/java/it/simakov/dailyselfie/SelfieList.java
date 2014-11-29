package it.simakov.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.simakov.dailyselfie.fragments.PhotosListFragment;
import it.simakov.dailyselfie.fragments.PhotosListAdapter;


public class SelfieList extends Activity implements PhotosListFragment.OnFragmentInteractionListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private PhotosListFragment mPhotosListFragment;

    private static final long ALARM_INTERVAL = 2 * 60 * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_list);
        if (savedInstanceState == null) {
            mPhotosListFragment = new PhotosListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPhotosListFragment)
                    .commit();
        }

        createAlertNotifications();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((PhotosListAdapter) mPhotosListFragment.getListAdapter()).updateItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selfie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Toast.makeText(this, "This device has no camera", Toast.LENGTH_LONG);
                return true;
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Can't open camera", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (storePhoto(imageBitmap)) {
                ((BaseAdapter) mPhotosListFragment.getListAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error while saving photo", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Data Methods

    private boolean storePhoto(Bitmap photo) {
        boolean fileSaved = false;
        File photoFile = null;
        FileOutputStream outputStream = null;
        try {
            photoFile = createImageFile();

            if (photoFile != null) {
                outputStream = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
            fileSaved = false;
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    fileSaved = true;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
                fileSaved = false;
            }
        }

        return fileSaved;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    private void createAlertNotifications() {

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(this, AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, mNotificationReceiverIntent, 0);

        // Set repeating alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + ALARM_INTERVAL,
                ALARM_INTERVAL,
                mNotificationReceiverPendingIntent);
    }


    // List Fragment Interface methods

    @Override
    public void onFragmentInteraction(File file) {
        Intent intent = new Intent(this, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_PATH, file.getPath());
        startActivity(intent);
    }


}

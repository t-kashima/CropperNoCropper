package com.fenctose.imagecropper;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.fenchtose.nocropper.CropperView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 21;
    private static final String TAG = "MainActivity";

    @Bind(R.id.imageview)
    CropperView mImageView;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
        } else {
            Log.i(TAG, "Set landscape mode");
            setContentView(R.layout.activity_main_landscape);
        }
        ButterKnife.bind(this);
//        mImageView.setDebug(true);
    }

    @OnClick(R.id.image_button)
    public void onImageButtonClicked() {
        startGalleryIntent();
    }

    @OnClick(R.id.crop_button)
    public void onImageCropClicked() {
        cropImage();
    }

    @OnClick(R.id.rotate_button)
    public void onImageRotateClicked() {
        rotateImage();
    }

    @OnClick(R.id.snap_button)
    public void onImageSnapClicked() {
        snapImage();
    }

    private void loadNewImage(String filePath) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo_image);
        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int size = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        final float maxScaleFactor = (float)size / 3000;

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int minDimen = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
                float minScaleFactor = (float)minDimen / (float)mImageView.getWidth();
                mImageView.setMinZoom(1.0f / minScaleFactor);
                mImageView.setMaxZoom(1.0f / maxScaleFactor);
            }
        });

        mBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight(), true);
        mImageView.setImageBitmap(mBitmap);
        mImageView.setDebug(true);
    }

    private void startGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if (responseCode == RESULT_OK) {
            String absPath = BitmapUtils.getFilePathFromUri(this, resultIntent.getData());
            loadNewImage(absPath);
        }
    }

    private void cropImage() {

        Bitmap bitmap = mImageView.getCroppedBitmap(1024);

        if (bitmap != null) {

            try {
                String outputPath = Environment.getExternalStorageDirectory() + "/crop_test.jpg";
                Log.d(MainActivity.class.getSimpleName(), "output: " + outputPath);
                BitmapUtils.writeBitmapToFile(bitmap, new File(outputPath), 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage() {
        mImageView.cropToCenter();
    }
}

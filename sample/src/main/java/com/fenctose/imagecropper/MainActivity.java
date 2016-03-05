package com.fenctose.imagecropper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.fenchtose.nocropper.CropperView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.imageview)
    CropperView mImageView;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_portrait);
        ButterKnife.bind(this);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo_image);
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

    @OnClick(R.id.rotate_button)
    public void onImageRotateClicked() {
        rotateImage();
    }

    @OnClick(R.id.snap_button)
    public void onImageSnapClicked() {
        snapImage();
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

//        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
//        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage() {
        mImageView.cropToCenter();
    }
}

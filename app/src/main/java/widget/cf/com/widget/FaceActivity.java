package widget.cf.com.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import widget.cf.com.widget.databinding.ActivityFaceBinding;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.face.FaceHelper;

public class FaceActivity extends BaseActivity {
    ActivityFaceBinding binding;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face);
        binding.imageIv.setImageBitmap(bitmap);
    }

    public void blur(View view) {
        FaceHelper.blurFace(bitmap, 20, bitmap1 -> binding.imageIv.setImageBitmap(bitmap1));
    }

    public void mosaic(View view) {
        FaceHelper.mosaicFace(bitmap, 20, bitmap1 -> binding.imageIv.setImageBitmap(bitmap1));
    }
}

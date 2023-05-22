package com.example.classproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 3;

    private Button btn_photo;
    private ImageView iv_photo;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        iv_photo = findViewById(R.id.iv_photo);
        btn_photo = findViewById(R.id.btn_photo);

        // 카메라 및 외부 저장소에 대한 권한을 확인합니다.
        if (hasCameraPermission() && hasWriteExternalStoragePermission()) {
            Log.d(TAG, "권한 설정 완료");
        } else {
            Log.d(TAG, "권한 설정 요청");
            requestPermissions();
        }

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, tesseractActivity.class);
                startActivity(intent);
            }
        });
    }

    // 카메라 권한이 있는지 확인합니다.
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // 외부 저장소 쓰기 권한이 있는지 확인합니다.
    private boolean hasWriteExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // 카메라 및 외부 저장소 권한을 요청합니다.
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSION);
    }

    // 권한 요청 결과를 처리합니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "카메라 권한 승인");
                } else {
                    Toast.makeText(this, "카메라 권한을 승인해야 사진을 찍을 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // 사진 촬영을 위한 카메라 앱을 실행합니다.
    private void takePhoto() {
        if (hasCameraPermission()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

            // 이미지가 저장될 URI를 생성합니다.
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 사진 촬영 결과를 처리합니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                // URI에서 이미지 비트맵을 가져옵니다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (bitmap != null) {
                    // 이미지를 회전시킵니다.
                    bitmap = rotateImageIfRequired(bitmap, imageUri);
                    iv_photo.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 이미지를 회전시키는 메서드입니다.
    private Bitmap rotateImageIfRequired(Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream input = getContentResolver().openInputStream(imageUri);
        ExifInterface exifInterface = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exifInterface = new ExifInterface(input);
        } else {
            exifInterface = new ExifInterface(imageUri.getPath());
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotateAngle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateAngle = 270;
                break;
        }
        if (rotateAngle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateAngle);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

}
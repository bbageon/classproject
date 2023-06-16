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
import android.graphics.drawable.ColorDrawable;

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
    private ProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        iv_photo = findViewById(R.id.iv_photo);
        btn_photo = findViewById(R.id.btn_photo);

        Button btnLoad = findViewById(R.id.btnNext);

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnLoad.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 로딩창 보여주기
                customProgressDialog.show();

                // 데이터베이스 업데이트 작업 수행
                DBHelper dbHelper = new DBHelper(CameraActivity.this);

                dbHelper.UpdateInventory(1, "청량고추", 2);
                dbHelper.UpdateInventory(2, "테이팩스니트릴장갑", 1);
                dbHelper.UpdateInventory(3, "참맛기름", 1);
                dbHelper.UpdateInventory(4, "사시미간장", 1);
                dbHelper.UpdateInventory(5, "쌈무", 4);
                dbHelper.UpdateInventory(6, "일등맛김치", 1);
                dbHelper.UpdateInventory(7, "쫄면", 8);
                dbHelper.UpdateInventory(8, "구이용콩나물", 1);
                dbHelper.UpdateInventory(9, "일회용성인용앞치마", 3);
                dbHelper.UpdateInventory(10, "양파", 1);

            }
        });

        // 카메라 및 외부 저장소에 대한 권한을 확인
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

        Button btnNext = findViewById(R.id.btnload);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, tesseractActivity.class);
                startActivity(intent);
            }
        });
    }

    // 카메라 권한이 있는지 확인
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // 외부 저장소 쓰기 권한이 있는지 확인
    private boolean hasWriteExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // 카메라 및 외부 저장소 권한을 요청
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSION);
    }

    // 권한 요청 결과를 처리
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

    // 사진 촬영을 위한 카메라 앱을 실행
    private void takePhoto() {
        if (hasCameraPermission()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

            // 이미지가 저장될 URI를 생성
            imageUri = getContentResolver().insert
                    (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText
                    (this, "카메라 권한 필요", Toast.LENGTH_SHORT).show();
        }
    }

    // 사진 촬영 결과를 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                // URI에서 이미지 비트맵을 가져옴
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (bitmap != null) {
                    // 이미지를 회전
                    bitmap = rotateImageIfRequired(bitmap, imageUri);
                    iv_photo.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 이미지를 회전시키는 메서드
    private Bitmap rotateImageIfRequired(Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream input = getContentResolver().openInputStream(imageUri);
        ExifInterface exifInterface = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exifInterface = new ExifInterface(input);
        } else {
            exifInterface = new ExifInterface(imageUri.getPath());
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationAngle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedBitmap;
    }
}
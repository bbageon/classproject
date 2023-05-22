package com.example.classproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class tesseractActivity extends AppCompatActivity {

    Bitmap image; //사용되는이미지
    private TessBaseAPI mTess;  //Tess API 참조
    String datapath =""; // 언어데이터 경로
    TextView OCRTextView; // OCR 결과뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesseract);

        OCRTextView = findViewById(R.id.OCRTextView);

        // 이미지 디코딩을 위한 초기화(Resource 폴더에 저장한 샘플 그림파일을 bitmap 으로 만들어 리턴)
        image = BitmapFactory.decodeResource(getResources(), R.drawable.sample_eng);
        // 언어파일 경로, getfilesdir() : 일반 파일들의 저장 경로 반환
        datapath = getFilesDir()+ "/tesseract";

        // 트레이닝 데이터가 카피되어있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        // Tesseract API 언어 세팅
        String lang ="eng";

        // OCR 세팅
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

    }
    
    // 이미지에서 텍스트 읽기
    public void processImage(View view) {
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();

        OCRTextView.setText(OCRresult);
    }


    private void checkFile(File dir) {
        // dir 없으면 dir 먼저생성하고 파일 생성
        if (!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        if (dir.exists()) {
            String datafilepath = datapath+ "파일경로";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath =  datapath+"파일경로";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("파일경로");
            OutputStream outStream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1){
                outStream.write(buffer, 0, read);
            }
            outStream.flush();
            outStream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
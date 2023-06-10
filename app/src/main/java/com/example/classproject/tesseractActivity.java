package com.example.classproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Intent;

/*import com.googlecode.tesseract.android.TessBaseAPI;*/

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class tesseractActivity extends AppCompatActivity {

    Bitmap image; //사용되는이미지
    /*private TessBaseAPI mTess;*/  //Tess API 참조
    String datapath =""; // 언어데이터 경로
    TextView OCRTextView; // OCR 결과뷰
    Button btn_ocr;
    private Button bottomButton;
    private TableLayout tablelayout1;

    private DBHelper dbHelper;

    private Button modifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesseract);

        OCRTextView = findViewById(R.id.OCRTextView);
        bottomButton = findViewById(R.id.bottomButton);
        tablelayout1 = findViewById(R.id.tableLayout1);
        modifyBtn = findViewById(R.id.modifyButton);
//        btn_ocr = findViewById(R.id.ocrButton);

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tesseractActivity.this, calendar_t.class);
                startActivity(intent);
            }
        });

        // 이미지 디코딩을 위한 초기화(Resource 폴더에 저장한 샘플 그림파일을 bitmap 으로 만들어 리턴)
        /*image = BitmapFactory.decodeResource(getResources(), R.drawable.sample_kor);*/

        // 언어파일 경로, getfilesdir() : 일반 파일들의 저장 경로 반환
        datapath = getFilesDir()+ "/tesseract/";

        // 트레이닝 데이터가 카피되어있는지 체크
        checkFile(new File(datapath + "tessdata/"), "kor");
        checkFile(new File(datapath + "tessdata/"), "eng");


        // Tesseract API 언어 세팅
        String lang = "kor+eng";

        // OCR 세팅
        /*mTess = new TessBaseAPI();
        mTess.init(datapath, lang);*/

        //텍스트 추출 버튼
        /*btn_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지를 가져와 비트맵으로 변환
                BitmapDrawable d = (BitmapDrawable) ((ImageView) findViewById(R.id.imageView)).getDrawable();
                image = d.getBitmap();

                String OCRResult = null;
                mTess.setImage(image);

                //텍스트 추출
                OCRResult = mTess.getUTF8Text();
                TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
                OCRTextView.setText(OCRResult);
            }
        });*/

        addTableRow(1, "청양고추", 2, "2023-06-05");
        addTableRow(2, "테이팩스니트릴장갑", 2, "2023-06-05");
        addTableRow(3, "참맛기름", 1, "2023-06-05");
        addTableRow(4, "사시미간장", 1, "2023-06-05");
        addTableRow(5, "쌈무", 4, "2023-06-05");
        addTableRow(6, "일등맛김치", 1, "2023-06-05");
        addTableRow(7, "쫄면", 8, "2023-06-05");
        addTableRow(8, "구이용콩나물", 1, "2023-06-05");
        addTableRow(9, "일회용성인용앞치마", 3, "2023-06-05");
        addTableRow(10, "양파", 1, "2023-06-05");


    }

    private void addTableRow(int id, String name, int quantity, String date) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TextView idTextView = new TextView(this);
        idTextView.setText(id);
        EditText nameEditText = new EditText(this);
        nameEditText.setText(name);
        EditText quantityEditText = new EditText(this);
        quantityEditText.setText(Integer.toString(quantity)); // quantity 변수를 문자열로 변환하여 설정
        EditText dateEditText = new EditText(this);
        dateEditText.setText(date);

        row.addView(idTextView);
        row.addView(nameEditText);
        row.addView(quantityEditText);
        row.addView(dateEditText);

        tablelayout1.addView(row);

        // 사용자가 값을 수정하고 버튼을 클릭할 때 호출되는 이벤트 리스너 등록
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 수정된 값을 가져옴
                String updatedName = nameEditText.getText().toString();
                int updatedQuantity = Integer.parseInt(quantityEditText.getText().toString());
                String updatedDate = dateEditText.getText().toString();

                // UpdateInventory() 메소드를 호출하여 값을 업데이트
                dbHelper.UpdateInventory(id, updatedName, updatedQuantity);
            }
        });

        row.addView(modifyBtn);
    }




    // 이미지에서 텍스트 읽기, 텍스트 추출버튼



    private void checkFile(File dir, String lang) {
        // dir 없으면 dir 먼저생성하고 파일 생성
        if (!dir.exists()&& dir.mkdirs()) {
            copyFiles(lang);
        }
        if (dir.exists()) {
            String datafilepath = datapath+ "/tessdata/"+lang+".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(lang);
            }
        }
    }

    private void copyFiles(String lang) {
        try {
            String filepath =  datapath+"/tessdata/"+lang+".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/"+lang+".traineddata");
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
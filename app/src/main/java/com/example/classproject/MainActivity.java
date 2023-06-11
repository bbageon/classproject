package com.example.classproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private DBHelper dbHelper;

    private Button backBtn;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = new DBHelper(this);

        backBtn = findViewById(R.id.btn_main);
        deleteBtn = findViewById(R.id.btn_delete);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, calendar_t.class);
                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창을 띄우고 ID 값을 입력 받음
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제할 상품의 ID를 입력하세요:");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = Integer.parseInt(input.getText().toString());
                        DBHelper dbHelper = new DBHelper(MainActivity.this);
                        dbHelper.DeleteInventory(id);
                        // 삭제 완료 후 필요한 작업 수행

                        // 예를 들어, 데이터 삭제 후 화면을 갱신하거나 메시지를 표시
                        Toast.makeText(MainActivity.this, "데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // 데이터베이스에 새로운 데이터 삽입 예시
        /*dbHelper.InsertInventory(1, "청량고추", 10);
        dbHelper.InsertInventory(2, "테이팩스니트릴장갑", 15);
        dbHelper.InsertInventory(3,"참맛기름", 20);
        dbHelper.InsertInventory(4,"사시미간장", 25);
        dbHelper.InsertInventory(5,"쌈무", 30);
        dbHelper.InsertInventory(6,"일등맛김치", 35);
        dbHelper.InsertInventory(7,"쫄면", 40);
        dbHelper.InsertInventory(8,"구이용콩나물", 45);
        dbHelper.InsertInventory(9,"일회용성인용앞치마", 50);
        dbHelper.InsertInventory(10,"양파", 55);

        dbHelper.InsertInventoryForDate(11, "환타", 10, "2023-06-05");
        dbHelper.InsertInventoryForDate(12, "소단단두부", 20, "2023-06-05");
        dbHelper.InsertInventoryForDate(13, "하늘그린 큐티물티", 30, "2023-06-05");
        dbHelper.InsertInventoryForDate(14, "다진마늘", 40, "2023-06-05");
        dbHelper.InsertInventoryForDate(15, "다목적수세미", 50, "2023-06-05");
        dbHelper.InsertInventoryForDate(16, "키친아트철수세미", 60, "2023-06-05");

        dbHelper.InsertInventoryForDate(17, "만능김가루", 100, "2023-06-01");
        dbHelper.InsertInventoryForDate(18, "깐마늘", 150, "2023-06-01");
        dbHelper.InsertInventoryForDate(19, "도깨비폼", 200, "2023-06-01");
        dbHelper.InsertInventoryForDate(20, "크린장갑", 250, "2023-06-01");
        dbHelper.InsertInventoryForDate(21, "참기름", 300, "2023-06-01");
        dbHelper.InsertInventoryForDate(22, "파채", 350, "2023-06-01");
        dbHelper.InsertInventoryForDate(23, "명이나물", 400, "2023-06-01");*/

        /*dbHelper.DeleteInventory(1);
        dbHelper.DeleteInventory(2);
        dbHelper.DeleteInventory(3);
        dbHelper.DeleteInventory(4);
        dbHelper.DeleteInventory(5);
        dbHelper.DeleteInventory(6);
        dbHelper.DeleteInventory(7);
        dbHelper.DeleteInventory(8);
        dbHelper.DeleteInventory(9);
        dbHelper.DeleteInventory(10);
        dbHelper.DeleteInventory(11);
        dbHelper.DeleteInventory(12);
        dbHelper.DeleteInventory(13);
        dbHelper.DeleteInventory(14);
        dbHelper.DeleteInventory(15);
        dbHelper.DeleteInventory(16);
        dbHelper.DeleteInventory(17);
        dbHelper.DeleteInventory(18);
        dbHelper.DeleteInventory(19);
        dbHelper.DeleteInventory(20);
        dbHelper.DeleteInventory(21);
        dbHelper.DeleteInventory(22);
        dbHelper.DeleteInventory(23);*/

        updateTable();

    }
    private void updateTable() {
        // DB에서 모든 상품 정보 가져오기
        ArrayList<InventoryItem> inventoryItems = dbHelper.getInventoryItems();

        tableLayout.removeAllViews();

        // 테이블의 첫 번째 행은 열의 제목을 나타냄
        TableRow headerRow = new TableRow(this);

        TextView headerId = new TextView(this);
        headerId.setText("상품번호");
        headerRow.addView(headerId);

        TextView headerName = new TextView(this);
        headerName.setText("상품명");
        headerRow.addView(headerName);

        TextView headerQuantity = new TextView(this);
        headerQuantity.setText("수량");
        headerRow.addView(headerQuantity);

        TextView headerSales = new TextView(this);
        headerSales.setText("판매량");
        headerRow.addView(headerSales);

        TextView headerStock = new TextView(this);
        headerStock.setText("재고량");
        headerRow.addView(headerStock);

        TextView headerDate = new TextView(this);
        headerDate.setText("날짜");
        headerRow.addView(headerDate);

        tableLayout.addView(headerRow);

        // 각각의 상품 정보에 대해 TableRow을 생성하여 표에 추가
        for (InventoryItem item : inventoryItems) {
            TableRow tableRow = new TableRow(this);

            TextView textViewId = new TextView(this);
            textViewId.setText(String.valueOf(item.getId()));
            tableRow.addView(textViewId);

            TextView textViewName = new TextView(this);
            textViewName.setText(item.getName());
            tableRow.addView(textViewName);

            TextView textViewQuantity = new TextView(this);
            textViewQuantity.setText(String.valueOf(item.getQuantity()));
            tableRow.addView(textViewQuantity);

            TextView textViewSales = new TextView(this);
            textViewSales.setText(String.valueOf(item.getSales()));
            tableRow.addView(textViewSales);

            TextView textViewStock = new TextView(this);
            textViewStock.setText(String.valueOf(item.getStock()));
            tableRow.addView(textViewStock);

            TextView textViewDate = new TextView(this);
            textViewDate.setText(item.getDate());
            tableRow.addView(textViewDate);

            tableLayout.addView(tableRow);
        }
    }

}

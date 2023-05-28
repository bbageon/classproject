package com.example.classproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = new DBHelper(this);

        // 데이터베이스에 새로운 데이터 삽입 예시
        dbHelper.InsertInventory(7, "음료수", 15);
        dbHelper.InsertInventory(8, "과자", 20);

        dbHelper.UpdateInventory(7, "환타", 20);
        dbHelper.UpdateInventory(8, "맛동산", 25);

        // dbHelper.DeleteInventory(1);

        updateTable();
    }
    private void updateTable() {
        // DB에서 모든 상품 정보 가져오기
        ArrayList<InventoryItem> inventoryItems = dbHelper.getInventoryItems();

        // "상품번호"를 기준으로 내림차순 정렬
        Collections.sort(inventoryItems, new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem item1, InventoryItem item2) {
                // item1과 item2의 "상품번호"를 비교하여 내림차순으로 정렬
                return Integer.compare(item1.getId(), item2.getId());
            }
        });

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
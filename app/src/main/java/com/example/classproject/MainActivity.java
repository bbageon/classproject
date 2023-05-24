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
        dbHelper.InsertInventory(1, "음료수", 10);
        dbHelper.InsertInventory(2, "과자", 15);

        dbHelper.UpdateInventory(1, "환타", 15);
        dbHelper.UpdateInventory(2, "맛동산", 20);

        dbHelper.DeleteInventory(1);

        updateTable();
    }
    private void updateTable() {

        // DB에서 모든 상품 정보 가져오기
        ArrayList<InventoryItem> inventoryItems = dbHelper.getInventoryItems();

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
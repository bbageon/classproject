package com.example.classproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv_inventory; // m = member
    private FloatingActionButton mBtn_write;
    private ArrayList<InventoryItem> mInventoryItems;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        하정훈 병신

        dbHelper = new DBHelper(this);
        ArrayList<InventoryItem> inventoryItems = dbHelper.getInventoryItems();


        dbHelper.InsertInventory(1, "상품1", 10);
        dbHelper.InsertInventory(2, "상품2", 20);

        dbHelper.UpdateInventory(1, "상품1", 15);
        dbHelper.UpdateInventory(2, "상품2", 30);

        // 로그로 상품 정보 출력
        for (InventoryItem item : inventoryItems) {
            Log.d("Inventory", "ID: " + item.getId());
            Log.d("Inventory", "Name: " + item.getName());
            Log.d("Inventory", "Quantity: " + item.getQuantity());
            Log.d("Inventory", "Sales: " + item.getSales());
            Log.d("Inventory", "Stock: " + item.getStock());
            Log.d("Inventory", "Date: " + item.getDate());
        }
    }
}
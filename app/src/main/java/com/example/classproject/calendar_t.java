package com.example.classproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class calendar_t extends AppCompatActivity {
    private ScrollView textBoxContainer;
    private LinearLayout textBoxLayout;
    private Button submitButton;
    private Button resetButton;
    private Button next2Button;

    private String selectedDate;
    private CalendarView calendarView;

    // TextView에서 재고량을 일정한 간격으로 맞추기 위한 상수
    private static final int STOCK_WIDTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maincalendardb);

        textBoxContainer = findViewById(R.id.textBoxContainer);
        textBoxLayout = findViewById(R.id.textBoxLayout);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        calendarView = findViewById(R.id.calendarView1);
        next2Button = findViewById(R.id.next2Button);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 월은 0부터 시작하므로 1을 더해줍니다.
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            }
        });

        next2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(calendar_t.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    showOrderItems(selectedDate);
                } else {
                    // 선택된 날짜가 없는 경우에 대한 처리
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(calendar_t.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // 날짜 선택 후 "날짜 선택" 버튼을 누르면 해당 날짜에 넣은 데이터의 값 중 상품명, 재고량을 보여줌
    // 해당 날짜에 넣은 데이터가 없으면 "데이터가 없습니다." 출력
    private void showOrderItems(String selectedDate) {
        textBoxLayout.removeAllViews();

        DBHelper dbHelper = new DBHelper(this);
        ArrayList<String> orderItems = dbHelper.getOrderItemsByDate(selectedDate);

        if (orderItems.isEmpty()) {
            TextView textView = new TextView(this);
            textView.setText("데이터가 없습니다.");
            textView.setPadding(0, 8, 0, 8);
            textBoxLayout.addView(textView);
        } else {
            for (String item : orderItems) {
                TextView textView = new TextView(this);
                textView.setText(item);
                textView.setPadding(0, 8, 0, 8);
                textBoxLayout.addView(textView);
            }
        }

        textBoxContainer.setVisibility(View.VISIBLE);
    }


    private void resetTextBoxes() {
        textBoxLayout.removeAllViews();
        textBoxContainer.setVisibility(View.GONE);
    }
}

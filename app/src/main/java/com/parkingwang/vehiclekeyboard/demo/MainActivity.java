/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parkingwang.vehiclekeyboard.KeyboardBinder;
import com.parkingwang.vehiclekeyboard.OnInputChangedListener;
import com.parkingwang.vehiclekeyboard.core.KeyboardType;
import com.parkingwang.vehiclekeyboard.support.PopupKeyboardHelper;
import com.parkingwang.vehiclekeyboard.view.InputView;
import com.parkingwang.vehiclekeyboard.view.KeyboardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private InputView mInputView;
    private KeyboardView mKeyboardView;

    private KeyboardBinder mKeyboardBinder;

    private final List<String> mTestNumber = new ArrayList<>();

    private final Random mRandom = new Random();

    private KeyboardView mPopupKeyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputView = findViewById(R.id.input_view);
        mKeyboardView = findViewById(R.id.keyboard_view);
        final Button lockType = findViewById(R.id.lock_type);

        // 绑定
        mKeyboardBinder = KeyboardBinder.with(mKeyboardView, mInputView);
        mKeyboardBinder.attach()
                .setKeyboardInputChangeSync()
                .useDefaultMessageListener()
                .setLockTypeButton(lockType)
                .updateNumberAndSelectFirstItem("粤L79P99");

        mTestNumber.add("粤A12345");
        mTestNumber.add("粤A987654");
        mTestNumber.add("粤A0123");
        mTestNumber.add("WJ12345");
        mTestNumber.add("WJ粤12345");

        mKeyboardBinder.setOnInputChangeListener(new OnInputChangedListener() {
            @Override
            public void onChanged(String number, boolean isCompleted) {
                Toast.makeText(MainActivity.this, number + ", 完成：" + isCompleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted(String number, boolean isAutoCompleted) {
                Toast.makeText(MainActivity.this, number + ", 自动完成：" + isAutoCompleted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        int id = view.getId();
        // 切换键盘类型
        switch (id) {
            case R.id.full:
                mKeyboardView.setKeyboardType(KeyboardType.FULL);
                // 更新车牌号码
                mKeyboardBinder.updateNumberAndSelectFirstItem(mInputView.getNumber());
                break;
            case R.id.civil:
                mKeyboardView.setKeyboardType(KeyboardType.CIVIL);
                // 更新车牌号码
                mKeyboardBinder.updateNumberAndSelectFirstItem(mInputView.getNumber());
                break;
            case R.id.civil_wj:
                mKeyboardView.setKeyboardType(KeyboardType.CIVIL_WJ);
                // 更新车牌号码
                mKeyboardBinder.updateNumberAndSelectFirstItem(mInputView.getNumber());
                break;
            case R.id.adjust_height:
                mKeyboardView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mKeyboardView.requestLayout();
                break;
            case R.id.fixed_height:
                mKeyboardView.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics());
                mKeyboardView.requestLayout();
                break;
            case R.id.test_number:
                mKeyboardBinder.updateNumberAndSelectLastItem(mTestNumber.get(mRandom.nextInt(mTestNumber.size())));
                break;
            case R.id.clear_number:
                mKeyboardBinder.updateNumberAndSelectFirstItem("");
                break;
            case R.id.popup_keyboard:
                if (mPopupKeyboardView == null) {
                    mPopupKeyboardView = new KeyboardView(this);
                    mPopupKeyboardView.setKeyboardType(KeyboardType.CIVIL_WJ);
                }
                if (PopupKeyboardHelper.showToActivity(this, mPopupKeyboardView)) {
                    Toast.makeText(this, "弹出键盘", Toast.LENGTH_SHORT).show();
                } else if (PopupKeyboardHelper.dismissFromActivity(this)) {
                    Toast.makeText(this, "隐藏键盘", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

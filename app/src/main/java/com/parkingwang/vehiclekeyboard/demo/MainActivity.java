/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parkingwang.keyboard.KeyboardInputController;
import com.parkingwang.keyboard.PopupKeyboard;
import com.parkingwang.keyboard.view.InputView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private InputView mInputView;
    private EditText mProvinceView;

    private final List<String> mTestNumber = new ArrayList<>();

    private PopupKeyboard mPopupKeyboard;
    private long mTestIndex = 0;

    private boolean mHideOKKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputView = findViewById(R.id.input_view);
        mProvinceView = findViewById(R.id.province_value);

        final Button lockTypeButton = findViewById(R.id.lock_type);

        mTestNumber.add("粤A12345");
        mTestNumber.add("粤BD12345");
        mTestNumber.add("粤Z1234港");
        mTestNumber.add("WJ粤12345");
        mTestNumber.add("WJ粤1234X");
        mTestNumber.add("NA00001");
        mTestNumber.add("123456使");
        mTestNumber.add("使123456");
        mTestNumber.add("粤A1234领");
        mTestNumber.add("粤12345领");
        mTestNumber.add("民航12345");
        mTestNumber.add("粤C0");
        mTestNumber.add("粤");
        mTestNumber.add("WJ粤12");

        // 创建弹出键盘
        mPopupKeyboard = new PopupKeyboard(this);
        // 弹出键盘内部包含一个KeyboardView，在此绑定输入两者关联。
        mPopupKeyboard.attach(mInputView, this);

        // 隐藏确定按钮
        mPopupKeyboard.getKeyboardEngine().setHideOKKey(mHideOKKey);

        // KeyboardInputController提供一个默认实现的新能源车牌锁定按钮
        mPopupKeyboard.getController()
                .setDebugEnabled(true)
                .bindLockTypeProxy(new KeyboardInputController.ButtonProxyImpl(lockTypeButton) {
                    @Override
                    public void onNumberTypeChanged(boolean isNewEnergyType) {
                        super.onNumberTypeChanged(isNewEnergyType);
                        if (isNewEnergyType) {
                            lockTypeButton.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        } else {
                            lockTypeButton.setTextColor(getResources().getColor(android.R.color.black));
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mInputView.performFirstFieldView();
    }

    public void onClick(View view) {
        int id = view.getId();
        // 切换键盘类型
        switch (id) {
            case R.id.test_number:
                final int idx = (int) (mTestIndex % mTestNumber.size());
                mTestIndex++;
                if (idx == 0) {
                    mTestIndex = 0;
                }
                // 上面测试例子中，第12个，指定为新能源车牌，部分车牌
                if (idx == 11) {
                    mPopupKeyboard.getController().updateNumberLockType(mTestNumber.get(idx), true);
                } else {
                    mPopupKeyboard.getController().updateNumber(mTestNumber.get(idx));
                }
                break;
            case R.id.clear_number:
                mPopupKeyboard.getController().updateNumber("");
                break;
            case R.id.popup_keyboard:
                if (mPopupKeyboard.isShown()) {
                    mPopupKeyboard.dismiss(MainActivity.this);
                } else {
                    mPopupKeyboard.show(MainActivity.this);
                }
                break;

            case R.id.hide_ok_key:
                mHideOKKey = !mHideOKKey;
                mPopupKeyboard.getKeyboardEngine().setHideOKKey(mHideOKKey);
                Toast.makeText(getBaseContext(),
                        "演示“确定”键盘状态，将在下一个操作中生效: " + (mHideOKKey ? "隐藏" : "显示"), Toast.LENGTH_SHORT)
                        .show();
                break;

            case R.id.commit_province:
                final String name = mProvinceView.getText().toString();
                mPopupKeyboard.getKeyboardEngine().setLocalProvinceName(name);
                Toast.makeText(getBaseContext(),
                        "演示“周边省份”重新排序，将在下一个操作中生效：" + name, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

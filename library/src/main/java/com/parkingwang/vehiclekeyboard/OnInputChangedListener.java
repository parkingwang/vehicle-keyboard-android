package com.parkingwang.vehiclekeyboard;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public interface OnInputChangedListener {

    void onChanged(String number, boolean isCompleted);

    void onCompleted(String number, boolean isAutoCompleted);
}

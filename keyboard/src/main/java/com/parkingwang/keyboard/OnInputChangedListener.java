package com.parkingwang.keyboard;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * 输入框车牌号码输入状态变化回调接口。
 */
public interface OnInputChangedListener {

    /**
     * 当输入框的车牌号码发生变化时，回调此方法。
     *
     * @param number      当前输入框的车牌号码
     * @param isCompleted 当前的车牌号码是否已输入完成
     */
    void onChanged(String number, boolean isCompleted);

    /**
     * 当车牌号码被输入完成后，调用此方法。
     * @param number 完整输入的车牌号码
     * @param isAutoCompleted 当前的输入完成状态，是否属于自动完成。
     *                        输入完成状态有两种：
     *                          1. 从部分车牌一直输入，直到输完最后一位时，会触发自动完成回调。此时isAutoCompleted为True。
     *                          2. 通过点击“确定”按键来触发输入完成。此时isAutoCompleted为False。
     */
    void onCompleted(String number, boolean isAutoCompleted);
}

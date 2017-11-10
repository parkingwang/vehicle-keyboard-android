/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com), 陈永佳 (yoojiachen@parkingwang.com)
 * @since 2017-09-25 0.1
 */
public class EngineRunner {

    private static final String TAG = "EngineRunner";

    private static final int DO_NOT_OPTIMIZE = -1;
    private static final String JS_FILE_NAME = "engine.js";
    private static final String SOURCE_NAME = "JavaScript";

    private static volatile String JS_TEXT = null;

    private Context mRhino;
    private ScriptableObject mEngineScope;
    private Function mUpdateFunction;

    public EngineRunner(android.content.Context context) {
        if (JS_TEXT == null) {
            JS_TEXT = readJSContent(context);
        }
    }

    public void start() {
        if (mUpdateFunction != null) {
            return;
        }
        mRhino = Context.enter();
        mRhino.setOptimizationLevel(DO_NOT_OPTIMIZE);
        mEngineScope = mRhino.initStandardObjects();
        mRhino.evaluateString(mEngineScope, JS_TEXT, SOURCE_NAME, 1, null);
        mRhino.evaluateString(mEngineScope, "var engine = new KeyboardEngine()", SOURCE_NAME, 1, null);
        Object object = mEngineScope.get("engine", mEngineScope);
        if (!(object instanceof Scriptable)) {
            return;
        }
        Scriptable engine = (Scriptable) object;
        Object functionObject = engine.get("update", engine);
        if (!(functionObject instanceof Function)) {
            return;
        }
        mUpdateFunction = (Function) functionObject;
    }

    public void stop() {
        if (mUpdateFunction != null) {
            mUpdateFunction = null;
            Context.exit();
        }
    }

    /**
     * 更新键盘
     * @param keyboardType 指定更新当前键盘布局的键盘类型
     * @param showIndex 指定更新当前键盘布局的车牌号码位置，例如当前输入为首个字符，则index为0
     * @param presetNumber 当前已预设置的车牌号码，可以是完整车牌号码，也可以是部分号码
     * @param numberType 指定车牌号码类型。例如可以强制指定为新能源车牌类型
     * @return 返回更新之后的键盘信息
     */
    public KeyboardEntry update(KeyboardType keyboardType, int showIndex, @NonNull String presetNumber, @NonNull NumberType numberType) {
        if (mUpdateFunction == null) {
            Log.e(TAG, "You need to call start() before call update method");
            return null;
        }
        Object[] params = new Object[]{keyboardType.ordinal(), showIndex, presetNumber, numberType.ordinal()};
        return update(params);
    }

    @Nullable
    private KeyboardEntry update(Object[] params) {
        Object result = mUpdateFunction.call(mRhino, mEngineScope, mEngineScope, params);
        if (!(result instanceof NativeObject)) {
            return null;
        }
        return parseResult((NativeObject) result);
    }

    private KeyboardEntry parseResult(NativeObject nativeObject) {
        List<List<KeyEntry>> keyboard = new ArrayList<>(5);
        int index = 0;
        String presetNumber = null;
        KeyboardType keyboardType = null;
        NumberType presetNumberType = NumberType.AUTO_DETECT;
        int numberLength = 0;
        int numberLimitLength = 0;
        NumberType detectedNumberType = NumberType.AUTO_DETECT;

        Set<Map.Entry<Object, Object>> entrySet = nativeObject.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            if ("index".equals(key)) {
                index = (int) Context.toNumber(value);
            } else if ("presetNumber".equals(key)) {
                presetNumber = Context.toString(value);
            } else if ("keyboardType".equals(key)) {
                keyboardType = KeyboardType.values()[(int) Context.toNumber(value)];
            } else if ("numberType".equals(key)) {
                presetNumberType = NumberType.values()[(int) Context.toNumber(value)];
            } else if ("numberLength".equals(key)) {
                numberLength = (int) Context.toNumber(value);
            } else if ("numberLimitLength".equals(key)) {
                numberLimitLength = (int) Context.toNumber(value);
            } else if (key.startsWith("row")) {
                List<KeyEntry> keyList = getKeyEntries(entry);
                if (keyList == null) continue;
                keyboard.add(keyList);
            } else if ("detectedNumberType".equals(key)) {
                detectedNumberType = NumberType.values()[(int) Context.toNumber(value)];
            }
        }
        return new KeyboardEntry(index, presetNumber, keyboardType, presetNumberType, numberLength,
                numberLimitLength, keyboard, detectedNumberType);
    }

    @Nullable
    private List<KeyEntry> getKeyEntries(Map.Entry<Object, Object> entry) {
        if (!(entry.getValue() instanceof NativeArray)) {
            return null;
        }

        NativeArray nativeArray = (NativeArray) entry.getValue();
        if (nativeArray.isEmpty()) {
            return null;
        }

        List<KeyEntry> keyList = new ArrayList<>();
        final int size = nativeArray.size();
        for (int i = 0; i < size; i++) {
            NativeObject elem = (NativeObject) nativeArray.get(i);
            keyList.add(new KeyEntry(
                    Context.toString(elem.get("text")),
                    (int) Context.toNumber(elem.get("keyCode")),
                    Context.toBoolean(elem.get("enabled")),
                    Context.toBoolean(elem.get("isFunKey"))
            ));
        }
        return keyList;
    }

    private static String readJSContent(android.content.Context context) {
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            is = context.getAssets().open(JS_FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}

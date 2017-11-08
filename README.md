# Keyboard-Android

## 依赖

```groovy
    implementation 'com.parkingwang:vehicle-keyboard:0.5.1-SNAPSHOT'
    // OR
    compile 'com.parkingwang:vehicle-keyboard:0.5.1-SNAPSHOT'
```

## 使用

### 车牌号码输入组件

![](./PWK_INPUT_VIEW.png)

在XML中放置输入组件：

```xml

    <com.parkingwang.vehiclekeyboard.view.InputView
        android:id="@+id/input_view"
        app:pwkInputTextSize="22sp"
        android:layout_width="match_parent"
        android:layout_height="60dp"/>

```
### 车牌号码键盘组件

![](./PWK_KEYBOARD_VIEW.png)

在XML中放置键盘组件：

```xml
    <com.parkingwang.vehiclekeyboard.view.KeyboardView
            android:id="@+id/keyboard"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            app:pwkKeyboardType="CIVIL"/>
```

在代码中绑定输入组件与键盘的关联：

```java
    // Init Views

    // 输入组件View
    mInputView = findViewById(R.id.input_view);
    // 键盘View
    mKeyboardView = findViewById(R.id.keyboard_view);
    // 锁定新能源车牌View
    mLockType = findViewById(R.id.lock_type);

    mKeyboardView.setKeyboardType(KeyboardType.CIVIL_WJ);

    // 绑定
    mKeyboardBinder = KeyboardBinder.with(mKeyboardView, mInputView);
    mKeyboardBinder.attach()
            .setKeyboardInputChangeSync()
            .useDefaultMessage()
            .setLockTypeButton(mLockType)
            .resetNumber("");


```

## API doc

https://parkingwang.github.io/vehicle-keyboard-android/index.html

## 其他设置

### 设置键盘按钮文字大小

在Java代码中添加以下设置：
```java
    mKeyboardView.setCNTextSize(float); //设置中文字体大小
    mKeyboardView.setENTextSize(float); //设置英文字母或数字字体大小
```

### 设置确定按键及气泡字体颜色

在colors.xml中覆盖以下颜色值以修改键盘主色
```xml
    <!--确定按键正常状态下的颜色，以及气泡的颜色-->
    <color name="pwk_keyboard_primary_color">#418AF9</color>
    <!--确定按键按下时的颜色-->
    <color name="pwk_keyboard_primary_dark_color">#3A7CE0</color>
```
### 设置输入组件字体大小：

```xml
    <com.parkingwang.vehiclekeyboard.view.InputView
            app:pwkInputTextSize="22sp"
            ..../>
```

### 设置键盘类型

键盘组件支持三种键盘类型：

- `KeyboardType.FULL` 全键盘模式，包括民用、警察、军队、民航、使馆等车牌类型；
- `KeyboardType.CIVIC` 民用车牌键盘；
- `KeyboardType.CIVIL_WJ` 民用车牌+武警车牌类型；

1. 在Java代码中设置

```java
    mKeyboardView.setKeyboardType(KeyboardType.CIVIL_WJ);
```

2. 在XML中设置

```xml
    <com.parkingwang.vehiclekeyboard.view.KeyboardView
            ...
            app:pwkKeyboardType="CIVIL"/>
```

### 设置键盘按下时的气泡：

1. 正确地显示气泡

由于顶层按键的气泡会显示到键盘之外，因此需要键盘所在的父布局增加以下属性（如果气泡范围超出父布局，则需往上递归设置）：
```xml
    android:clipChildren="false"
```

2. 不显示气泡

```java
    mKeyboardView.setShowBubble(false);
```

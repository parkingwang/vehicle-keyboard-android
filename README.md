# 停车王车牌号码专用键盘 VehicleKeyboard - Android

`VehicleKeyboard`是停车王品牌的各端产品线的基础组件，专为提高中国车牌号码输入速度而定制开发的专用键盘组件，包括以下三个项目：

- `Android` Android项目，为Android客户端定制包括输入组件、键盘组件及相关控制逻辑实现；
- `iOS` iOS客户端项目，为iOS客户端定制包括输入组件、键盘组件及相关控制逻辑实现；
- `JavaScript(Vue.js)` JavaScript(Vue.js)项目，为H5页面定制，包括Web、微信、支付宝等，同样包括输入组件、键盘组件及相关控制逻辑实现

## 项目主页

### Android 版本
- GitHub项目主页： [https://github.com/parkingwang/vehicle-keyboard-android](https://github.com/parkingwang/vehicle-keyboard-android)
- OSChina项目主页： [https://gitee.com/iRainIoT/vehicle-keyboard-android](https://gitee.com/iRainIoT/vehicle-keyboard-android)

### iOS 版本
- GitHub项目主页： [https://github.com/parkingwang/vehicle-keyboard-ios](https://github.com/parkingwang/vehicle-keyboard-ios)
- OSChina项目主页： [https://gitee.com/iRainIoT/vehicle-keyboard-ios](https://gitee.com/iRainIoT/vehicle-keyboard-ios)

### JavaScript Vue.js 版本
- GitHub项目主页： [https://github.com/parkingwang/vehicle-keyboard-js](https://github.com/parkingwang/vehicle-keyboard-js)
- OSChina项目主页： [https://gitee.com/iRainIoT/vehicle-keyboard-js](https://gitee.com/iRainIoT/vehicle-keyboard-js)


## 车牌号码规则

1. 艾润物联公司整理的车牌号码规则：[停车王车牌号码专用键盘规则和设计说明](./NumberRules.md)

1. [中华人民共和国民用机动车号牌](https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%8D%8E%E4%BA%BA%E6%B0%91%E5%85%B1%E5%92%8C%E5%9B%BD%E6%B0%91%E7%94%A8%E6%9C%BA%E5%8A%A8%E8%BD%A6%E5%8F%B7%E7%89%8C)

## 添加依赖

增加仓库地址：

```groovy
repositories {
    jcenter()
}
```

添加库依赖：

```groovy
    implementation 'com.parkingwang:keyboard:0.4.0'
    // OR
    compile 'com.parkingwang:keyboard:0.4.0'
```

## 使用组件

### 车牌号码输入组件 InputView

![](./PWK_INPUT_VIEW.png)

InputView是用于手动输入车牌的组件，提供7-8个用户可选择修改的输入框，如上图所示。

在XML中放置输入组件：

```xml

    <com.parkingwang.vehiclekeyboard.view.InputView
        android:id="@+id/input_view"
        app:pwkInputTextSize="22sp"
        android:layout_width="match_parent"
        android:layout_height="60dp"/>

```

### 车牌号码键盘组件 - KeyboardView

![](./PWK_KEYBOARD_VIEW.png)

KeyboardView是车牌输入键盘组件，提供按车牌类型显示一定规则的键盘布局供用户点击，如上图所示。

在XML中放置键盘组件：

```xml
    <com.parkingwang.vehiclekeyboard.view.KeyboardView
            android:id="@+id/keyboard"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            app:pwkKeyboardType="CIVIL"/>
```

### 输入框和键盘控制器 - KeyboardInputController

在代码中绑定输入组件与键盘的关联：

##### 使用弹出键盘

详见 MainActivity 的演示代码。

```java
    // Init Views

    // 输入组件View
    mInputView = findViewById(R.id.input_view);
    // 锁定新能源车牌View
    mLockType = findViewById(R.id.lock_type);

    // 创建弹出键盘
    mPopupKeyboard = new PopupKeyboard(this);
    // 弹出键盘内部包含一个KeyboardView，在此绑定输入两者关联。
    mPopupKeyboard.attach(mInputView, this);
    mPopupKeyboard.getKeyboardView()
            .setKeyboardType(KeyboardType.CIVIL_WJ);

    // KeyboardInputController提供一个默认实现的新能源车牌锁定按钮
    mPopupKeyboard.getController()
            .setDebugEnabled(true)
            .bindLockTypeProxy(new KeyboardInputController.ButtonProxyImpl(mLockType) {
                @Override
                public void onNumberTypeChanged(boolean isNewEnergyType) {
                    super.onNumberTypeChanged(isNewEnergyType);
                    if (isNewEnergyType) {
                        mLockType.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    } else {
                        mLockType.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
            });


```

##### 不弹出键盘，直接显示

```java

// 使用 KeyboardInputController 来关联
mController = KeyboardInputController
                    .with(mKeyboardView, inputView);

mController.useDefaultMessageHandler();

```

## 键盘样式设置

### 设置键盘按钮文字大小

在Java代码中添加以下设置：
```java
    mKeyboardView.setCNTextSize(float); //设置中文字体大小
    mKeyboardView.setENTextSize(float); //设置英文字母或数字字体大小
```

### 设置键盘主题颜色

在colors.xml中覆盖以下颜色值以修改键盘主题色
```xml
    <color name="pwk_primary_color">#418AF9</color>
    <!--确定按键按下时的颜色-->
    <color name="pwk_primary_dark_color">#3A7CE0</color>
```
### 设置输入组件字体大小：

```xml
    <com.parkingwang.vehiclekeyboard.view.InputView
            app:pwkInputTextSize="22sp"
            ..../>
```

### 设置输入组件的样式

默认提供两种输入组件样式：

#### 1. 混合紧排样式（默认样式） - MIXED

![](./PWK_INPUT_MIXED_STYLE.png)

```xml
    <!--输入框按键样式，最左最右键样式设置-->
    <style name="PWKInputItemStyleKey" parent="PWKInputItemStyle_BORDER_KEY"/>
    <style name="PWKInputItemStyleLeft" parent="PWKInputItemStyle_BORDER_LEFT"/>
    <style name="PWKInputItemStyleRight" parent="PWKInputItemStyle_BORDER_RIGHT"/>
    <!--输入框外部样式-->
    <style name="PWKInputViewStyle" parent="PWKInputViewStyle_MIXED"/>
```

#### 2. 分隔块状样式 - DIVIDED

![](./PWK_INPUT_DIVIDED_STYLE.png)

```xml
    <!--输入框按键样式，最左最右键样式设置-->
    <style name="PWKInputItemStyleKey" parent="PWKInputItemStyle_FILLED_KEY"/>
    <style name="PWKInputItemStyleLeft" parent="PWKInputItemStyle_FILLED_LEFT"/>
    <style name="PWKInputItemStyleRight" parent="PWKInputItemStyle_FILLED_RIGHT"/>
    <!--输入框外部样式-->
    <style name="PWKInputViewStyle" parent="PWKInputViewStyle_DIVIDED"/>

```

在项目的`styles.xml`中覆盖设置以上两种样式配置，可以切换显示不同的样式。可参考 App 的配置代码。

##### 如何修改自己的样式

覆盖`PWKInputItemStyleKey / PWKInputItemStyleLeft / PWKInputItemStyleRight`和`PWKInputViewStyle`来实现。

- `PWKInputItemStyleKey` 控制输入组件内每个输入框的按键Button样式，样式作用于每个Button；
- `PWKInputItemStyleLeft` 控制输入组件内最左侧输入框的按键Button样式，样式作用于一个Button；
- `PWKInputItemStyleRight` 控制输入组件内最右侧输入框的按键Button样式，样式作用于一个Button；
- `PWKInputViewStyle` 控制输入组件的整体样式，作用于LinearLayout；

##### InputView的样式选项

通过覆盖以下样式配置，可以修改默认样式

```xml

    <!--输入框外边框宽度-->
    <dimen name="pwk_input_view_border_width">0.7pt</dimen>
    <!--背景缩进，必须与pwk_input_view_border_width保持一致，并且为负值-->
    <dimen name="pwk_input_view_border_width_inset">-0.7pt</dimen>
    <!--输入框分割线的宽度，在混合组件中使用-->
    <dimen name="pwk_input_view_divider_split_line">@dimen/pwk_input_view_border_width</dimen>
    <!--输入框分割空间的距离，在分离组件样式中使用-->
    <dimen name="pwk_input_view_divider_split_space">5dp</dimen>
    <!--输入框选中状态的边框宽度，在混合组件中使用-->
    <dimen name="pwk_input_item_highlight_border_width">1.2pt</dimen>
    <!--输入框圆角-->
    <dimen name="pwk_input_item_radius">4dp</dimen>
    <!--输入框字体大小-->
    <dimen name="pwk_input_item_text_size">24sp</dimen>

```

## 设置键盘类型

键盘组件支持三种键盘类型：

- `KeyboardType.FULL` 全键盘模式，包括民用、警察、军队、使馆等车牌类型；
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

## 混淆规则 - Proguard

在项目的`proguard-rules.pro`中添加以下混淆规则：

```groguard

# rhino (javascript engine)
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.classfile.**
-keep class org.mozilla.classfile.** { *; }
-keep class org.mozilla.javascript.* { *; }
-keep class org.mozilla.javascript.annotations.** { *; }
-keep class org.mozilla.javascript.ast.** { *; }
-keep class org.mozilla.javascript.commonjs.module.** { *; }
-keep class org.mozilla.javascript.commonjs.module.provider.** { *; }
-keep class org.mozilla.javascript.debug.** { *; }
-keep class org.mozilla.javascript.jdk13.** { *; }
-keep class org.mozilla.javascript.jdk15.** { *; }
-keep class org.mozilla.javascript.json.** { *; }
-keep class org.mozilla.javascript.optimizer.** { *; }
-keep class org.mozilla.javascript.regexp.** { *; }
-keep class org.mozilla.javascript.serialize.** { *; }
-keep class org.mozilla.javascript.typedarrays.** { *; }
-keep class org.mozilla.javascript.v8dtoa.** { *; }
-keep class org.mozilla.javascript.xml.** { *; }
-keep class org.mozilla.javascript.xmlimpl.** { *; }

```
## 文档

KeyboardInputController提供一系列有用的方法，详细参见DOC文档对应的类方法说明：

[DOC文档](https://parkingwang.github.io/vehicle-keyboard-android/)

## RoadMap

- 使用Java原生代码来替代JavaScript脚本实现核心引擎逻辑；
- 增加二级键盘提示，应对特定场景下显示超出键盘布局容量的按键；

## 版本更新

### v0.4.0 2018.0424

> 说明：在此版本中，如果使用`com.parkingwang.vehiclekeyboard.support.KeyboardInputController`包的相关类，
> 会报告Deprecated,使用`com.parkingwang.vehiclekeyboard.KeyboardInputController`包的类即可。

- 更新KeyboardInputController及相关类的包结构；
- 增加Controller各个Set和Add接口的NullPointer检查；

### v0.3-ALPHA 2017.1120

- 修复Texts关于字符判断的问题；
- 更新Gradle版本为3.3.0；
- 更新JS文件(JS压缩)；
- 全键盘模式下，增加“民航”车牌类型；
- 修改KeyboardView每行键位数的基准为第一行；

### v0.2-ALPHA 2017.1113

- 修改键盘组件主题颜色的控制逻辑，原有的`pwk_keyboard_primary_color`修改为`pwk_primary_color`，并作为整个键盘的主题色。
- 为输入框不同键位的增加控制主题。`PWKInputItemStyleKey / PWKInputItemStyleLeft / PWKInputItemStyleRight`。

### v0.1-BETA 2017.1108

- 完成基础功能，更新文档并公开发布；

## Contributors

[VehicleKeyboard-Android](https://github.com/parkingwang/vehicle-keyboard-android)
由西安艾润物联网技术服务有限公司第一事业部深圳研发中心开发和维护，旨在为停车行业同行进行技术交流和分享。

目前在两位开发者在开发维护，如您有相关问题，可以通过PullRequest和Issues来提交，也可以通过邮件与我们联系。

- [陈永佳](https://github.com/yoojia) 联系方式：chenyongjia@parkingwang.com
- [黄浩杭](https://github.com/msdx) 联系方式：huanghaohang@parkingwang.com

Android 中集成  SDK
============
 
## 快速体验
导入ean-pay-an-sdk整个项目，做如下修改，即可运行该demo。 

修改ClientSDKActivity类中参数YOUR_URL为自己支付服务器端charge生成接口

例如：YOUR_URL = "http://192.168.1.10710:8088/payment/api_payment";

<font color="red">需要注意: </font>确保服务器端获取charge接口畅通。

## 开始接入
### 步骤1：添加相应的依赖到项目中
#### 使用gradle依赖，将xpay_sdk-release.aar文件复制到libs目录，增加build.gradle内容。

``` groovy
dependencies {
    compile(name: 'xpay_sdk-release', ext: 'aar')
    compile fileTree(include: ['*.jar'], dir: 'libs')
}
```

### 步骤2：在清单文件中声明所需权限
<font color='red'>(注：有些权限是需要动态注册的,如"READ_PHONE_STATE"权限)</font>

``` xml
    <!-- 通用权限 -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
    
### 步骤3：在清单文件中注册相应的组件
-  SDK所需要注册

``` xml
        <activity
                android:name="com.xpay.demoapp.MainActivity"
                android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <activity android:name="com.xpay.demoapp.ClientSDKActivity">
        </activity>
```

### 步骤4：获取到charge后，调起支付
#### 获取charge
charge 对象是一个包含支付信息的 JSON 对象，是  SDK 发起支付的必要参数。该参数需要请求用户服务器获得，服务端生成 charge 的方式参考 服务端接入简介。SDK 中的 demo 里面提供了如何获取 charge 的实例方法，供用户参考。

#### 调起支付
因为  已经封装好了相应的调用方法，所以只需要调用支付方法即可调起支付控件：
(<font color='red'>注：该调用方法需要在主线程(UI线程)完成</font>)

- 调起支付方式：

``` java_holder_method_tree
//参数一：Activity  当前调起支付的Activity
//参数二：data  获取到的charge或order的JSON字符串
XPay.createPayment(YourActivity.this, data);
```

### 步骤5：获取支付结果
<font color='red'>注意：</font>此种支付方式不能使用异步通知获取支付接结果。
后台异步程序会将支付结果异步通知到商户服务端，由商户服务端自行推送到移动端。

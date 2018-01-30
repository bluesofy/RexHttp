# RexHttp

[ ![Download](https://api.bintray.com/packages/blueyuki/maven/RexHttp/images/download.svg) ](https://bintray.com/blueyuki/maven/RexHttp/_latestVersion)

基于Retrofit/RxJava/OkHttp实现的Http封装库，
在RxEasyHttp的基础上简化了缓存功能，
统一处理Retrofit的Api访问入口，重新定义数据返回形式，修改了部分方法及参数配置，使其更贴近实际业务的使用

## 使用说明  Usage
* **添加依赖**
```gradle
api 'cn.byk.pandora:rexhttp:1.1.1'
```

* **请自行添加第三方库，便于自定义版本，避免库多版本加载**
```gradle
api 'com.alibaba:fastjson:1.1.63.android'

api 'com.squareup.okhttp3:okhttp:3.9.1'
api 'com.squareup.okio:okio:1.13.0'

api 'io.reactivex.rxjava2:rxjava:2.1.7'
api 'io.reactivex.rxjava2:rxandroid:2.0.1'

api 'com.squareup.retrofit2:retrofit:2.3.0'
api 'com.squareup.retrofit2:adapter-rxjava2:2.3.0'
```

* **具体调用方法有空再补充，目前本库自用**

## 关于混淆  ProGuard
* **只需添加OkHttp的混淆**
```
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
```

## 特别鸣谢  Tks to
* [RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)

## 联系方式  Support or Contact
* E-Mail: bluesofy@qq.com
* E-Mail: bluesofy@live.cn
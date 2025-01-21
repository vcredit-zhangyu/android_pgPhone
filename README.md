
# 掌御APP

### 项目简介
本项目为全Kotlin语言开发的一款用于管理测试手机的安卓app

### 环境准备
+ 开发工具：jdk、AndroidStudio
+ 运行环境：jdk17及以上、Flamingo及以上
+ 编译版本：sdk33

### 项目运行
#### 使用
修改ApiManager文件BaseApiEnvironment类的getTestEnvironment和getProduceEnvironment方法返回值，
分别修改对应的测试环境和正式环境的域名。

#### 扩展-添加上报的应用
1、DataCenter.getPackageList方法添加包名和对应code；
2、主module下的AndroidManifest文件中queries标签中添加对应包名的package标签；
例：`<package android:name="com.example.xxx" />`

#### 扩展-兼容性
+ 目前支持安卓版本：安卓5.0～安卓13
+ 已测试通过华为、小米、vivo、oppo、荣耀多个品牌的手机设备

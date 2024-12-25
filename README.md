
# 掌御APP

## 使用
修改ApiManager文件BaseApiEnvironment类的getTestEnvironment和getProduceEnvironment方法返回值，
分别修改对应的测试环境和正式环境的域名。

### 扩展-添加上报的应用
1、DataCenter.getPackageList方法添加包名和对应code；
2、主module下的AndroidManifest文件中queries标签中添加对应包名的package标签；
例：`<package android:name="com.example.xxx" />`

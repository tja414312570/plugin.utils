# plugin.utils
	* 提供ant路径匹配功能
	* 提供路径扫描功能
	* 提供字节转数值类型功能
	* 提供字符变量功能
	* 提供基础的xml解析功能
	* 提供扩展的类加载器功能
	* 提供反射信息缓存工鞥呢
	* 提供包扫描功能
	* 提供文件工具，支持追加写入等
	* 提供资源路劲上下文功能
# 20191023
* 新增抽象资源实体(AbstractResourceEntry)，用于包裹可能的File或JarEntry
* 新增资源扫描(ResourceScanner)，可以同时扫描路劲或则jar包
* 将包扫描底层的扫描工具改为资源扫描
# 20191108
* 新增通过传入Class对象获取Class的ClassPath的方法
```java
ResourceManager.getClassPath(Logger.class)
```

package com.yanan.utils.reflect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.yanan.utils.UnsafeUtils;
import com.yanan.utils.reflect.cache.ClassHelper;
import com.yanan.utils.reflect.cache.ClassInfoCache;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.PathMatcher;
/**
 * ClassLoader for YaNan.frame 该类加载器是YaNan应用的核心处理机制之一，用于对应用内部实体、类等的控制
 * 新增独占模式:独占模式，完全为一个行的加载器，所有类只要能用此加载器加载的将强制以此加载器加载
 * ！！启用独占模式后加载的类会和调用此加载器环境不一致，导致相同的类名有不同的hash，两个容器之间将不能传值
 * @author Administrator
 * @version 1.0.1
 * @author YaNan
 *
 */
public class AppClassLoader extends ClassLoader{
	private static final Consumer<Throwable> IGNORE_EXCEPTION_FUNCTION = new Consumer<Throwable>() {
		@Override
		public void accept(Throwable t) {
			
		}
	};
	//操作的对象
	private Object loadObject;
	//操作的类
	private Class<?> loadClass;
	//类信息缓存
	private ClassHelper classHelper = null;
	//独占模式
	private boolean exclusive = false;
	//共享类
	private volatile Set<String> sharedClassMap;
	/**
	 * get the class class helper info
	 * @return class helper
	 */
	public ClassHelper getClassHelper() {
		return classHelper;
	}
	/**
	 * 添加共享类
	 * @param name 共享类名
	 */
	public void addShardClass(String name) {
		if(this.sharedClassMap == null) {
			synchronized (this) {
				if(sharedClassMap == null)
					sharedClassMap = new HashSet<>();
			}
		}
		this.sharedClassMap.add(name);
	}
	/**
	 * 判断共享类是否存在
	 * @param name 名称
	 * @return 是否有
	 */
	public boolean hasShardClass(String name) {
		return this.sharedClassMap != null &&this.sharedClassMap.contains(name);
	}
	/**
	 * 获取共享类映射
	 * @return 共享类集合
	 */
	public Set<String> getSharedClassMap(){
		return this.sharedClassMap;
	}
	/**
	 * enable exclusive
	 */
	public void enableExclusive() {
		this.exclusive = true;
	}
	/**
	 * whether the loader is exclusive
	 * @return boolean
	 */
	public boolean isExclusive() {
		return exclusive;
	}
	/**
	 * get the class loader loaded object
	 * @return a object at this class loader
	 */
	public Object getLoadObject() {
		return loadObject;
	}
	/**
	 * set current class loader object
	 * @param loadObject set the class loader loading object
	 */
	public void setLoadObject(Object loadObject) {
		this.loadObject = loadObject;
	}

	/**
	 * 默认构造器，不传入任何参数，要使用请先调用loadClass或loadObjcet
	 */
	public AppClassLoader() {
	};

	/**
	 * 默认构造器，传入一个Object型的对象，此构造器将会加载Object和Object的Class
	 * 
	 * @param object the loader object
	 */
	public AppClassLoader(Object object) {
		this.loadObject = object;
		this.loadClass = this.loadObject.getClass();
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
	}

	/**
	 * 默认构造器，传入一个Object型的对象和boolean型是否创建类的实例 ， 此构造器将会加载Class
	 * 
	 * @param cls the load target class
	 */
	public AppClassLoader(Class<?> cls) {
		this.loadClass = cls;
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
		try {
			this.loadObject = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 默认构造器，传入一个Object型的对象和boolean型是否创建类的实例
	 * ，如果boolean为此构造器将会加载Object和Object的Class
	 * 
	 * @param cls the load target class
	 * @param instance whether create instance when loaded the class
	 */
	public AppClassLoader(Class<?> cls, boolean instance) {
		this.loadClass = cls;
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
		try {
			if (instance)
				this.loadObject = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 默认构造器，传入一个String型的完整类名，如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className String:the load target class name
	 * @throws InstantiationException ex
	 * @throws IllegalAccessException ex
	 * @throws ClassNotFoundException ex
	 */
	public AppClassLoader(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.loadClass = Class.forName(className);
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
		this.loadObject = this.loadClass.newInstance();
	}

	/**
	 * 默认构造器，传入一个String型的完整类名和一个boolean型是否创建实例， 如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className String:the class name for load
	 * @param instance whether create instance when loaded the class
	 * @throws InstantiationException ex
	 * @throws IllegalAccessException ex
	 * @throws ClassNotFoundException ex
	 */
	public AppClassLoader(String className, boolean instance)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.loadClass = Class.forName(className);
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
		if (instance)
			this.loadObject = this.loadClass.newInstance();
	}

	/**
	 * 默认构造器，传入一个String型的完整类名，如果该类的默认构造器无需传入参数
	 * ，那么第二个参数为空，否则，传入要加载的类的默认构造器所需要的参数，如果 类实例化失败后，可以直接试用instance(Object...
	 * args)重新创建实例
	 * 
	 * @param className String the name for class
	 * @param args create instance arguments
	 * @throws InstantiationException ex
	 * @throws IllegalAccessException ex 
	 * @throws ClassNotFoundException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public AppClassLoader(String className, Object... args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
		this.classHelper = ClassInfoCache.getClassHelper(this.loadClass);
		if (args.length == 0) {
			this.loadObject = this.loadClass.newInstance();
		} else {
			this.loadObject = this.Instance(args);
		}

	}

	/**
	 * 创建一个类的实例，此方法在类被加载到该加载器后才能试用
	 * 
	 * @param Method String:invoke static method name
	 * @param args invoke args
	 * @return the loader
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public Object Instance(String Method, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.loadObject = this.invokeMethod(Method, args);
		return this.loadObject;
	}

	/**
	 * 创建类的实例，需要先加载类到该加载器，所需类的默认构造器的参数
	 * 
	 * @param args the constructor arguments
	 * @return the instance for loaded class
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex 
	 * @throws InstantiationException ex
	 */
	public Object Instance(Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		Constructor<?> cls = this.classHelper.getConstructor(ParameterUtils.getParameterTypes(args));
		this.loadObject = cls.newInstance(args);
		return this.loadObject;
	}

	/**
	 * 加载对象到该加载器，传入完整String完整类名，如果该类实例化需要 参数，请依次传入参数，否则第二个参数为空
	 * 
	 * @param className loader class name
	 * @param args the method arguments
	 * @throws ClassNotFoundException ex
	 * @throws InstantiationException ex
	 * @throws IllegalAccessException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public void loadObject(String className, Object... args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		this.loadClass = Class.forName(className);
		if (args.length == 0) {
			this.loadObject = this.loadClass.newInstance();
		} else {
			this.loadObject = this.Instance(args);
		}
	}

	/**
	 * 加载对象到加载器，同时加载该对象的类，传入对象的实例
	 * 
	 * @param object load object
	 */
	public void loadObject(Object object) {
		this.loadObject = object;
		this.loadClass = this.loadObject.getClass();
	}

	/**
	 * 获得加载器内加载的类
	 * 
	 * @return the Loaded class
	 */
	public Class<?> getLoadedClass() {
		return this.loadClass;
	}

	/**
	 * 获得加载器内的加载的实例
	 * 
	 * @return  the loaded object
	 */
	public Object getLoadedObject() {
		return this.loadObject;
	}

	/*
	 * Field
	 */
	/**
	 * 获得加载器内加载类的所有公开的属性
	 * 
	 * @return the filed of loaded class
	 */
	public Field[] getFields() {
		return this.classHelper.getFields();
	}

	/**
	 * 获得加载器内加载类的所有属性
	 * 
	 * @return the declared field of loaded class
	 */
	public Field[] getDeclaredFields() {
		return this.classHelper.getDeclaredFields();
	}

	/**
	 * 判断加载器内加载的类中是否有某个公开的属性,传入属性名
	 * 
	 * @param field the target field
	 * @return boolean whether exists
	 */
	public boolean hasField(String field) {
		return this.classHelper.getField(field) != null;
	}

	/**
	 * 判断加载器内加载的类中是否有某个属性，传入属性名
	 * 
	 * @param field field the target field
	 * @return boolean whether exists
	 */
	public boolean hasDeclaredField(String field) {
		return this.classHelper.getDeclaredField(field) != null;
	}

	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param fieldName String field name
	 * @return the value of field with loaded class
	 * @throws IllegalAccessException ex
	 */
	public Object getFieldValue(String fieldName) throws IllegalAccessException {
		Field field = this.classHelper.getField(fieldName);
		if(field == null) 
			field = this.classHelper.getDeclaredField(fieldName);
		return getFieldValue(field);
	}

	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param field String field name
	 * @return the value of field with loaded class
	 * @throws IllegalArgumentException ex
	 * @throws IllegalAccessException ex
	 */
	public Object getFieldValue(Field field) throws IllegalArgumentException, IllegalAccessException {
		return ReflectUtils.getFieldValue(field, loadObject);
	}
	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param fieldName String field name
	 * @return the value of field with loaded class
	 * @throws NoSuchFieldException ex
	 * @throws SecurityException ex
	 * @throws IllegalArgumentException ex
	 * @throws IllegalAccessException ex
	 */
	public Object getDeclaredFieldValue(String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = this.classHelper.getDeclaredField(fieldName);
		return getFieldValue(field);
	}

	/**
	 * 设置加载器内加载的对象的属性值，传入String型属性名，任意类型的值
	 * 
	 * @param field the target field
	 * @param value the target value
	 * @throws IllegalAccessException ex
	 */
	public void setFieldValue(String field, Object value) throws IllegalAccessException {
		Field f = this.classHelper.getDeclaredField(field);
		setFieldValue(f, value);
	}
	/**
	 * 设置加载器内加载的对象的属性值，传入String型属性名，任意类型的值
	 * @param field the target field
	 * @param value the target value
	 * @throws NoSuchFieldException ex
	 * @throws SecurityException ex
	 * @throws IllegalArgumentException ex
	 * @throws IllegalAccessException ex
	 */
	public void setDeclaredFieldValue(String field, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = this.classHelper.getDeclaredField(field);
		setFieldValue(f, value);
	}

	/**
	 * 设置加载器内加载的对象的属性值，传入Field型属性名，任意类型的值
	 * 
	 * @param field the target field
	 * @param value the target value
	 * @throws IllegalAccessException ex
	 */
	public void setFieldValue(Field field, Object value) throws IllegalAccessException {
		ReflectUtils.setFieldValue(field, loadObject, value);
	}

	/*
	 * field set or get,only used by servletDispatcher
	 */
	/**
	 * 该方法用于直接设置某个属性的值，传入field name 和 value 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param fieldName field: field name
	 * @param arg value
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public void set(String fieldName, Object arg) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Field field = this.getDeclaredField(fieldName);
		this.set(field, arg);
	}

	/**
	 * 该方法用于直接设置某个属性的值，传入Field 和 value 优先通过Set方式获取，没有则通过直接获取
	 * 
	 * @param field field: field name
	 * @param arg value
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public void set(Field field, Object arg) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String method = ReflectUtils.createFieldSetMethod(field);
		if (this.hasMethod(method, field.getType())) {
			Class<?>[] parameter = new Class<?>[1];
			parameter[0] = field.getType();
			invokeMethod(method, parameter, ParameterUtils.castType(arg, field.getType()));
		} else {
			setFieldValue(field, ParameterUtils.castType(arg, field.getType()));
		}
	}

	/**
	 * 该方法用于直接设置某个属性的值，传入String属性，String 值
	 * 
	 * @param field field: field name
	 * @param parameterType parameter type
	 * @param arg value
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public void set(String field, Class<?> parameterType, Object arg) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] parameter = new Class<?>[1];
		parameter[0] = parameterType;
		invokeMethod(ReflectUtils.createFieldSetMethod(field), parameter, arg);
	}

	/**
	 * 该方法用于直接获取某个属性的值，传入field name 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param field the target filed
	 * @return the value of field with current object
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public Object get(String field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String method = ClassInfoCache.getFieldGetMethod(field);
		if (this.hasMethod(method)) {
			return invokeMethod(method);
		} else {
			return this.getFieldValue(field);
		}
	}

	/**
	 * 该方法用于直接获取某个属性的值，传入Field 优先通过Get方式获取，没有则通过直接获取
	 * 
	 * @param field target field
	 * @return field value
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex 
	 */
	public Object get(Field field) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		String method = ClassInfoCache.getFieldGetMethod(field);
		if (this.hasMethod(method)) {
			return invokeMethod(method);
		} else {
			return this.getFieldValue(field);
		}
	}

	/*
	 * invoke method
	 */
	/**
	 * 调用加载器内加载对象的某个方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName invoke method name
	 * @param args invoke arguments
	 * @return the result  
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public Object invokeMethod(String methodName, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invokeMethod(methodName, ParameterUtils.getParameterBaseType(args), args);
	}

	/**
	 * 调用加载器内加载对象的某个方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName method name
	 * @param parameterType parameter type array
	 * @param args invoke arguments
	 * @return invoke result
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public Object invokeMethod(String methodName, Class<?>[] parameterType, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return invokeMethod(this.loadObject, methodName, parameterType, args);
	}
	/**
	 * 调用加载器内加载对象的某个方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param object target object
	 * @param methodName method name
	 * @param parameterType parameter type array
	 * @param args invoke arguments
	 * @return invoke result
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public Object invokeMethod(Object object, String methodName, Class<?>[] parameterType, Object... args)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = this.classHelper.getMethod(methodName, parameterType);
		if(method==null) {
			method = this.classHelper.getDeclaredMethod(methodName, parameterType);
		}
		if (method == null) {
			StringBuilder ptStr = new StringBuilder(this.loadClass.getName()).append(".").append(methodName)
					.append("(");
			for (int i = 0; i < parameterType.length; i++) {
				ptStr.append(parameterType[i].getName());
				if (i < parameterType.length - 1)
					ptStr.append(".");
			}
			throw new NoSuchMethodException(ptStr.append(")").toString());
		}
		return ReflectUtils.invokeMethod(object, method, args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param methodName invoke method name
	 * @param args invoke parameters
	 * @return invoke result
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public Object invokeStaticMethod(String methodName, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invokeMethod(null, methodName,  ParameterUtils.getParameterTypes(args), args);
	}

	/**
	 * 获得加载器内加载类的某个方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method target method
	 * @param parameterType method parameter type array
	 * @return target method
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public Method getDeclaredMethod(String method, Class<?>... parameterType)
			throws NoSuchMethodException, SecurityException {
		return this.classHelper.getDeclaredMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的某个公开的方法，传入String方法名，参数类型（可选）
	 * 
     * @param method target method
	 * @param parameterType method parameter type array
	 * @return target method
	 */
	public Method getMethod(String method, Class<?>... parameterType){
		return this.classHelper.getMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的所有公开的方法
	 * 
	 * @return all method array
	 */
	public Method[] getMethods() {
		return this.classHelper.getMethods();
	}

	/**
	 * 获得加载器内加载类的所有方法
	 * 
	 * @return declared method array
	 */
	public Method[] getDeclaredMethods() {
		return this.classHelper.getDeclaredMethods();
	}

	// judge method
	/**
	 * 判断加载器内加载的类是否有某个方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method target method
	 * @param parameterType method parameter type
	 * @return whether exists target method
	 */
	public boolean hasDeclaredMethod(String method, Class<?>... parameterType) {
		return this.classHelper.getDeclaredMethod(method, parameterType) != null;
	}

	/**
	 * 判断加载器内加载的类是否有某个公开的方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method target method
	 * @param parameterType method parameter type
	 * @return whether exists target method
	 */
	public boolean hasMethod(String method, Class<?>... parameterType) {
		return this.classHelper.getMethod(method, parameterType) != null;
	}
	/**
	 * 获取类的属性
	 * @param field  filed name
	 * @return the filed 
	 */
	public Field getDeclaredField(String field) {
		return this.classHelper.getDeclaredField(field);
	}
	/**
	 * clone object
	 * @param target to object
	 * @param source from object
	 * @return whether success
	 */
	public static boolean clone(Object target, Object source) {
		if (target.getClass() == source.getClass()) {
			DisClone(target, source);
			return true;
		}
		return false;
	}
	/**
	 * 跨对象复制对象，复制规则为相同的属性
	 * @param target to object
	 * @param source from object
	 */
	public static void DisClone(Object target, Object source){
		DisClone(target, source, IGNORE_EXCEPTION_FUNCTION);
	}
	/**
	 * 跨对象复制对象，复制规则为相同的属性
	 * @param target to object
	 * @param source from object
	 * @param consumer exception consumer
	 */
	public static void DisClone(Object target, Object source,Consumer<Throwable> consumer){
		ClassHelper helper = ClassHelper.getClassHelper(source.getClass());
		Field[] fields = helper.getAllFields();
			for (Field f : fields) {
				try {
					Field sField = helper.getAnyField(f.getName());
					if(sField == null)
						continue;
					Object value = ReflectUtils.getFieldValue(sField, source);
					ReflectUtils.setFieldValue(f, target,  value);
				} catch (Throwable e) {
					consumer.accept(e);
				}
			}
	}
	/**
	 * 深度克隆
	 * @param target target object
	 * @param source source object
	 */
	public static void deepClone(Object target,Object source) {
		Field[] fields = ClassHelper.getClassHelper(source.getClass()).getAllFields();
		Class<?> sCls = source.getClass();
			for (Field f : fields) {
				try {
					Field sField =ClassHelper.getClassHelper(sCls).getAnyField(f.getName());
					Object targetFieldObj = ReflectUtils.getFieldValue(sField, source);
					if(targetFieldObj == null)
						continue;
					if(f.getType().isArray()) {
						int len = Array.getLength(targetFieldObj);
						targetFieldObj = Array.newInstance(f.getType(),len );
						for (int i = 0; i < len; i++) {
							Object element = Array.get(targetFieldObj, i);
							if(element!= null && !ParameterUtils.isBaseUnwrapperType(element.getClass())&& !Modifier.isFinal(element.getClass().getModifiers())){
								deepClone(element.getClass(), element);
							}
							Array.set(targetFieldObj, i,element);
						}
					}else {
						if(!ParameterUtils.isBaseUnwrapperType(f.getType()) && f.getAnnotation(ShallowClone.class) == null
								&& !Modifier.isFinal(f.getType().getModifiers())) {
							 targetFieldObj = deepClone(targetFieldObj.getClass(), targetFieldObj);
						}
					}
					
					ReflectUtils.setFieldValue(f, target,  targetFieldObj);
				} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException e) {
					e.printStackTrace();
				}
			}
	}
	@SuppressWarnings({ "restriction", "unchecked" })
	public static <T> T deepClone(Class<T> targetClass,Object source) throws InstantiationException {
		if(source == null)
			return null;
		T object;
		try {
			object = targetClass.newInstance();
		}catch (Throwable e) {
			object = (T) UnsafeUtils.getUnsafe().allocateInstance(targetClass);
		}
		deepClone(object, source);
		return object;
	}
	
	/**
	 * 复制一个对象到目标类型
	 * 
	 * @param <T> generic type
	 * @param target to object
	 * @param source from object
	 * @return a new object from clone
	 */
	public static <T> T clone(Class<T> target, Object source) {
		try {
			T object = target.newInstance();
			Field[] fields = target.getDeclaredFields();
			for (Field f : fields) {
				try {
					Field fs = source.getClass().getDeclaredField(f.getName());
					Object value = ReflectUtils.getFieldValue(fs, source);
					ReflectUtils.setFieldValue(f, object, value);
				} catch (NoSuchFieldException | SecurityException e) {
					try {
						Method fgm = source.getClass().getMethod(ClassInfoCache.getFieldGetMethod(f.getName()));
						if (fgm != null)
							f.set(object, fgm.invoke(source));
					} catch (NoSuchMethodException | SecurityException | InvocationTargetException e1) {
						continue;
					}
				}
			}
			return object;
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 加载类的二进制字节码
	 * @param clzzName 将要加载的名字
	 * @param bytes 类的字节码数组
	 * @return class
	 */
	public Class<?> loadClass(String clzzName, byte[] bytes) {
		this.loadClass = defineClass(clzzName, bytes, 0,bytes.length);
		return this.loadClass;
	}
	public boolean matchSharedMap(String name) {
		if(this.sharedClassMap != null)
		for(String c :this.sharedClassMap) {
			if(PathMatcher.match(c, name).isMatch())
				return true;
		}
		return false;
	}
	//用于查找类
	protected Class<?> loadClass(String name, boolean resolve)
	        throws ClassNotFoundException
	    {
	        synchronized (getClassLoadingLock(name)) {
	            // First, check if the class has already been loaded
	            Class<?> c = findLoadedClass(name);
	            if (c == null) {
	            	//是否独占模式
	            	if(this.exclusive && !matchSharedMap(name)) {
//	            		if((ClassLoaderSharedClass == null || ClassLoaderSharedInheritedClass == null)
//	            				&& (!name.equals(ClassLoaderShared.class.getName()) && !name.equals(ClassLoaderSharedInherited.class.getName()))
//	            				 && !name.startsWith("java")) {
//	            			ClassLoaderSharedClass = (Class<? extends Annotation>) loadClass(ClassLoaderShared.class.getName(), resolve);
//	            			ClassLoaderSharedInheritedClass = (Class<? extends Annotation>) loadClass(ClassLoaderSharedInherited.class.getName(), resolve);
//	            		}
	            		//获取二进制资源
	            		ClassLoader parentLoader =  this.getParent();
	            		int len = -1;
	            		InputStream is = null;
	            		ByteArrayOutputStream baos = null;
	            		try {
	            			is = parentLoader.getResourceAsStream(name.replace(".", "/")+".class");
	            			len = is.available();
	            			baos = new ByteArrayOutputStream();
	            			while((len = is.read() )!= -1) {
	            				baos.write(len);
	            			}
	            			c = AppClassLoader.loadClass(name, baos.toByteArray(), this);
//	            			if(c.getAnnotation(ClassLoaderSharedClass) != null 
//	            					|| c.getAnnotation(ClassLoaderSharedClass) != null) {
//	            				c = super.loadClass(name, resolve);
//	            			}
	            		} catch (Throwable  e) {
	            			if(e.getLocalizedMessage()!= null && !e.getLocalizedMessage().contains("java")) {
		            			e.printStackTrace();
	            			}
	            			c = super.loadClass(name, resolve);
	            		}finally {
							if(is != null)
								try {
									is.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							if(baos != null) {
								try {
									baos.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
	            	}else {
	            		c = super.loadClass(name, resolve);
	            	}
	            }
	            return c;
	        }
	    }
	/**
	 * 通过某加载器加载某类
	 * @param clzzName class name
	 * @param bytes class bytes
	 * @param ncLoader context loader
	 * @return class object
	 */
	public static Class<?> loadClass(String clzzName, byte[] bytes, java.lang.ClassLoader ncLoader) {
		try {
			Class<?> loaderClass = ncLoader.getClass();
			Method method = ClassHelper.getClassHelper(loaderClass).getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			while(method == null && (loaderClass = loaderClass.getSuperclass()) != null) {
				method = ClassHelper.getClassHelper(loaderClass).getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			}
			if(method == null)
				return null;
			Class<?> resultClass = (Class<?>) ReflectUtils.invokeMethod(ncLoader, method, clzzName,bytes,0,bytes.length);
			return resultClass;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("failed to load class ["+clzzName+"] by "+ncLoader,e);
		}
	}
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//获取当前线程的加载器
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//从线程加载加载资源
        return loader.loadClass(name);
    }
	/**
	 * 获取当前线程上下文的类路径资源
	 * @return
	 */
	public static String getCurrentContextClassPath() {
		return getClassLoaderClassPath(Thread.currentThread().getContextClassLoader());
	}
	/**
	 * 获取类加载器的类路径
	 * @param classLoader 资源加载器
	 * @return 资源加载器的类路径
	 */
	public static String getClassLoaderClassPath(ClassLoader classLoader) {
		return ResourceManager.processPath(classLoader.getResource(".").getPath());
	}
	/**
	 * 获取类所在的类路径
	 * @param targetClass 类资源
	 * @return 类路径
	 */
	public static String getClassClasspath(Class<?> targetClass) {
		String classPackage = targetClass.getPackage().getName().replace(".", "/");
		String resourcePath = ResourceManager.processPath(targetClass.getResource(".").getPath())
				.replace("file:", "");
		return resourcePath.substring(0,resourcePath.lastIndexOf(classPackage));
	}
}
package com.YaNan.frame.utils.reflect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.YaNan.frame.utils.PathMatcher;
import com.YaNan.frame.utils.reflect.cache.ClassHelper;
import com.YaNan.frame.utils.reflect.cache.ClassInfoCache;


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
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Object loadObject;
	private Class<?> loadClass;
	private ClassHelper infoCache = null;
	private boolean exclusive = false;
	private volatile Set<String> sharedClassMap;
	private volatile Class<? extends Annotation> ClassLoaderSharedClass = null;
	private volatile Class<? extends Annotation> ClassLoaderSharedInheritedClass = null;
	/**
	 * get the class class helper info
	 * @return class helper
	 */
	public ClassHelper getInfoCache() {
		return infoCache;
	}
	public void addShardClass(String name) {
		if(this.sharedClassMap == null) {
			synchronized (this) {
				if(sharedClassMap == null)
					sharedClassMap = new HashSet<>();
			}
		}
		this.sharedClassMap.add(name);
	}
	public boolean hasShardClass(String name) {
		return this.sharedClassMap != null &&this.sharedClassMap.contains(name);
	}
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
	 * 判断类是否存在，参数 完整类名
	 * 
	 * @param className find the class name
	 * @return boolean:is exists
	 */
	public static boolean exists(String className) {
		try {
			ClassInfoCache.classForName(className);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获得类下所有公开的属性 传入 完整类名
	 * 
	 * @param className String:class name,the target class name
	 * @return all public fields
	 */
	public static Field[] getFields(String className) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得类下所有属性 参数 完整类名
	 * 
	 * @param className String:class name,the target class name
	 * @return all declared filed at the current class
	 */
	public static Field[] getAllFields(String className) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getDeclaredFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断公开的方法是否存在，参数 完整类名，方法名，参数类型（可选）
	 * 
	 * @param className String:class name
	 * @param methodName String:method name
	 * @param args LClass arguments type array
	 * @return boolean:is exists
	 */
	public static boolean hasMethod(String className, String methodName, Class<?>... args) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getMethod(methodName,
					args) != null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断方法是否存在，参数 完整类名，方法名，参数类型（可选）
	 * 
	 * @param className String:class name
	 * @param methodName String:method name
	 * @param args LClass arguments type array
	 * @return boolean:is exists
	 */
	public static boolean hasDeclaredMethod(String className, String methodName, Class<?>... args) {
		try {
			return ClassInfoCache.getClassHelper(ClassInfoCache.classForName(className)).getMethod(methodName,
					args) != null;
		} catch (SecurityException | ClassNotFoundException e) {
			return false;
		}
	}
	/**
	 * get the base type base on argument's values
	 * @param args argument's
	 * @return base type array
	 */
	public static Class<?>[] getParameterTypes(Object... args) {
		Class<?>[] parmType = new Class[args.length];
		for (int i = 0; i < args.length; i++)
			parmType[i] = args[i]==null?null:args[i].getClass();
		return parmType;
	}
	/**
	 * get the base type base on argument's values
	 * @param args argument's
	 * @return base type array
	 */
	public static Class<?>[] getParameterBaseType(Object... args) {
		Class<?>[] parmType = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			parmType[i] = patchBaseType(args[i]);
		return parmType;
	}

	/**
	 * 获取方法返回值类型
	 * 
	 * @param method the method
	 * @return the method return type
	 */
	public static Class<?> getMethodReturnType(Method method) {
		return method.getReturnType();
	}

	/**
	 * 创建属性的get方法
	 * 
	 * @param field String:filed name
	 * @return the method name for get filed
	 */
	public static String createFieldGetMethod(String field) {
		return ClassInfoCache.getFieldGetMethod(field);
	}
	/**
	 * 创建属性get方法
	 * @param field Field:field
	 * @return the method name for get field
	 */
	public static String createFieldGetMethod(Field field) {
		return ClassInfoCache.getFieldGetMethod(field.getName());
	}

	/**
	 * 创建属性的set方法
	 * 
	 * @param field String:field name
	 * @return the method name for set field
	 */
	public static String createFieldSetMethod(String field) {
		return ClassInfoCache.getFieldSetMethod(field);
	}
	/**
	 * 创建属性set方法
	 * @param field Field:field
	 * @return the method name for set field
	 */
	public static String createFieldSetMethod(Field field) {
		return ClassInfoCache.getFieldSetMethod(field.getName());
	}

	/**
	 * 创建属性的add方法
	 * 
	 * @param name String:the field name
	 * @return the field add method name
	 */
	public static String createFieldAddMethod(String name) {
		return ClassInfoCache.getFieldAddMethod(name);
	}

	/**
	 * 获取类的公开方法，传入String类名，String方法名，参数类型数组（可选）
	 * 
	 * @param cls Class the target class
	 * @param methodName String:the method name
	 * @param parameterTypes LClass the parameter types array
	 * @return target method
	 * @throws NoSuchMethodException ex
	 */
	public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return ClassInfoCache.getClassHelper(cls).getMethod(methodName, parameterTypes);
	}

	/**
	 * 获取类的方法，传入String类名，String方法名，参数类型数组（可选）
	 * 
	 * @param cls Class the target class
	 * @param methodName String:the method name
	 * @param parameterTypes LClass the parameter types array
	 * @return target method
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 */
	public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return ClassInfoCache.getClassHelper(cls).getDeclaredMethod(methodName, parameterTypes);
	}

	/*
	 * static method end
	 */

	/*
	 * constructs start
	 */
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
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
	}

	/**
	 * 默认构造器，传入一个Object型的对象和boolean型是否创建类的实例 ， 此构造器将会加载Class
	 * 
	 * @param cls the load target class
	 */
	public AppClassLoader(Class<?> cls) {
		this.loadClass = cls;
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
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
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
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
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
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
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
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
		this.infoCache = ClassInfoCache.getClassHelper(this.loadClass);
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
		Constructor<?> cls = this.infoCache.getConstructor(getParameterTypes(args));
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
		return this.infoCache.getFields();
	}

	/**
	 * 获得加载器内加载类的所有属性
	 * 
	 * @return the declared field of loaded class
	 */
	public Field[] getDeclaredFields() {
		return this.infoCache.getDeclaredFields();
	}

	/**
	 * 判断加载器内加载的类中是否有某个公开的属性,传入属性名
	 * 
	 * @param field the target field
	 * @return boolean whether exists
	 */
	public boolean hasField(String field) {
		return this.infoCache.getField(field) != null;
	}

	/**
	 * 判断加载器内加载的类中是否有某个属性，传入属性名
	 * 
	 * @param field field the target field
	 * @return boolean whether exists
	 */
	public boolean hasDeclaredField(String field) {
		return this.infoCache.getDeclaredField(field) != null;
	}

	/**
	 * 获取加载器内加载的对象的属性值，传入String型属性名
	 * 
	 * @param fieldName String field name
	 * @return the value of field with loaded class
	 * @throws IllegalAccessException ex
	 */
	public Object getFieldValue(String fieldName) throws IllegalAccessException {
		Field field = this.infoCache.getField(fieldName);
		if(field == null) 
			field = this.infoCache.getDeclaredField(fieldName);
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
		field.setAccessible(true);
		Object result = field.get(this.loadObject);
		return result;
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
		Field field = this.infoCache.getDeclaredField(fieldName);
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
		Field f = this.infoCache.getDeclaredField(field);
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
		Field f = this.infoCache.getDeclaredField(field);
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
		field.setAccessible(true);
		field.set(this.loadObject, value);
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
		String method = AppClassLoader.createFieldSetMethod(field);
		if (this.hasMethod(method, field.getType())) {
			Class<?>[] parameter = new Class<?>[1];
			parameter[0] = field.getType();
			invokeMethod(method, parameter, castType(arg, field.getType()));
		} else {
			setFieldValue(field, castType(arg, field.getType()));
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
		invokeMethod(createFieldSetMethod(field), parameter, arg);
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
		String method = AppClassLoader.createFieldGetMethod(field);
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
		String method = AppClassLoader.createFieldGetMethod(field);
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
		return invokeMethod(methodName, getParameterBaseType(args), args);
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
		Method method = this.infoCache.getMethod(methodName, parameterType);
		if(method==null) {
			method = this.infoCache.getDeclaredMethod(methodName, parameterType);
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
		method.setAccessible(true);
		return method.invoke(object, args);
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
		return invokeMethod(null, methodName, getParameterTypes(args), args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param clzz invoke class
	 * @param methodName invoke method name
	 * @param args invoke parameter
	 * @return invoke result
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public static Object invokeStaticMethod(Class<?> clzz, String methodName, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = ClassInfoCache.getClassHelper(clzz).getMethod(methodName, getParameterBaseType(args));
		if(method == null)
			throw new NoSuchMethodException();
		return method.invoke(null, args);
	}

	/**
	 * 调用加载器内加载对象的某个静态方法，传入String方法名，参数所需要的参数（可选）
	 * 
	 * @param clzz invoke class
	 * @param methodName invoke method name
	 * @param parameterTypes the invoke method parameter type
	 * @param args invoke arguments
	 * @return invoke result
	 * @throws NoSuchMethodException ex
	 * @throws SecurityException ex
	 * @throws IllegalAccessException ex
	 * @throws IllegalArgumentException ex
	 * @throws InvocationTargetException ex
	 */
	public static Object invokeStaticMethod(Class<?> clzz, String methodName, Class<?>[] parameterTypes, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = ClassInfoCache.getClassHelper(clzz).getMethod(methodName, parameterTypes);
		if(method == null)
			throw new NoSuchMethodException();
		return method.invoke(null, args);
	}

	/*
	 * Method
	 */
	// get method
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
		return this.infoCache.getDeclaredMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的某个公开的方法，传入String方法名，参数类型（可选）
	 * 
     * @param method target method
	 * @param parameterType method parameter type array
	 * @return target method
	 */
	public Method getMethod(String method, Class<?>... parameterType){
		return this.infoCache.getMethod(method, parameterType);
	}

	/**
	 * 获得加载器内加载类的所有公开的方法
	 * 
	 * @return all method array
	 */
	public Method[] getMethods() {
		return this.infoCache.getMethods();
	}

	/**
	 * 获得加载器内加载类的所有方法
	 * 
	 * @return declared method array
	 */
	public Method[] getDeclaredMethods() {
		return this.infoCache.getDeclaredMethods();
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
		return this.infoCache.getDeclaredMethod(method, parameterType) != null;
	}

	/**
	 * 判断加载器内加载的类是否有某个公开的方法，传入String方法名，参数类型（可选）
	 * 
	 * @param method target method
	 * @param parameterType method parameter type
	 * @return whether exists target method
	 */
	public boolean hasMethod(String method, Class<?>... parameterType) {
		return this.infoCache.getMethod(method, parameterType) != null;
	}
	/**
	 * 获取类的属性
	 * @param field  filed name
	 * @return the filed 
	 */
	public Field getDeclaredField(String field) {
		return this.infoCache.getDeclaredField(field);
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
	public static void DisClone(Object target, Object source) {
		Field[] fields = target.getClass().getDeclaredFields();
		Class<?> sCls = source.getClass();
		try {
			for (Field f : fields) {
				Field sField = null;
				try {
					sField = sCls.getDeclaredField(f.getName());
				} catch (NoSuchFieldException e1) {
					continue;
				}
				sField.setAccessible(true);
				f.setAccessible(true);
				f.set(target, sField.get(source));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
				f.setAccessible(true);
				Field fs;
				try {
					fs = source.getClass().getDeclaredField(f.getName());
					fs.setAccessible(true);
					f.set(object, fs.get(source));
				} catch (NoSuchFieldException | SecurityException e) {
					try {
						Method fgm = source.getClass().getMethod(createFieldGetMethod(f.getName()));
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
	 * 判断一个类是否继承自某个接口，不支持包含父类继承的接口的继承 eg. class A implements B{}
	 * implementOf(A.class,B.class) ==》true class A implements B{},class C
	 * extends A{}; implementOf(C.class,B.class) ==》false
	 * 
	 * @param orginClass 要判断的类
	 * @param interfaceClass 要验证实现的接口
	 * @return whether true
	 */
	public static boolean implementOf(Class<?> orginClass, Class<?> interfaceClass) {
		Class<?>[] cls = orginClass.getInterfaces();
		for (Class<?> cCls : cls) {
			if (cCls.equals(interfaceClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断一个类是否继承自某个接口，支持包含父类继承的接口的继承 class A implements B{}
	 * implementOf(A.class,B.class) ==》true class A implements B{},class C
	 * extends A{}; implementOf(C.class,B.class) ==》true
	 * 
	 * @param orginClass 要判断的类
	 * @param interfaceClass 要验证实现的接口
	 * @return whether true
	 */
	public static boolean implementsOf(Class<?> orginClass, Class<?> interfaceClass) {
		Class<?> tempClass = orginClass;
		while (tempClass != null && !tempClass.equals(Object.class)) {
			if (tempClass.equals(interfaceClass)) {
				return true;
			}
			Class<?>[] interfaces = tempClass.getInterfaces();
			for (Class<?> inter : interfaces) {
				if (interfaceClass.equals(inter)) {
					return true;
				}
			}
			tempClass = tempClass.getSuperclass();
			if(tempClass == null) {
				break;
			}
		}
		return false;
	}
	/**
	 * 判断一个类是否是另一个类的子类
	 * @param orginClass child class
	 * @param parentClass parent class
	 * @return boolean
	 */
	public static boolean extendOf(Class<?> orginClass, Class<?> parentClass) {
		return orginClass.getSuperclass().equals(parentClass);
	}
	/**
	 * 判断一个类是否继承了另一个类,采用多级查找机制，两个类可能是父子，孙爷关系
	 * 
	 * @param orginClass child class
	 * @param parentClass parent class
	 * @return boolean
	 */
	public static boolean extendsOf(Class<?> orginClass, Class<?> parentClass) {
		Class<?> tempClass = orginClass;
		while (!tempClass.equals(Object.class)) {
			if (tempClass.equals(parentClass)) {
				return true;
			}
			tempClass = tempClass.getSuperclass();
			if(tempClass == null) {
				break;
			}
		}
		return false;
	}
	/**
	 * 将包装类型转化基础类型
	 * 
	 * @param patchType 包装类型
	 * @return 原始类型
	 */
	public static Class<?> patchBaseType(Object patchType) {
		// 无类型
		if (patchType.getClass().equals(Void.class)) {
			return void.class;
		}
		// 整形
		if (patchType.getClass().equals(Integer.class)) {
			return int.class;
		}
		if (patchType.getClass().equals(Short.class)) {
			return short.class;
		}
		if (patchType.getClass().equals(Long.class)) {
			return long.class;
		}
		// 浮点
		if (patchType.getClass().equals(Double.class)) {
			return double.class;
		}
		if (patchType.getClass().equals(Float.class)) {
			return float.class;
		}
		// 字节
		if (patchType.getClass().equals(Byte.class)) {
			return byte.class;
		}
		if (patchType.getClass().equals(Character.class)) {
			return char.class;
		}
		// 布尔
		if (patchType.getClass().equals(Boolean.class)) {
			return boolean.class;
		}
		return patchType.getClass();
	}
	/**
	 * 将一个类型转化为目标类型
	 * @param orgin orgin
	 * @param targetType target type
	 * @return cast type
	 */
	public static Object castType(Object orgin, Class<?> targetType) {
		if(orgin != null
			&& (implementsOf(orgin.getClass(), targetType)||extendsOf(orgin.getClass(), targetType))) {
			return orgin;
		}
		// 整形
		if (targetType.equals(int.class)) {
			return orgin == null?0:(int)(Integer.parseInt((orgin.toString()).equals("") ? "0" : orgin.toString()));
		}
		if (targetType.equals(short.class)) {
			return orgin == null?0:Short.parseShort((String) orgin);
		}
		if (targetType.equals(long.class)) {
			return orgin == null?0:Long.parseLong(orgin.toString());
		}
		if (targetType.equals(byte.class)) {
			return orgin == null?0:Byte.parseByte(orgin.toString());
		}
		// 浮点
		if (targetType.equals(float.class)) {
			return orgin == null?0:Float.parseFloat(orgin.toString());
		}
		if (targetType.equals(double.class)) {
			return orgin == null?false:Double.parseDouble(orgin.toString());
		}
		// 日期
		if (targetType.equals(Date.class)) {
			try {
				if(orgin == null)
					return null;
				if(extendsOf(orgin.getClass(), Date.class)) {
					return orgin;
				}
				return SimpleDateFormat.getInstance().parse(orgin.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// 布尔型
		if (targetType.equals(boolean.class)) {
			return orgin == null?false:Boolean.parseBoolean((String) orgin);
		}
		// char
		if (targetType.equals(char.class)) {
			return orgin == null?0:(char) orgin;
		}
		if (targetType.equals(String.class)) {
			return orgin == null?null:orgin.toString();
		}
		// 没有匹配到返回源数据
		return orgin;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz array class 
	 * @param arg arg array
	 * @param format format
	 * @return cast result
	 * @throws ParseException ex
	 */
	public static Object parseBaseTypeArray(Class<?> clzz, String[] arg, String format) throws ParseException {
		if (!clzz.isArray()) {
			return parseBaseType(clzz, arg[0], format);
		}
		if (clzz.equals(String[].class)) {
			return arg;
		}
		Object[] args = new Object[arg.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = parseBaseType(clzz, arg[i], format);
		}
		return args;
	}
	/**
	 * 判断类是否为可支持的基本类型
	 * 
	 * @param clzz judge class
	 * @return whether 
	 */
	public static boolean isBaseType(Class<?> clzz) {
		if (clzz.equals(String.class))
			return true;
		if (clzz.equals(boolean.class))
			return true;
		if (clzz.equals(int.class))
			return true;
		if (clzz.equals(float.class))
			return true;
		if (clzz.equals(byte.class))
			return true;
		if (clzz.equals(short.class))
			return true;
		if (clzz.equals(long.class))
			return true;
		if (clzz.equals(double.class))
			return true;
		if (clzz.equals(char.class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Byte.class))
			return true;
		if (clzz.equals(Short.class))
			return true;
		if (clzz.equals(Integer.class))
			return true;
		if (clzz.equals(Long.class))
			return true;
		if (clzz.equals(Float.class))
			return true;
		if (clzz.equals(Double.class))
			return true;
		if (clzz.equals(Boolean.class))
			return true;
		if (clzz.equals(Character.class))
			return true;
		// 日期
		if (extendsOf(clzz,Date.class))
			return true;

		// 以上所有类型的数组类型
		if (clzz.equals(String[].class))
			return true;
		if (clzz.equals(boolean[].class))
			return true;
		if (clzz.equals(int[].class))
			return true;
		if (clzz.equals(float[].class))
			return true;
		if (clzz.equals(byte[].class))
			return true;
		if (clzz.equals(short[].class))
			return true;
		if (clzz.equals(long[].class))
			return true;
		if (clzz.equals(double[].class))
			return true;
		if (clzz.equals(char[].class))
			return true;
		// 八个基本数据类型的包装类型
		if (clzz.equals(Short[].class))
			return true;
		if (clzz.equals(Integer[].class))
			return true;
		if (clzz.equals(Long[].class))
			return true;
		if (clzz.equals(Float[].class))
			return true;
		if (clzz.equals(Double[].class))
			return true;
		if (clzz.equals(Boolean[].class))
			return true;
		if (clzz.equals(Character[].class))
			return true;
		// 日期
		if (extendsOf(clzz,Date[].class))
			return true;
		return false;
	}

	/**
	 * 将字符类型转换为目标类型
	 * 
	 * @param clzz target type
	 * @param arg argument string
	 * @param format the date format
	 * @return parse result
	 */
	public static Object parseBaseType(Class<?> clzz, String arg, String format) {
		// 匹配时应该考虑优先级 比如常用的String int boolean应该放在前面 其实 包装类型应该分开
		if (clzz.equals(String.class))
			return arg;
		// 8个基本数据类型及其包装类型
		if (clzz.equals(int.class))
			return arg == null ? 0 : Integer.parseInt(arg);
		if (clzz.equals(Integer.class))
			return arg == null ? null : Integer.valueOf(arg);

		if (clzz.equals(boolean.class))
			return arg == null ? false : Boolean.parseBoolean(arg);
		if (clzz.equals(Boolean.class))
			return arg == null ? null : Boolean.valueOf(arg);

		if (clzz.equals(float.class))
			return arg == null ? 0.0f : Float.parseFloat(arg);
		if (clzz.equals(Float.class))
			return arg == null ? null : Float.valueOf(arg);

		if (clzz.equals(short.class))
			return arg == null ? 0 : Short.parseShort(arg);
		if (clzz.equals(Short.class))
			return arg == null ? null : Short.valueOf(arg);

		if (clzz.equals(long.class))
			return arg == null ? 0l : Long.parseLong(arg);
		if (clzz.equals(Long.class))
			return arg == null ? null : Long.valueOf(arg);

		if (clzz.equals(double.class))
			return arg == null ? 0.0f : Double.parseDouble(arg);
		if (clzz.equals(Double.class))
			return arg == null ? null : Double.valueOf(arg);

		if (clzz.equals(char.class))
			return arg == null ? null : arg.charAt(0);
		if (clzz.equals(Character.class))
			return arg == null ? null : Character.valueOf(arg.charAt(0));

		if (clzz.equals(char[].class))
			return arg == null ? null : arg.toCharArray();

		if (clzz.equals(byte.class) || clzz.equals(Byte.class))
			return arg == null ? null : Byte.parseByte(arg);
		// 日期
		if (clzz.equals(Date.class))
			try {
				return format == null ? DATE_FORMAT.parse(arg) : new SimpleDateFormat(format).parse(arg);
			} catch (ParseException e) {
				new RuntimeException(e);
			}
		return arg;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object proxyInstance) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object result = field.get(proxyInstance);
		field.setAccessible(false);
		return (T) result;
	}
	/**
	 * 传入参数类型和目标参数类型匹配，主要用于判断方法的匹配
	 * 支持子类型判断，比如[string,int]--[object,int]匹配
	 * @param matchType match type
	 * @param parameterTypes parameter type
	 * @return boolean
	 */
	public static  boolean matchType(Class<?>[] matchType, Class<?>[] parameterTypes) {
		if(parameterTypes.length!=matchType.length)
			return false;
		for(int i = 0;i<parameterTypes.length;i++){
			if(parameterTypes[i]==null&&!isNotNullType(matchType[i]))
				continue;
			if(parameterTypes[i].equals(matchType[i]))
				continue;
			if(AppClassLoader.extendsOf(parameterTypes[i], matchType[i]))
				continue;
			if(AppClassLoader.implementsOf(parameterTypes[i], matchType[i]))
				continue;
			return false;
		}
		return true;
	}
	/**
	 * 类型传入类型是否为非空类型，主要用于某些值在初始化的时候不能为null
	 * @param type target type
	 * @return boolean
	 */
	public static boolean isNotNullType(Class<?> type) {
		return type.equals(int.class)||
			   type.equals(long.class)||
			   type.equals(float.class)||
			   type.equals(double.class)||
			   type.equals(short.class)||
			   type.equals(boolean.class)?true:false;
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
	 * 获取field为List的泛型
	 * @param field the field
	 * @return the generic type
	 */
	public static Class<?> getListGenericType(Field field) {
		Type genericType = field.getGenericType(); 
		if(genericType != null && genericType instanceof ParameterizedType){   
			ParameterizedType pt = (ParameterizedType) genericType;
			//得到泛型里的class类型对象  
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
			return genericClazz;
		}   
		return null;
	}
	/**
	 * 获取Parameter为List的泛型
	 * @param parm the parameter
	 * @return generic type of the parameter
	 */
	public static Class<?> getListGenericType(Parameter parm) {
		Type genericType = parm.getParameterizedType(); 
		if(genericType != null && genericType instanceof ParameterizedType){   
			ParameterizedType pt = (ParameterizedType) genericType;
			//得到泛型里的class类型对象  
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
			return genericClazz;
		}   
		return null;
	}
	/**
	 * 获取数组的类型
	 * @param arrayClass array class
	 * @return the type of array
	 */
	public static Class<?> getArrayType(Class<?> arrayClass){
		if(arrayClass.isArray()){
			return arrayClass.getComponentType();
		}
		return null;
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
			method.setAccessible(true);
			Class<?> resultClass = (Class<?>) method.invoke(ncLoader, clzzName,bytes,0,bytes.length);
			method.setAccessible(false);
			return resultClass;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("failed to load class ["+clzzName+"] by "+ncLoader,e);
		}
	}
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//获取当前线程的加载器
		java.lang.ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//从线程加载加载资源
        return loader.loadClass(name);
    }
}
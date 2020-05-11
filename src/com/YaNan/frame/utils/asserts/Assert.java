package com.YaNan.frame.utils.asserts;

/**
 * 
 * assert support
 * @author yanan
 * @since plugin 2.0
 *
 */
public class Assert {
	
	public static void isNull(Object object) {
		isNull(object, new NullPointerException());
	}
	public static void isNull(Object object,String message) {
		isNull(object,  new NullPointerException(message));
	}
	public static void isNull(Object object,RuntimeException throwable){
		if(object == null) {
			throw throwable;
		}
	}
	public static void isNull(Object object, Function function) {
		if(object == null && function != null) {
			function.execute();
		}
	}
	
	public static void isNotNull(Object object, String message) {
		isNotNull(object,new IsNotNullException(message));
	}
	public static void isNotNull(Object object) {
		isNotNull(object,new IsNotNullException());
	}
	public static void isNotNull(Object object,RuntimeException throwable){
		if(object != null) {
			throw throwable;
		}
	}
	
}

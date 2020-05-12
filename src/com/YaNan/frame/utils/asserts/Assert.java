package com.YaNan.frame.utils.asserts;

/**
 * 
 * assert support
 * @author yanan
 * @since plugin 2.0
 *
 */
public class Assert {
	
	public static void isNull(final Object object) {
		isNull(object, new NullPointerException());
	}
	public static void isNull(final Object object,String message) {
		isNull(object,  new NullPointerException(message));
	}
	public static void isNull(final Object object,RuntimeException throwable){
		isTrue(object == null,throwable);
	}
	public static void isNull(final Object object, Function function) {
		isTrue(object == null,function);
	}
	
	public static void isNotNull(final Object object, String message) {
		isNotNull(object,new AssertNotNullException(message));
	}
	public static void isNotNull(final Object object) {
		isNotNull(object,new AssertNotNullException());
	}
	public static void isNotNull(final Object object,RuntimeException throwable){
		isTrue(object != null,throwable);
	}
	
	public static void isTrue(final boolean bol) {
		isTrue(bol,new AssertTrueException());
	}
	public static void isTrue(final boolean bol, String message) {
		isTrue(bol,new AssertTrueException(message));
	}
	public static void isTrue(final boolean bol,RuntimeException throwable) {
		if(bol) {
			throw throwable;
		}
	}
	public static void isTrue(final boolean bol, Function function) {
		if(bol && function != null) {
			function.execute();
		}
	}
	public static void isFalse(final boolean bol) {
		isFalse(bol,new AssertTrueException());
	}
	public static void isFalse(final boolean bol, String message) {
		isFalse(bol,new AssertTrueException(message));
	}
	public static void isFalse(final boolean bol,RuntimeException throwable) {
		isTrue(!bol,throwable);
	}
	
}

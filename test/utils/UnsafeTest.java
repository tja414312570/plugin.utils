package utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import com.YaNan.frame.utils.UnsafeUtils;

import sun.misc.Unsafe;

public class UnsafeTest {
	public static void main(String[] args) {
		System.out.println(UnsafeUtils.getUnsafe());
	}
}

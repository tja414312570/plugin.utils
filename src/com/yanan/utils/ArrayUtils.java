package com.yanan.utils;

import java.lang.reflect.Array;

import com.yanan.utils.reflect.AppClassLoader;

/**
 * 数组工具
 * 
 * @author yanan
 */
public class ArrayUtils {
	/**
	 * 从数据中查找元素
	 * 
	 * @param arrays 需要查找的数组
	 * @param target 需要查找的数据
	 * @return 位置
	 */
	public static int indexOf(Object[] arrays, Object target) {
		for (int index = 0; index < arrays.length; index++) {
			Object ele = arrays[index];
			if ((ele == null && target == null) || (ele != null && target != null && ele.equals(target)))
				return index;
		}
		return -1;
	}

	/**
	 * 从整形数组中查找数据
	 * 
	 * @param arrays 数组
	 * @param target 目标数据
	 * @return 目标数据位置
	 */
	public static int indexOf(int[] arrays, int target) {
		for (int index = 0; index < arrays.length; index++) {
			if (arrays[index] == target)
				return index;
		}
		return -1;
	}

	/**
	 * 向数组中添加元素，如果数组不存在，则创建新数组
	 * 
	 * @param arrays   数组
	 * @param elements 添加的元素
	 * @return 新的数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] arrays, T... elements) {
		if (elements.length == 0)
			return null;
		if (arrays == null)
			return elements;
		T[] newArrays = (T[]) Array.newInstance(AppClassLoader.getArrayType(arrays.getClass()),
				arrays.length + elements.length);
		System.arraycopy(arrays, 0, newArrays, 0, arrays.length);
		System.arraycopy(elements, 0, newArrays, arrays.length, elements.length);
		return newArrays;
	}

	/**
	 * 向数组中添加元素，如果数组不存在，则创建新数组
	 * 
	 * @param arrays 数组
	 * @param element 元素
	 * @param index 元素位置
	 * @return 插入后的数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] arrays, T element, int index) {
		if (arrays == null && index == 0) {
			arrays = (T[]) Array.newInstance(element.getClass(), 1);
			arrays[0] = element;
			return arrays;
		}
		if (index > arrays.length)
			throw new IndexOutOfBoundsException();
		T[] newArrays = (T[]) Array.newInstance(AppClassLoader.getArrayType(arrays.getClass()), arrays.length + 1);
		//判断位置 处于边界位置 可以减少复制数组的次数
		if(index == 0)
			System.arraycopy(arrays, 0, newArrays, 1, arrays.length);
		else if(index == arrays.length)
			System.arraycopy(arrays, 0, newArrays, 0, arrays.length);
		else {
			System.arraycopy(arrays, 0, newArrays, 0, index);
			System.arraycopy(arrays,index , newArrays, index+1, arrays.length-index);
		}
		newArrays[index] = element;
		return newArrays;
	}
}

package com.yanan.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * 集合工具
 * @author yanan
 *
 */
public class CollectionUtils {
	/**
	 * 判断集合是否为空
	 * @param collection 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
	/**
	 * 判断集合是否不为空
	 * @param collection 集合
	 * @return 是否不为空
	 */
	public static boolean isNotEmpty(final Collection<?> collection) {
		return !isEmpty(collection);
	}
	/**
	 * 判断集合是否为空
	 * @param map 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}
	/**
	 * 判断集合是否不为空
	 * @param map 集合
	 * @return 是否不为空
	 */
	public static boolean isNotEmpty(final Map<?, ?> map) {
		return !isEmpty(map);
	}
	/**
	 * 获取集合的长度
	 * @param collection 集合
	 * @return 集合大小
	 */
	public static int getSize(final Collection<?> collection) {
		return collection == null ? 0 :collection.size();
	}
	/**
	 * 获取集合的长度
	 * @param collection 集合
	 * @return 集合大小
	 */
	public static int getSize(final Map<?, ?> map) {
		return map == null ? 0: map.size();
	}
	/**
	 * 判断集合是否有重复元素
	 * @param collection 集合
	 * @return boolean
	 */
	public static boolean hasReparet(final Collection<?> collection) {
		if(collection == null)
			throw new NullPointerException();
		return new HashSet<>(collection).size() == collection.size();
	}
}

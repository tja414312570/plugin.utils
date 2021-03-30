package com.yanan.utils;

/**
 * 位处理工具
 * 
 * @author yanan
 *
 */
public class ByteUtils {
	/**
	 * int整数转换为4字节的byte数组
	 * 
	 * @param i 整数
	 * @return byte数组
	 */
	public static byte[] intToBytes(int i) {
		byte[] num = new byte[4];
		intToBytes(i,num);
		return num;
	}
	public static void intToBytes(int i,byte[] bytes) {
		intToBytes(i,bytes,0);
	}
	
	public static void intToBytes(int i,byte[] bytes,int pos) {
		bytes[3+pos] = (byte) (i & 0xFF);
		bytes[2+pos] = (byte) (i >> 8 & 0xFF);
		bytes[1+pos] = (byte) (i >> 16 & 0xFF);
		bytes[0+pos] = (byte) (i >> 24 & 0xFF);
	}
	/**
	 * long整数转换为8字节的byte数组
	 * 
	 * @param lo long整数
	 * @return byte数组
	 */
	public static byte[] longToBytes(long lo) {
		byte[] num = new byte[8];
		longToBytes(lo, num);
		return num;
	}

	public static void longToBytes(long lo, byte[] bytes) {
		longToBytes(lo, bytes, 0);
	}

	public static void longToBytes(long lo, byte[] bytes, int pos) {
		int len = bytes.length;
		if (len > 8)
			len = 8;
		for (int i = 0; i < len; i++) {
			int offset = (7 - i) << 3;
			bytes[i+pos] = (byte) ((lo >>> offset) & 0xFF);
		}
	}

	/**
	 * short整数转换为2字节的byte数组
	 * 
	 * @param s short整数
	 * @return byte数组
	 */
	public static byte[] unsignedShortToBytes(int s) {
		byte[] num = new byte[2];
		unsignedShortToBytes(s,num);
		return num;
	}
	public static void unsignedShortToBytes(int s,byte[] bytes) {
		bytes[0] = (byte) (s >> 8 & 0xFF);
		bytes[1] = (byte) (s & 0xFF);
	}

	/**
	 * byte数组转换为无符号short整数
	 * 
	 * @param bytes byte数组
	 * @return short整数
	 */
	public static int bytesToUnsignedShort(byte[] bytes) {
		return bytesToUnsignedShort(bytes, 0);
	}

	/**
	 * byte数组转换为无符号short整数
	 * 
	 * @param bytes byte数组
	 * @param off   开始位置
	 * @return short整数
	 */
	public static int bytesToUnsignedShort(byte[] bytes, int off) {
		int high = bytes[off];
		int low = bytes[off + 1];
		return (high << 8 & 0xFF00) | (low & 0xFF);
	}

	/**
	 * byte数组转换为int整数
	 * 
	 * @param bytes byte数组
	 * @param off   开始位置
	 * @return int整数
	 */
	public static int bytesToInt(byte[] bytes, int off) {
		int b0 = bytes[off] & 0xFF;
		int b1 = bytes[off + 1] & 0xFF;
		int b2 = bytes[off + 2] & 0xFF;
		int b3 = bytes[off + 3] & 0xFF;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
	}

	/**
	 * byte数组转换为int整数
	 * 
	 * @param bytes byte数组 开始位置
	 * @return int整数
	 */
	public static int bytesToInt(byte[] bytes) {
		return bytesToInt(bytes, 0);
	}

	/**
	 * byte数组转换为long
	 * 
	 * @param bytes byte数组
	 * @return long整数
	 */
	public static long bytesToLong(byte[] bytes) {
		return bytesToLong(bytes, 0);
	}

	/**
	 * byte数组转换为long
	 * 
	 * @param bytes byte数组
	 * @return long整数
	 */
	public static long bytesToLong(byte[] bytes, int offset) {
		long num = 0;
		int len = offset + 8;
		for (int i = offset; i < len; ++i) {
			num <<= 8;
			num |= (bytes[i] & 0xff);
		}
		return num;
	}
}

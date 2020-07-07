package com.YaNan.frame.utils;

import java.nio.ByteBuffer;

/**
 * 位处理工具
 * @author yanan
 *
 */
public class ByteUtils {
	 /** 
	   * int整数转换为4字节的byte数组 
	   * 
	   * @param i 
	   *      整数 
	   * @return byte数组 
	   */
	  public static byte[] intToByte(int i) { 
	    byte[] num = new byte[4]; 
	    num[3] = (byte) (i & 0xFF); 
	    num[2] = (byte) (i >> 8 & 0xFF); 
	    num[1] = (byte) (i >> 16 & 0xFF); 
	    num[0] = (byte) (i >> 24 & 0xFF); 
	    return num; 
	  } 
	  
	  /** 
	   * long整数转换为8字节的byte数组 
	   * 
	   * @param lo 
	   *      long整数 
	   * @return byte数组 
	   */
	  public static byte[] longToByte(long lo) { 
	    byte[] num = new byte[8]; 
	    for (int i = 0; i < 8; i++) { 
	      int offset = (num.length - 1 - i) * 8; 
	      num[i] = (byte) ((lo >>> offset) & 0xFF); 
	    } 
	    return num; 
	  } 
	  
	  /** 
	   * short整数转换为2字节的byte数组 
	   * 
	   * @param s 
	   *      short整数 
	   * @return byte数组 
	   */
	  public static byte[] unsignedShortToByte(int s) { 
	    byte[] num = new byte[2]; 
	    num[0] = (byte) (s >> 8 & 0xFF); 
	    num[1] = (byte) (s & 0xFF); 
	    return num; 
	  } 
	  
	  /** 
	   * byte数组转换为无符号short整数 
	   * 
	   * @param bytes 
	   *      byte数组 
	   * @return short整数 
	   */
	  public static int byteToUnsignedShort(byte[] bytes) { 
	    return byteToUnsignedShort(bytes, 0); 
	  } 
	  
	  /** 
	   * byte数组转换为无符号short整数 
	   * 
	   * @param bytes 
	   *      byte数组 
	   * @param off 
	   *      开始位置 
	   * @return short整数 
	   */
	  public static int byteToUnsignedShort(byte[] bytes, int off) { 
	    int high = bytes[off]; 
	    int low = bytes[off + 1]; 
	    return (high << 8 & 0xFF00) | (low & 0xFF); 
	  } 
	  /** 
	   * byte数组转换为int整数 
	   * 
	   * @param bytes 
	   *      byte数组 
	   * @param off 
	   *      开始位置 
	   * @return int整数 
	   */
	  public static int byteToInt(byte[] bytes, int off) { 
	    int b0 = bytes[off] & 0xFF; 
	    int b1 = bytes[off + 1] & 0xFF; 
	    int b2 = bytes[off + 2] & 0xFF; 
	    int b3 = bytes[off + 3] & 0xFF; 
	    return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3; 
	  } 
	  /** 
	   * byte数组转换为int整数 
	   * 
	   * @param bytes 
	   *      byte数组 
	   *      开始位置 
	   * @return int整数 
	   */
	  public static int byteToInt(byte[] bytes) { 
	    return byteToInt(bytes,0); 
	  } 
	  /** 
	   * byte数组转换为long 
	   * 
	   * @param bytes 
	   *      byte数组 
	   * @return long整数 
	   */
	  public static long bytesToLong(byte[] bytes) { 
		  ByteBuffer buffer = ByteBuffer.allocate(8); 
		  buffer.put(bytes, 0, bytes.length); 
		  return buffer.getLong(); 
	  }
}

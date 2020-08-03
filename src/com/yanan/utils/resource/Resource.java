package com.yanan.utils.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 资源类，用于提供系统抽象资源 定义一套统一资源api
 * 
 * @author yanan
 *
 */
public interface Resource {
	/**
	 * 获取资源的路径
	 * 
	 * @return the path
	 */
	String getPath();

	/**
	 * 判断资源是否是一个目录
	 * 
	 * @return is direct
	 */
	boolean isDirect();

	/**
	 * 获取资源的最后的修改时间
	 * 
	 * @return the last modify time
	 */
	long lastModified();

	/**
	 * 获取资源可用数据大小
	 * 
	 * @return the resource available stream length
	 * @throws IOException ex
	 */
	long size() throws IOException;

	/**
	 * 如果资源还有下级资源，则此方法列出所有下级资源
	 * 
	 * @return return the resource list
	 */
	List<? extends Resource> listResource();

	/**
	 * 获取资源的输出流
	 * 
	 * @return os
	 * @throws IOException ex
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * 获取资源输入流
	 * 
	 * @return input stream
	 * @throws IOException ex
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * 获取资源名称
	 * 
	 * @return resource
	 */
	String getName();
}

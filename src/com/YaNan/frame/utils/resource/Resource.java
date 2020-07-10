package com.YaNan.frame.utils.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 资源类，用于提供系统抽象资源
 * 定义一套统一资源api
 * @author yanan
 *
 */
public interface Resource {
	/**
	 * 获取资源的路径
	 * @return the path
	 */
	String getPath();
	/**
	 * resource is direct
	 * @return is direct
	 */
	boolean isDirect();
	/**
	 * get the resource last modify time
	 * @return the last modify time
	 */
	long lastModified();
	/**
	 * get resource available data size
	 * @return the resource available stream length
	 */
	long size();
	/**
	 * if the resource is direct ,use the method get all list
	 * @return return the resource list 
	 */
	List<? extends Resource> listResource();
	/**
	 * get the resource out stream 
	 * @return os
	 * @throws FileNotFoundException ex
	 */
	public OutputStream getOutputStream() throws IOException;
	/**
	 * get the resource input stream
	 * @return input stream
	 */
	public InputStream getInputStream() throws IOException;
	/**
	 * get the resource name
	 * @return resource
	 */
	 String getName();
}

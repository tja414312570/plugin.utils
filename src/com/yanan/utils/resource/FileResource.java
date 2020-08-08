package com.yanan.utils.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.yanan.utils.string.StringUtil;

/**
 * 文件系统资源，普通的file资源
 * 用于将文件封装成抽象资源
 * 
 * @author yanan
 *
 */
public class FileResource implements Resource {
	// 如果为扫描产生，此值标记扫描路径
	private String origin;
	// 如果是文件类型
	private File file;

	/**
	 * 将文件封装为抽象资源
	 * @param origin 扫描路径
	 * @param file 文件
	 */
	public FileResource(String origin, File file) {
		super();
		this.origin = origin;
		this.file = file;
	}

	/**
	 * 将file封装成抽象资源类型
	 * 
	 * @param file 要被封装的文件
	 */
	public FileResource(File file) {
		super();
		this.origin = file.getAbsolutePath();
		this.file = file;
	}

	/**
	 * 获取文件的绝对路径
	 * 
	 * @return the path
	 */
	public String getPath() {
		return ResourceManager.processPath(file.getAbsolutePath());
	}

	/**
	 * 判断此文件是否是一个目录
	 * 
	 * @return is direct
	 */
	public boolean isDirect() {
		return this.file.isDirectory();
	}

	/**
	 * 获取文件的最后修改时间
	 * 
	 * @return the last modify time
	 */
	public long lastModified() {
		return this.file.lastModified();
	}

	/**
	 * 获取资源的长度
	 * 
	 * @return the resource available stream length
	 */
	public long size() {
		return this.file.length();
	}

	/**
	 * 如果此资源为文件夹资源，则此方法获取此文件夹下的所有文件，返回做为资源
	 * 
	 * @return return the resource list
	 */
	public List<Resource> listResource() {
		if (!isDirect())
			throw new UnsupportedOperationException("this resource is a file");
		String pathExpress = getPath();
		String last = pathExpress.substring(pathExpress.length() - 1);
		if (StringUtil.equals(last, "/") || StringUtil.equals(last, "\\")) {
			pathExpress += "*";
		} else {
			pathExpress += "/*";
		}
		return ResourceManager.getResourceList(pathExpress);
	}

	/**
	 * 获取文件的输出流
	 * 
	 * @return os
	 * @throws FileNotFoundException ex
	 */
	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(this.file);
	}

	/**
	 * 获取文件的输入流
	 * 
	 * @return input stream
	 * @throws FileNotFoundException ex
	 */
	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(this.file);
	}

	/**
	 * 获取文件的名字
	 * 
	 * @return name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * 获取文件的原始路径或扫描路径
	 * <p>如果此资源通过扫描获取，则此值为文件的扫描路径
	 * <p>否则为文件的绝对路劲
	 * 
	 * @return resource origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * 获取文件
	 * @return file
	 */
	public File getFile() {
		return file;
	}
}

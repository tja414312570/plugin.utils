package com.yanan.utils.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.jar.JarEntry;

import com.yanan.utils.string.StringUtil;

/**
 * 类路径支援，支持类路径的资源以及jar内的类路径资源
 * 
 * @author yanan
 */
public class ClassPathResource implements Resource {
	// 资源绝对路劲
	private String path;
	// 资源名称
	private String name;
	// 如果为扫描产生，此值标记扫描路径
	private String origin;
	// 如果是jar资源
	private JarEntry jarEntry;
	// jar资源需要带上资源加载器
	private ClassLoader resourceLoader;
	/**
	 * 类路径资源，用于将类资源封装为抽象资源使用
	 * @param path 路径
	 * @param name 资源名
	 * @param origin 资源扫描路径
	 * @param jarEntry jar实体
	 * @param resourceLoader 资源加载器
	 */
	public ClassPathResource(String path, String name, String origin, JarEntry jarEntry, ClassLoader resourceLoader) {
		super();
		this.path = path;
		this.name = name;
		this.origin = origin;
		this.jarEntry = jarEntry;
		this.resourceLoader = resourceLoader;
	}
	public ClassLoader getResourceLoader() {
		return resourceLoader;
	}
	/**
	 * 类路径资源，用于将类资源封装为抽象资源使用
	 * @param path 路径
	 * @param name 资源名
	 * @param jarEntry jar实体
	 * @param resourceLoader 资源加载器
	 */
	public ClassPathResource(String path, String name,  JarEntry jarEntry, ClassLoader resourceLoader) {
		super();
		this.path = path;
		this.name = name;
		this.origin = path;
		this.jarEntry = jarEntry;
		this.resourceLoader = resourceLoader;
	}

	/**
	 * 获取资源的路径
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * resource is direct
	 * 
	 * @return is direct
	 */
	public boolean isDirect() {
		return this.jarEntry.isDirectory();
	}

	/**
	 * get the resource last modify time
	 * 
	 * @return the last modify time
	 */
	public long lastModified() {
		return this.jarEntry.getLastModifiedTime().toMillis();
	}

	/**
	 * get resource available data size
	 * 
	 * @return the resource available stream length
	 */
	public long size() {
		return this.jarEntry.getSize();
	}

	/**
	 * if the resource is direct ,use the method get all list
	 * 
	 * @return return the resource list
	 */
	public List<Resource> listResource() {
		String pathExpress = this.path;
		String last = this.path.substring(this.path.length() - 1);
		if (StringUtil.equals(last, "/") || StringUtil.equals(last, "\\")) {
			pathExpress += "*";
		} else {
			pathExpress += "/*";
		}
		return ResourceManager.getResourceList(pathExpress);
	}

	/**
	 * get the resource out stream
	 * 
	 * @return os
	 * @throws FileNotFoundException ex
	 */
	public OutputStream getOutputStream() throws FileNotFoundException {
		throw new UnsupportedOperationException("jar resource is not support output stream");
	}

	/**
	 * get the resource input stream
	 * 
	 * @return input stream
	 */
	public InputStream getInputStream() {
		return resourceLoader.getResourceAsStream(this.jarEntry.getName());
	}

//	private InputStream getClassResourceAsStream(String resourceName) {
//		InputStream inputStream = null;
//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//		while(classLoader != null && (inputStream = classLoader.getResourceAsStream(resourceName)) == null) {
//			classLoader = classLoader.getParent();
//		}
//		return inputStream;
//	}
	/**
	 * get the resource name
	 * 
	 * @return resource
	 */
	public String getName() {
		return name;
	}

	/**
	 * 资源原始路径
	 * 
	 * @return resource origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * if the type of resource is jar ,use the method to get jar entry
	 * 
	 * @return the jar
	 */
	public JarEntry getJarEntry() {
		return jarEntry;
	}
	@Override
	public URI getURI() {
		throw new UnsupportedOperationException();
	}
}

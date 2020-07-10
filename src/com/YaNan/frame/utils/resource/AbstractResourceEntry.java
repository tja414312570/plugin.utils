package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.JarEntry;

import com.YaNan.frame.utils.StringUtil;
import com.YaNan.frame.utils.asserts.Assert;

public class AbstractResourceEntry implements Resource{
	/**
	 * 资源类型 jar ，file
	 * @author yanan
	 *
	 */
	public static enum Type{
		JAR,FILE
	}
	//资源绝对路劲
	private String path;
	//资源名称
	private String name;
	//如果为扫描产生，此值标记扫描路径
	private String origin;
	//资源类型
	private Type type;
	//如果是文件类型
	private File file;
	//如果是jar资源
	private JarEntry jarEntry;
	/**
	 * 抽象资源
	 * @param path 资源路径
	 * @param name 资源名称
	 * @param origin 资源来源路径
	 * @param type 资源
	 * @param file 如果是文件类型传入
	 * @param jarEntry 如果是jar资源类型传入
	 */
	public AbstractResourceEntry(String path, String name, String origin, Type type, File file, JarEntry jarEntry) {
		super();
		this.path = path;
		this.name = name;
		this.origin = origin;
		this.type = type;
		this.file = file;
		this.jarEntry = jarEntry;
	}
	/**
	 * 将file封装成抽象资源类型
	 * @param file 要被封装的文件
	 */
	public AbstractResourceEntry(File file) {
		super();
		this.path = file.getAbsolutePath();
		this.name = file.getName();
		this.origin = file.getAbsolutePath();
		this.type = Type.FILE;
		this.file = file;
	}
	/**
	 * 获取资源的路径
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	void setPath(String path) {
		this.path = path;
	}
	/**
	 * resource is direct
	 * @return is direct
	 */
	public boolean isDirect() {
		switch(type) {
			case JAR:
				return this.jarEntry.isDirectory();
			case FILE:
				return this.file.isDirectory();
		}
		return false;
	}
	/**
	 * get the resource last modify time
	 * @return the last modify time
	 */
	public long lastModified() {
		switch(type) {
			case JAR:
				return this.jarEntry.getLastModifiedTime().toMillis();
			case FILE:
				return this.file.lastModified();
		}	
		return -1l;
	}
	/**
	 * get resource available data size
	 * @return the resource available stream length
	 */
	public long size() {
		switch(type) {
			case JAR:
				return this.jarEntry.getSize();
			case FILE:
				return this.file.length();
		}	
		return -1l;
	}
	/**
	 * if the resource is direct ,use the method get all list
	 * @return return the resource list 
	 */
	public List<AbstractResourceEntry> listResource() {
		String pathExpress = this.path;
		String last = this.path.substring(this.path.length()-1);
		if(StringUtil.equals(last,"/") || StringUtil.equals(last,"\\")) {
			pathExpress += "*";
		}else {
			pathExpress += "/*";
		}
		return ResourceManager.getResourceList(pathExpress);
	}
	/**
	 * get the resource out stream 
	 * @return os
	 * @throws FileNotFoundException ex
	 */
	public OutputStream getOutputStream() throws FileNotFoundException {
		Assert.isTrue(this.type == Type.JAR,new RuntimeException("jar file could not get output"));
		return new FileOutputStream(this.file);
	}
	/**
	 * get the resource input stream
	 * @return input stream
	 */
	public InputStream getInputStream(){
		try {
			if(this.type == Type.JAR) {
				return ClassLoader.getSystemResourceAsStream(this.jarEntry.getName());
			}else {
				return new FileInputStream(this.file);
			}
		} catch (FileNotFoundException e) {
			throw new ResourceInputStreamException(e);
		}
	}
	/**
	 * get the resource name
	 * @return resource
	 */
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	/**
	 * 资源原始路径
	 * @return resource origin
	 */
	public String getOrigin() {
		return origin;
	}
	void setOrigin(String origin) {
		this.origin = origin;
	}
	/**
	 * 获取资源类型
	 * @return resource type
	 */
	public Type getType() {
		return type;
	}
	void setType(Type type) {
		this.type = type;
	}
	/**
	 * if the type of resource is file ,use the method to get origin file 
	 * @return file
	 */
	public File getFile() {
		return file;
	}
	void setFile(File file) {
		this.file = file;
	}
	/**
	 * if the type of resource is jar ,use the method to get jar entry
	 * @return the jar
	 */
	public JarEntry getJarEntry() {
		return jarEntry;
	}
	void setJarEntry(JarEntry jarEntry) {
		this.jarEntry = jarEntry;
	}
	@Override
	public String toString() {
		return "AbstractResourceEntry [path=" + path + ", name=" + name + ", origin=" + origin + ", type=" + type
				+ ", file=" + file + ", jarEntry=" + jarEntry + "]";
	}
}

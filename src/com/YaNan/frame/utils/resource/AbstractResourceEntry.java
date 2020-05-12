package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import com.YaNan.frame.utils.asserts.Assert;

public class AbstractResourceEntry{
	public static enum Type{
		JAR,FILE
	}
	private String path;
	private String name;
	private String origin;
	private Type type;
	private File file;
	private JarEntry jarEntry;
	public AbstractResourceEntry(String path, String name, String origin, Type type, File file, JarEntry jarEntry) {
		super();
		this.path = path;
		this.name = name;
		this.origin = origin;
		this.type = type;
		this.file = file;
		this.jarEntry = jarEntry;
	}
	public AbstractResourceEntry(File file) {
		super();
		this.path = file.getAbsolutePath();
		this.name = file.getName();
		this.origin = file.getAbsolutePath();
		this.type = Type.FILE;
		this.file = file;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * resource is direct
	 * @return
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
	 * @return
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
	 * @return
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
	public List<File> listResource() {
		return new ArrayList<File>();
	}
	public OutputStream getOutputStream() throws FileNotFoundException {
		Assert.isTrue(this.type == Type.JAR,new RuntimeException("jar file could not get output"));
		return new FileOutputStream(this.file);
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public JarEntry getJarEntry() {
		return jarEntry;
	}
	public void setJarEntry(JarEntry jarEntry) {
		this.jarEntry = jarEntry;
	}
	@Override
	public String toString() {
		return "AbstractResourceEntry [path=" + path + ", name=" + name + ", origin=" + origin + ", type=" + type
				+ ", file=" + file + ", jarEntry=" + jarEntry + "]";
	}
}

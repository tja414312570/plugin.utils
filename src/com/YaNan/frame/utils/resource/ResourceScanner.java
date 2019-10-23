package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.YaNan.frame.utils.PathMatcher;

/*
 * encoding = "utf-8"
 * 用于路径的批量操作，目前只用于 删除，复制，移动
 */
public class ResourceScanner {
	private File file;
	private List<String> filter = new ArrayList<String>();
	private int num;
	public int getNum() {
		return num;
	}

	private File target;
	private int buffSize = 1024;


	public File getTarget() {
		return target;
	}

	public int getBuffSize() {
		return buffSize;
	}

	// 传入单个路径
	public ResourceScanner(String source) {
		int index = source.indexOf("!");
		if(index>-1) {
			this.filter.add(source+"**");
			source = source.substring(0,index);
		}
		// 添加事件
		File file = new File(source);
		if (file.exists()) {
			this.file = file;
		} else {
			throw new RuntimeException("resource path invalid：" + file.toString());
		}
	}

	public ResourceScanner(File file) {
		if (file.exists()) {
			this.file=file;
		} else {
			throw new RuntimeException("resource path invalid：" + file.toString());
		}
	}

	public void scanner(ResourceInter p) {
		if(!file.getAbsolutePath().endsWith(".jar"))
			doScannerFile(p, file);
		else
			doScannerJar(p,file);
			
	}
	
	private void doScannerJar(ResourceInter p, File file) {
		try {
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while(jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				String fileName =  jarEntry.getName();
				int fileNameIndex = fileName.lastIndexOf("/");
				if(fileNameIndex>-1) 
					fileName = fileName.substring(fileNameIndex+1);
				AbstractResourceEntry abstractResourceEntry = 
						new AbstractResourceEntry(file.getAbsolutePath()+"!/"+jarEntry.getName(),fileName, this.
								file.getAbsolutePath(), 
								AbstractResourceEntry.Type.JAR, null, jarEntry);
					this.find(p,abstractResourceEntry);
			}
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void doScannerFile(ResourceInter p, File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if(list!=null)
			for (File f : list)
				doScannerFile(p, f);
		}
		AbstractResourceEntry abstractResourceEntry = 
			new AbstractResourceEntry(file.getPath(), file.getName(), this.file.getAbsolutePath(), AbstractResourceEntry.Type.FILE, file, null);
		this.find(p,abstractResourceEntry);
	}
	
	private void find(ResourceInter p, AbstractResourceEntry resource) {
		if(filter.isEmpty())
			p.find(resource);
		else for(String word : filter)
			if(PathMatcher.match(word, resource.getPath()).isMatch())
				p.find(resource);
	}


	public static interface ResourceInter {
		public void find(AbstractResourceEntry resource);
	}

	public void filter(String... strings) {
		for(String word : strings)
			this.filter.add(word);
	}
}

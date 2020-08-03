package com.yanan.utils.resource.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import com.yanan.utils.asserts.Assert;
import com.yanan.utils.resource.ClassPathResource;
import com.yanan.utils.resource.FileResource;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.string.PathMatcher;

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
//			this.filter.add(source+"**");
			source = source.substring(0,index);
		}
		// 添加事件
		File file = new File(source);
		Assert.isFalse(file.exists(),new IllegalArgumentException("resource path invalid：" + file.toString()));
		this.file = file;
	}

	public ResourceScanner(File file) {
		Assert.isFalse(file.exists(),new IllegalArgumentException("resource path invalid：" + file.toString()));
		this.file = file;
	}

	public void scanner(ResourceInter p) {
		if(!file.getAbsolutePath().endsWith(".jar")) {
			doScannerFile(p, file);
		}else {
			doScannerJar(p,file);
		}
	}
	
	private void doScannerJar(ResourceInter p, File file) {
		try {
			JarFile jarFile = new JarFile(file);
			jarFile.stream().forEach((jarEntry)->{
				String fileName =  jarEntry.getName();
				int fileNameIndex = fileName.lastIndexOf("/");
				if(fileNameIndex>-1) {
					fileName = fileName.substring(fileNameIndex+1);
				}
				Resource abstractResourceEntry = 
						new ClassPathResource(file.getAbsolutePath()+"!/"+jarEntry.getName(),fileName, this.
								file.getAbsolutePath(), 
								jarEntry,Thread.currentThread().getContextClassLoader());
				this.find(p,abstractResourceEntry);
			});
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void doScannerFile(ResourceInter p, File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if(list!=null) {
				for (File f : list) {
					doScannerFile(p, f);
				}
			}
		}
		Resource abstractResourceEntry = 
			new FileResource( this.file.getAbsolutePath(), file);
		this.find(p,abstractResourceEntry);
	}
	
	private void find(ResourceInter p, Resource resource) {
		if(filter.isEmpty()) {
			p.find(resource);
		}else { 
			for(String word : filter) {
				if(PathMatcher.match(word, resource.getPath()).isMatch()) {
					p.find(resource);
				}
			}
		}
	}

	public static interface ResourceInter {
		public void find(Resource resource);
	}

	public void filter(String... strings) {
		for(String word : strings) {
			this.filter.add(word);
		}
	}
}

package com.YaNan.frame.utils.resource;

import java.io.File;

import com.YaNan.frame.utils.resource.Path.PathInter;

public class PackageScanner {
	private String packageName;
	private String classPath;
	private File sourcePath;
	private File packagePath;
	private boolean ignoreLoadingException;
	public PackageScanner(String packageName, String classPath) {
		this.packageName = packageName;
		this.classPath = classPath;
	}
	public PackageScanner(){
		this.classPath = ResourceManager.classPath();
	}
	public void doScanner(final ClassInter inter) {
		if(this.packageName==null)this.packageName=".";
		String packagePath = this.packageName.replace(".", "/");
		this.sourcePath= new File(classPath);//资源目录
		this.packagePath= new File(classPath+"/"+packagePath);//包路径
		Path scanner = new Path(this.packagePath.getAbsolutePath());
		scanner.scanner(new PathInter(){
			@Override
			public void find(File file) {
				if(file.getName().endsWith(".class")){
					String className = file.getAbsolutePath().replace(sourcePath.getAbsolutePath(), "").replace("\\","." ).replace("/", ".").replace("$", ".").replace(".class", "");
					className= className.substring(0, 1).equals(".")?className.substring(1, className.length()):className;
					try {
						inter.find(Class.forName(className));
					} catch (Throwable e) {
						if(!ignoreLoadingException&&!e.getClass().equals(ClassNotFoundException.class))
							throw new RuntimeException("failed to load class \""+className+"\" at class file \""+file+"\"",e);
					}
				}
				
			}
		});
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public File getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(File sourcePath) {
		this.sourcePath = sourcePath;
	}
	public File getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(File packagePath) {
		this.packagePath = packagePath;
	}
	public boolean isIgnoreLoadingException() {
		return ignoreLoadingException;
	}
	public void setIgnoreLoadingException(boolean ignoreLoadingException) {
		this.ignoreLoadingException = ignoreLoadingException;
	}
	public static interface ClassInter {
		public void find(Class<?> cls);
	}
}

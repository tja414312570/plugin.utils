package com.yanan.utils.resource.scanner;


import java.util.Arrays;

import com.yanan.utils.ArrayUtils;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.resource.scanner.ResourceScanner.ResourceInter;

/**
 * 支持通配符,按照PathMath匹配模式
 * @author yanan
 *
 */
public class PackageScanner {
	private static final String JAR_SUFFIX = ".jar!";
	private String[] packageDirs;
	private boolean ignoreLoadingException;
	
	public static int getMarkIndex(String string, int offset) {
		int hI = string.indexOf("{",offset);
		int sI = string.indexOf("*",offset);
		int qI = string.indexOf("?",offset);
		int i = -1;
		if(hI!=-1)
			if(i != -1)
				i = Math.min(i, hI);
			else i = hI;
		if(sI!=-1)
			if(i != -1)
				i = Math.min(i, sI);
			else i = sI;
		if(qI!=-1)
			if(i != -1)
				i = Math.min(i, qI);
			else i = qI;
		return i;
	}
	
	public String[] getScanPath() {
		return packageDirs == null?null:Arrays.copyOf(packageDirs, packageDirs.length);
	}
	public void addScanPath(String... paths) {
		if(paths.length>0) {
			if(packageDirs==null) {
				packageDirs =paths;
			}else {
				this.packageDirs = ArrayUtils.megere(packageDirs, paths);
			}
		}
	}
	public void setScanPath(String paths) {
		if(paths == null)
			throw new IllegalArgumentException();
		String[] tempPaths = paths.split(",");
		this.packageDirs = null;
		this.addScanPath(tempPaths);
	}
	public void setScanPath(String[] paths) {
		this.packageDirs = paths;
	}
	public PackageScanner(String classPath) {
		this.addScanPath(classPath);
	}
	public PackageScanner(){
	}
	public void doScanner(final ClassInter inter) {
		if(this.packageDirs==null)
			this.addScanPath(ResourceManager.classPath());
		for (String packDir : packageDirs) {
			if (packDir == null)
				continue;
			String packReplace;
			int index = packDir.lastIndexOf("/");
			if(index == -1) {
				packReplace = packDir.replace(".", "/");
			}else {
				packReplace = packDir.substring(0,index+1)+packDir.substring(index+1).replace(".", "/");
			}
			String[] paths = ResourceManager.getPathExress(packReplace);
			this.doScanner(paths,inter);
		}
	}
	private void doScanner(String[] paths, ClassInter inter) {
		//扫描位置 过滤类型 类路径
		String scannerPath,classPath = null;
		for(String path : paths) {
			//jar里的资源
			int jarIndex = path.indexOf(JAR_SUFFIX);
			if(jarIndex != -1) {
				classPath = scannerPath = path.substring(0,jarIndex+JAR_SUFFIX.length());
			}else {
				for(String classpath : ResourceManager.classPaths()) {
					if(path.startsWith(classpath)) 
						classPath = classpath;
				}
				if(classPath == null)
					throw new IllegalArgumentException("cloud not found class path for "+path);
				int maxIndex = Math.max(path.indexOf("?"), path.indexOf("*"));
				if(maxIndex != -1) {
					scannerPath = path.substring(0,maxIndex);
					scannerPath = path.substring(0,scannerPath.lastIndexOf("/"));
				}else {
					scannerPath = path.substring(0,path.lastIndexOf("/"));
				}
			}
			this.scanner(scannerPath,classPath,inter,path);
		}
	}
	private void scanner(String path,String classPath,ClassInter inter, String filter) {
		ResourceScanner scanner = new ResourceScanner(path);
		if(filter != null)
			scanner.filter(filter+".class");
		scanner.scanner(new ResourceInter(){
			@Override
			public void find(Resource resourceEntry) {
				if(resourceEntry.getName().indexOf("$") == -1){
					String className = resourceEntry.getPath().replace(classPath, "").replace("\\","." ).replace("/", ".").replace(".class", "");
					className= className.substring(0, 1).equals(".")?className.substring(1, className.length()):className;
					try {
						inter.find(Class.forName(className));
					} catch (Throwable e) {
						if(!ignoreLoadingException)
							throw new RuntimeException("failed to load class \""+className+"\" at class file \""+resourceEntry.getPath()+"\"",e);
						else
							e.printStackTrace();
					}
				}
				
			}
		});
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

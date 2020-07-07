package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import com.YaNan.frame.utils.StringUtil;
import com.YaNan.frame.utils.asserts.Assert;
import com.YaNan.frame.utils.resource.AbstractResourceEntry.Type;

/**
 * 资源管理工具，获取文件
 * 支持多个ClassPath，支持自动查询主ClassPath
 * @author yanan
 *
 */
public class ResourceManager {
	final static String CLASSPATH_EXPRESS = "classpath:";
	final static String CLASSPATHS_EXPRESS = "classpath*:";
	final static String PROJECT_EXPRESS = "project:";
	static volatile String[] classPath;
	/**
	 * 根据路径表达式获取具体的路径
	 * 比如classpath:返回/home/class
	 * @param pathExpress 路径表达式
	 * @return 具体的路径
	 */
	public static String[] getPathExress(String pathExpress){
		if(pathExpress == null) {
			throw new ResourcePathExpressException("path express is null");
		}
		int cpIndex = pathExpress.indexOf(CLASSPATH_EXPRESS);
		if(cpIndex>-1) {
			return new String[]{classPath()+pathExpress.substring(cpIndex+CLASSPATH_EXPRESS.length())};
		}
		cpIndex = pathExpress.indexOf(CLASSPATHS_EXPRESS);
		if(cpIndex>-1) {
			String[] temp = new String[classPaths().length];
			for(int i = 0;i<temp.length;i++)
				temp[i] = classPaths()[i]+pathExpress.substring(cpIndex+CLASSPATHS_EXPRESS.length());
			return temp;
		}
		cpIndex = pathExpress.indexOf(PROJECT_EXPRESS);
		if(cpIndex>-1) {
			try {
				return new String[]{projectPath()+pathExpress.substring(cpIndex+PROJECT_EXPRESS.length())};
			} catch (IOException e) {
				throw new ResourceNotFoundException("failed to get project director",e);
			}
		}
		return new String[]{pathExpress}; 
	}
	/**
	 * 根据路径表达式数组返回具体的路径
	 * @param pathExpressArray 路径表达式数组
	 * @return 具体的路径
	 */
	public static String[] getPathExress(String[] pathExpressArray) {
		Assert.isNull(pathExpressArray);
		List<String> pathList = new ArrayList<>();
		for(String pathExpress : pathExpressArray) {
			String[] paths = getPathExress(pathExpress);
			if(paths != null) {
				for(String path : paths) {
					pathList.add(path);
				}
			}
		}
		return pathList.toArray(new String[]{});
	}
	public static String projectPath() throws IOException {
		return new File("").getCanonicalPath().replace("%20"," ");
	}
	/**
	 * 通过路径表达式获取符合该路劲的所有资源
	 * @param pathExpress 路径表达式
	 * @return 抽象资源列表
	 */
	public static List<AbstractResourceEntry> getResourceList(String pathExpress){
		String[] pathResults = getPathExress(pathExpress);
		List<AbstractResourceEntry> result = new ArrayList<AbstractResourceEntry>();
		for(String pathResult : pathResults){
			int index = pathResult.indexOf("*");
			int qndex = pathResult.indexOf("?");
			if(qndex>-1&&qndex<index)
				index = qndex;
			if(index==-1){
				File file = new File(pathResult);
				if(file.exists()) {
					result.add(new AbstractResourceEntry(file.getAbsolutePath(), file.getName(), file.getAbsolutePath(), Type.FILE, file, null));
				}
				continue;
//				Assert.isFalse(file.exists(),new ResourceNotFoundException("resource \"" +pathResult+"\" is not exists! absolute:\""+file.getAbsolutePath()+"\""));
//				return Arrays.asList();
			}
			String path = pathResult.substring(0, index);
			qndex = path.lastIndexOf("/");
			if(qndex>0) {
				path = path.substring(0,qndex);
			}
			if(StringUtil.isBlank(path))
				try {
					path = new File("").getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
			result.addAll(getMatchFile(path,pathResult));
		}
		return result.stream().distinct().collect(Collectors.toList());
	}
	/**
	 * 根据路径表达式获取一个资源
	 * @param pathExpress 路径表达式
	 * @return 抽象资源
	 */
	public static AbstractResourceEntry getResource(String pathExpress){
		List<AbstractResourceEntry>  list = getResourceList(pathExpress);
		return list == null || list.isEmpty() ? null : list.get(0);
	}
	/*
	 * 获取匹配的资源，是获取抽象资源的具体实现
	 */
	private static List<AbstractResourceEntry> getMatchFile(String pathExpress,String regex) {
		ResourceScanner path = new ResourceScanner(pathExpress);
		path.filter(regex);
		final List<AbstractResourceEntry> fileList = new ArrayList<AbstractResourceEntry>();
		path.scanner((resource)->fileList.add(resource));
		return fileList;
	}
	public static String classPath() {
		return classPaths()[0];
	}
	public static String[] classPaths() {
		if(classPath == null) {
			synchronized (ResourceManager.class) {
				Vector<String> temp = new Vector<>();
				StackTraceElement[] stacks = new RuntimeException().getStackTrace();
				Class<?> mainClass = null;
				for(StackTraceElement stack : stacks) {
					if(stack.getMethodName() == "main") {
						try {
							mainClass = Class.forName(stack.getClassName());
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				if(mainClass != null) {
					temp.add(getClassPath(mainClass)[0]);
				}
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				String path = null;
				while(loader != null) {
					path = loader.getResource("").getPath().replace("%20"," ");
					if(!temp.contains(path))
						temp.add(path);
					loader = loader.getParent();
				}
				classPath = temp.toArray(new String[] {});
			}
		}
		return classPath;
	}
	/**
	 * @param name String:express
	 * @return the real path
	 */
	public static String getClassPath(String name) {
		return name.replace("%20"," ").replace(ResourceManager.classPath(), "").replaceAll("/|\\\\", ".");
	}
	/**
	 * get classpath by class file 
	 * @param clzzs an array contains class all classpath
	 * @return class path array
	 */
	public static String[] getClassPath(Class<?>... clzzs) {
		String[] classPaths = new String[clzzs.length];
		for(int i = 0;i<clzzs.length;i++) {
			Class<?> clzz = clzzs[i];
			String path = "";
			try {
				path = clzz.getResource(".").getFile().replace("%20"," ");
				String packagePath = clzz.getPackage().getName().replace(".",File.separator);
				classPaths[i] = path.substring(0,path.lastIndexOf(packagePath));
			}catch(Exception e) {
				path = clzz.getProtectionDomain().getCodeSource().getLocation().getFile();  
				try {
					path = java.net.URLDecoder.decode(path, "UTF-8");
					classPaths[i] = path.replace("%20"," ");
				} catch (UnsupportedEncodingException t) {
					t.addSuppressed(e);
					throw new ResourceScannerException(t);
				}
			}
		}
		return classPaths;
	}
	public static void addClassPath(Class<?>... contextClass) {
		Assert.isNull(contextClass, "content class is null");
		if(contextClass == null || contextClass.length == 0) 
			return;
		synchronized (ResourceManager.class) {
			String strs[] = getClassPath(contextClass);
			String[] template;
			for(String str : strs) {
				if(Arrays.binarySearch(classPath, str) < 0) {
					template = new String[classPath.length+1];
					System.arraycopy(classPath, 0, template, 0, classPath.length);
					template[classPath.length] = str;
					classPath = template;
				}
			}
		}
	}
}

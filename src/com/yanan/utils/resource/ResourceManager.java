package com.yanan.utils.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import com.yanan.utils.ArrayUtils;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.resource.scanner.ResourceScanner;
import com.yanan.utils.resource.scanner.ResourceScannerException;
import com.yanan.utils.string.StringUtil;

/**
 * 资源管理工具，获取文件 支持多个ClassPath，支持自动查询主ClassPath
 * 
 * @author yanan
 *
 */
public class ResourceManager {
	final static String CLASSPATH_EXPRESS = "classpath:";
	final static String CLASSPATHS_EXPRESS = "classpath*:";
	final static String PROJECT_EXPRESS = "project:";
	private static volatile String[] classPaths;

	/**
	 * 根据路径表达式获取具体的路径 比如classpath:返回/home/class
	 * 
	 * @param pathExpress 路径表达式
	 * @return 具体的路径
	 */
	public static String[] getPathExress(String pathExpress) {
		if (pathExpress == null) {
			throw new ResourcePathExpressException("path express is null");
		}
		int cpIndex = pathExpress.indexOf(CLASSPATH_EXPRESS);
		if (cpIndex > -1) {
			return new String[] { classPath() + pathExpress.substring(cpIndex + CLASSPATH_EXPRESS.length()) };
		}
		cpIndex = pathExpress.indexOf(CLASSPATHS_EXPRESS);
		if (cpIndex > -1) {
			String[] temp = new String[classPaths().length];
			for (int i = 0; i < temp.length; i++)
				temp[i] = classPaths()[i] + pathExpress.substring(cpIndex + CLASSPATHS_EXPRESS.length());
			return temp;
		}
		cpIndex = pathExpress.indexOf(PROJECT_EXPRESS);
		if (cpIndex > -1) {
			try {
				return new String[] { projectPath() + pathExpress.substring(cpIndex + PROJECT_EXPRESS.length()) };
			} catch (IOException e) {
				throw new ResourceNotFoundException("failed to get project director", e);
			}
		}
		return new String[] { pathExpress };
	}

	/**
	 * 根据路径表达式数组返回具体的路径
	 * 
	 * @param pathExpressArray 路径表达式数组
	 * @return 具体的路径
	 */
	public static String[] getPathExress(String[] pathExpressArray) {
		Assert.isNull(pathExpressArray);
		List<String> pathList = new ArrayList<>();
		for (String pathExpress : pathExpressArray) {
			String[] paths = getPathExress(pathExpress);
			if (paths != null) {
				for (String path : paths) {
					pathList.add(path);
				}
			}
		}
		return pathList.toArray(new String[] {});
	}

	/**
	 * 获取项目路径
	 * 
	 * @return 项目路径
	 * @throws IOException ex
	 */
	public static String projectPath() throws IOException {
		return new File("").getCanonicalPath().replace("%20", " ");
	}

	/**
	 * 通过路径表达式获取符合该路劲的所有资源
	 * 
	 * @param pathExpress 路径表达式
	 * @return 抽象资源列表
	 */
	public static List<Resource> getResourceList(String pathExpress) {
		String[] pathResults = getPathExress(pathExpress);
		List<Resource> result = new ArrayList<>();
		for (String pathResult : pathResults) {
			int index = pathResult.indexOf("*");
			int qndex = pathResult.indexOf("?");
			if (qndex > -1 && qndex < index)
				index = qndex;
			if (index == -1) {
				Resource resource ;
				//普通文件直接返回了
				if(pathExpress.indexOf(".jar!") == -1) {
					File file = new File(pathResult);
					resource = new FileResource(file);
					result.add(resource);
					continue;
				}
				index = pathResult.length();
			}
			String path = pathResult.substring(0, index);
			qndex = path.lastIndexOf("/");
			if (qndex > 0) {
				path = path.substring(0, qndex);
			}
			if (StringUtil.isBlank(path))
				try {
					path = new File("").getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
			result.addAll(getMatchFile(path, pathResult));
		}
		return result.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 根据路径表达式获取一个资源
	 * 
	 * @param pathExpress 路径表达式
	 * @return 抽象资源
	 */
	public static Resource getResource(String pathExpress) {
		List<Resource> list = getResourceList(pathExpress);
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	/*
	 * 获取匹配的资源，是获取抽象资源的具体实现
	 */
	private static List<Resource> getMatchFile(String pathExpress, String regex) {
		ResourceScanner path = new ResourceScanner(pathExpress);
		path.filter(regex);
		final List<Resource> fileList = new ArrayList<>();
		path.scanner((resource) -> fileList.add(resource));
		return fileList;
	}

	public static String classPath() {
		return classPaths()[0];
	}

	/**
	 * 获取类路径
	 * 
	 * @return 类路径
	 */
	public static String[] classPaths() {
		if (classPaths == null) {
			synchronized (ResourceManager.class) {
				Vector<String> temp = new Vector<>();
				StackTraceElement[] stacks = new RuntimeException().getStackTrace();
				Class<?> mainClass = null;
				for (StackTraceElement stack : stacks) {
					if (stack.getMethodName().equals("main")) {
						try {
							mainClass = Class.forName(stack.getClassName());
							temp.add(getClassPath(mainClass)[0]);
							break;
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				String path = null;
				
				while (loader != null) {
					URL url = loader.getResource("");
					if(url != null) {
						path = loader.getResource("").getPath().replace("%20", " ");
						if (!temp.contains(path))
							temp.add(path);
					}
					loader = loader.getParent();
				}
				classPaths = temp.toArray(new String[] {});
			}
		}
		return classPaths;
	}

	/**
	 * @param name String:express
	 * @return the real path
	 */
	public static String getClassPath(String name) {
		return name.replace("%20", " ").replace(ResourceManager.classPath(), "").replaceAll("/|\\\\", ".");
	}

	/**
	 * get classpath by class file
	 * 
	 * @param clzzs an array contains class all classpath
	 * @return class path array
	 */
	public static String[] getClassPath(Class<?>... clzzs) {
		String[] classPaths = new String[clzzs.length];
		for (int i = 0; i < clzzs.length; i++) {
			Class<?> clzz = clzzs[i];
			String path = "";
			try {
				path = clzz.getResource(".").getFile().replace("%20", " ");
				String packagePath = clzz.getPackage().getName().replace(".", File.separator);
				classPaths[i] = path.substring(0, path.lastIndexOf(packagePath));
			} catch (Exception e) {
				path = clzz.getProtectionDomain().getCodeSource().getLocation().getFile();
				try {
					path = java.net.URLDecoder.decode(path, "UTF-8");
					classPaths[i] = path.replace("%20", " ");
				} catch (UnsupportedEncodingException t) {
					t.addSuppressed(e);
					throw new ResourceScannerException(t);
				}
			}
		}
		return classPaths;
	}

	/**
	 * 设置主类路径
	 * 
	 * @param classPath 类路径
	 */
	public static void setClassPath(String classPath,int index) {
		synchronized (ResourceManager.class) {
			classPaths = ArrayUtils.add(classPaths, classPath, index);
		}
	}

	/**
	 * 添加类路径
	 * 
	 * @param contextClass 上下文类
	 */
	public static void addClassPath(Class<?>... contextClass) {
		Assert.isTrue( contextClass.length == 0, "content class is null");
		String classpaths[] = getClassPath(contextClass);
		addClassPath(classpaths);
	}
	/**
	 * 添加类路径
	 * @param classPaths
	 */
	public static void addClassPath(String... classPaths) {
		synchronized (ResourceManager.class) {
			classPaths = ArrayUtils.add(classPaths, classPaths);
		}
	}
}

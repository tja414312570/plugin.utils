package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.YaNan.frame.utils.StringUtil;
import com.YaNan.frame.utils.asserts.Assert;
import com.YaNan.frame.utils.asserts.AssertNotNullException;
import com.YaNan.frame.utils.resource.AbstractResourceEntry.Type;

/**
 * 资源管理工具，获取文件
 * @author yanan
 *
 */
public class ResourceManager {
	final static String CLASSPATH_EXPRESS = "classpath:";
	final static String PROJECT_EXPRESS = "project:";
	static volatile String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("%20"," ");
	public static String getPathExress(String pathExpress){
		Assert.isNull(pathExpress,new ResourcePathExpressException("path express is null"));
		int cpIndex = pathExpress.indexOf(CLASSPATH_EXPRESS);
		if(cpIndex>-1) {
			pathExpress= classPath()+pathExpress.substring(cpIndex+CLASSPATH_EXPRESS.length());
		}
		cpIndex = pathExpress.indexOf(PROJECT_EXPRESS);
		if(cpIndex>-1) {
			try {
				pathExpress= projectPath()+pathExpress.substring(cpIndex+PROJECT_EXPRESS.length());
			} catch (IOException e) {
				throw new ResourceNotFoundException("failed to get project director",e);
			}
		}
		return pathExpress;
	}
	public static String projectPath() throws IOException {
		return new File("").getCanonicalPath().replace("%20"," ");
	}
	/**
	 * 通过路径表达式获取符合该路劲的所有资源
	 * @param pathExpress
	 * @return
	 */
	public static List<AbstractResourceEntry> getResourceList(String pathExpress){
		pathExpress = getPathExress(pathExpress);
		int index = pathExpress.indexOf("*");
		int qndex = pathExpress.indexOf("?");
		if(qndex>-1&&qndex<index)
			index = qndex;
		if(index==-1){
			File file = new File(pathExpress);
			Assert.isFalse(file.exists(),new ResourceNotFoundException("resource \"" +pathExpress+"\" is not exists! absolute:\""+file.getAbsolutePath()+"\""));
			return Arrays.asList(new AbstractResourceEntry(file.getAbsolutePath(), file.getName(), file.getAbsolutePath(), Type.FILE, file, null));
		}
		String path = pathExpress.substring(0, index);
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
		return getMatchFile(path,pathExpress);
	}
	public static AbstractResourceEntry getResource(String pathExpress){
		List<AbstractResourceEntry>  list = getResourceList(pathExpress);
		return list == null || list.isEmpty() ? null : list.get(0);
	}
	private static List<AbstractResourceEntry> getMatchFile(String pathExpress,String regex) {
		ResourceScanner path = new ResourceScanner(pathExpress);
		path.filter(regex);
		final List<AbstractResourceEntry> fileList = new ArrayList<AbstractResourceEntry>();
		path.scanner((resource)->fileList.add(resource));
		return fileList;
	}
	public static String classPath() {
		return classPath;
	}
	/**
	 * @param name
	 * @return
	 */
	public static String getClassPath(String name) {
		return name.replace("%20"," ").replace(ResourceManager.classPath(), "").replaceAll("/|\\\\", ".");
	}
	/**
	 * get classpath by class file 
	 * @param clzzs an array contains class all classpath
	 * @return
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
	public static void switchClassPath(Class<?> contextClass) {
		Assert.isNull(contextClass, "content class is null");
		synchronized (ResourceManager.class) {
			classPath = getClassPath(contextClass)[0];
		}
	}
	public static void switchClassPathIfNotExist(Class<?> contextClass) {
		try {
			Assert.isNotNull(contextClass);
			synchronized (ResourceManager.class) {
				classPath = getClassPath(contextClass)[0];
			}
		}catch(AssertNotNullException t) {
			
		}
	}
}

package com.YaNan.frame.utils.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.YaNan.frame.utils.resource.Path.PathInter;

/**
 * 资源管理工具，获取文件
 * @author yanan
 *
 */
public class ResourceManager {
	final static String CLASSPATH_EXPRESS = "classpath:";
	final static String PROJECT_EXPRESS = "project:";
	public static String getPathExress(String pathExpress){
		if(pathExpress==null)
			throw new ResourcePathExpressException("path express is null");
		int cpIndex = pathExpress.indexOf(CLASSPATH_EXPRESS);
		if(cpIndex>-1)
			pathExpress= classPath()+pathExpress.substring(cpIndex+CLASSPATH_EXPRESS.length());
		cpIndex = pathExpress.indexOf(PROJECT_EXPRESS);
		if(cpIndex>-1)
			try {
				pathExpress= projectPath()+pathExpress.substring(cpIndex+PROJECT_EXPRESS.length());
			} catch (IOException e) {
				throw new ResourceNotFoundException("failed to get project director",e);
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
	public static List<File> getResource(String pathExpress){
		pathExpress = getPathExress(pathExpress);
		int index = pathExpress.indexOf("*");
		int qndex = pathExpress.indexOf("?");
		if(qndex>-1&&qndex<index)
			index = qndex;
		if(index==-1){
			File file = new File(pathExpress);
			if(!file.exists())
				throw new ResourceNotFoundException("resource \"" +pathExpress+"\" is not exists! absolute:\""+file.getAbsolutePath()+"\"");
			List<File> fileList = new ArrayList<File>();
			fileList.add(file);
			return fileList;
		}
		String path = pathExpress.substring(0, index);
		qndex = path.lastIndexOf("/");
		if(qndex>0)
			path = path.substring(0,qndex);
		if(path==null||path.trim().equals(""))
			try {
				path = new File("").getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return getMatchFile(path,pathExpress);
	}
	private static List<File> getMatchFile(String pathExpress,String regex) {
		Path path = new Path(pathExpress);
		path.filter(regex);
		final List<File> fileList = new ArrayList<File>();
		path.scanner(new PathInter() {
			@Override
			public void find(File file) {
				fileList.add(file);
			}
		});
		return fileList;
	}
	public static String classPath() {
		return ResourceManager.class.getClassLoader().getResource("").getPath().replace("%20"," ");
	}
	public static String getClassPath(String name) {
		return name.replace("%20"," ").replace(ResourceManager.classPath(), "").replaceAll("/|\\\\", ".");
	}
	public static String[] getClassPath(Class<?>... clzzs) {
		String[] classPaths = new String[clzzs.length];
		for(int i = 0;i<clzzs.length;i++) {
			Class<?> clzz = clzzs[i];
			String packagePath = clzz.getPackage().getName().replace(".",File.separator);
			String path = "";
			try {
				path = clzz.getResource(".").getFile();
				classPaths[i] = path.substring(0,path.lastIndexOf(packagePath)-1);
			}catch(Exception e) {
				path = clzz.getProtectionDomain().getCodeSource().getLocation().getFile();  
				try {
					path = java.net.URLDecoder.decode(path, "UTF-8");
					classPaths[i] = path;
				} catch (UnsupportedEncodingException t) {
					t.addSuppressed(e);
					throw new ResourceScannerException(t);
				}
			}
		}
		return classPaths;
	}
}

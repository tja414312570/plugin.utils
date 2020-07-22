package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import com.YaNan.frame.utils.resource.AbstractResourceEntry;
import com.YaNan.frame.utils.resource.PackageScanner;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.YaNan.frame.utils.resource.ResourceScanner;
import com.YaNan.frame.utils.resource.ResourceScanner.ResourceInter;

//file:/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/rt.jar!/java/util/jar/JarFile.class
public class JarTest {
	public static void main(String[] args) {
		AbstractResourceEntry resource = ResourceManager.getResource("classpath:com");
		System.out.println(resource);
		System.out.println(resource.listResource());
		//ResourceManager.classPath();//
		String dir = "/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/rt.jar!.java**";
//		PackageScanner scanner = new PackageScanner(dir);
//		scanner.doScanner(new PackageScanner.ClassInter() {
//			
//			@Override
//			public void find(Class<?> resource) {
//				System.out.println(resource);
//			}
//		});
		
		
		//		try {
//			System.out.println(JarFile.class.getResource("JarFile.class").getPath());
//			JarFile jarFile = new JarFile(dir);
//			System.out.println(jarFile);
//			Enumeration<JarEntry> jarEntries = jarFile.entries();
//			while(jarEntries.hasMoreElements()) {
//				JarEntry jarEntry = jarEntries.nextElement();
//				System.out.println(jarEntry);
////				JarInputStream jis = = new JarInputStream(jarEntry.)
//				System.out.println(JarTest.class.getResource("/"+jarEntry.getName()));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}

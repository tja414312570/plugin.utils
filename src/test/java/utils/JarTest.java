package utils;

import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;

//file:/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/rt.jar!/java/util/jar/JarFile.class
public class JarTest {
	public static void main(String[] args) {
		Resource resource = ResourceManager.getResource("classpath:com");
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

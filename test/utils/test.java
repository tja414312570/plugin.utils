package utils;

import com.YaNan.frame.utils.resource.PackageScanner;
import com.YaNan.frame.utils.resource.PackageScanner.ClassInter;
import com.YaNan.frame.utils.resource.ResourceManager;

public class test {
	public static void main(String[] args) {
		PackageScanner scanner = new PackageScanner();
		scanner.setClassPath(ResourceManager.classPath());
		scanner.doScanner(new ClassInter() {
			
			@Override
			public void find(Class<?> cls) {
				System.out.println(cls);
			}
		});
		System.out.println("ok");
	}
}

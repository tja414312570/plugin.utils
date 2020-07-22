package utils;

import com.YaNan.frame.utils.PathMatcher;
import com.YaNan.frame.utils.PathMatcher.Token;
import com.YaNan.frame.utils.resource.PackageScanner;
import com.YaNan.frame.utils.resource.PackageScanner.ClassInter;
import com.YaNan.frame.utils.resource.ResourceManager;

public class test {
	public static void main(String[] args) {
		String str = "/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/rt.jar!java/awt/dnd/DropTargetContext.class";
		String reg = "/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib/rt.jar!/java/**";
			
		
		
		System.out.println(PathMatcher.match(reg, str).isMatch());
		System.out.println("   ");
		System.out.println(PathMatcher.match(reg, str).variableMap());
		System.out.println("   ");
		for(Token token :PathMatcher.match(reg, str).getTokens()) {
			System.out.println(token.getName()+":"+token.getValue());
		}
		
	}
}

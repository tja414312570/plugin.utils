package utils;

import com.yanan.utils.string.PathMatcher;
import com.yanan.utils.string.PathMatcher.Token;

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

package utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.yanan.utils.string.PathMatcher;
import com.yanan.utils.string.PathMatcher.Token;

public class test {
	public static void main(String[] args) {
		String exp = "111/2222/{var1*}/3333/{var3**}/666??7";
		String value = "111/2222/8888/3333/abcde/eftxe/666pp7";
		System.err.println(PathMatcher.match(exp, value).isMatch());
		System.err.println(PathMatcher.match(exp, value).getTokens());
	}
}

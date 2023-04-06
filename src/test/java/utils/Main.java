package utils;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		int[] chars = new int[1024];
		
		for(int i = 0 ; i < chars.length;i++) {
			chars[i] = 0;
		}
		System.err.println(Arrays.toString(chars));
		long t = System.currentTimeMillis();
		while(chars[9] < 9) {
			getAndAdd(chars);
			System.err.println(Arrays.toString(chars));
		}
		System.err.println(Arrays.toString(chars));
		System.err.println(System.currentTimeMillis()-t);
	}

	private static int[] getAndAdd(int[] chars) {
		tryAdd(chars,0);
		return chars;
	}

	private static void tryAdd(int[] chars, int i) {
		if(chars[i] == 9) {
			chars[i] = 0 ;
			tryAdd(chars,i+1);
		}else {
			chars[i] = chars[i]+1;
		}
	}

}

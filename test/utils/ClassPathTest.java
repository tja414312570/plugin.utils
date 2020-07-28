package utils;

import java.util.Arrays;

import com.yanan.utils.ArrayUtils;
import com.yanan.utils.resource.ResourceManager;

public class ClassPathTest {
	public static void main(String[] args) {
		int[] arrays = new int[] {1,2,3,4,5,6};
		System.out.println(ArrayUtils.indexOf(arrays, 8));
		System.out.println(Arrays.toString(ArrayUtils.add(new String[] {"123","456"},"abc",2)));
		System.out.println(ResourceManager.classPath());
	}
}

package utils;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import com.yanan.utils.CacheHashMap;
import com.yanan.utils.HashMaps;
import com.yanan.utils.reflect.TypeToken;

public class HashMapTest {
	public static void main(String[] args) throws InterruptedException {
		HashMaps<String, String> maps = new HashMaps<>();
		maps.put("h", "e");
		System.out.println(maps.get("h"));
		Class<? extends Reference<?>> cls =new TypeToken<WeakReference<?>>() {}.getTypeClass();
		CacheHashMap<String, HashMapTest>  maps2 = new CacheHashMap<>(cls);
		maps2.puts("h", new HashMapTest());
		System.out.println(maps2);
		System.out.println(maps2.get("h"));
		Reference<?> softReference = new SoftReference<>( new HashMapTest());
		new Thread(()->{
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
//				System.gc();
				System.err.println("==================");
				System.out.println(maps2.get("h"));
				System.out.println(softReference.get());
//				test = null;
//				maps2.remove("h");
			}
		}).start();
//		String s = "";
//		while(true) {
//			s+=s+"iiiii";
//		}
	}
}

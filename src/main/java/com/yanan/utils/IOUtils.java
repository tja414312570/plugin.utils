package com.yanan.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	public static String toString(InputStream inputStream){
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(inputStream.available());
			while((len = inputStream.read(bytes)) != 0) {
				baos.write(bytes,0,len);
			}
			return new String(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("failed to read stream",e);
		}finally {
			close(inputStream);
		}
	}
	public static void transport(byte[] bits,OutputStream outputStream) throws IOException {
		try{
			outputStream.write(bits);
			outputStream.flush();
		}finally {
			close(outputStream);
		}
		
	}
	public static void clone(InputStream inputStream,OutputStream outputStream) throws IOException {
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			while((len = inputStream.read(bytes)) != 0) {
				outputStream.write(bytes,0,len);
			}
		} finally {
			close(inputStream);
		}
	}
	public static void close(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}
	
}
 
package com.yanan.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class IOUtils {
	public static String toString(InputStream inputStream) {
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(inputStream.available());
			while ((len = inputStream.read(bytes)) > 0) {
				baos.write(bytes, 0, len);
			}
			return new String(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("failed to read stream", e);
		} finally {
			close(inputStream);
		}
	}

	public static String toString(Reader reader) {
		char[] bytes = new char[1024];
		int len = 0;
		try {
			StringBuffer stringBuffer = new StringBuffer();
			while ((len = reader.read(bytes)) > 0) {
				stringBuffer.append(bytes, 0, len);
			}
			return stringBuffer.toString();
		} catch (IOException e) {
			throw new RuntimeException("failed to read stream", e);
		} finally {
			close(reader);
		}
	}

	public static String toString(InputStream inputStream, String charset) {
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(inputStream.available());
			while ((len = inputStream.read(bytes)) > 0) {
				baos.write(bytes, 0, len);
			}
			return new String(baos.toByteArray(), charset);
		} catch (IOException e) {
			throw new RuntimeException("failed to read stream", e);
		} finally {
			close(inputStream);
		}
	}

	public static void transport(byte[] bits, OutputStream outputStream) throws IOException {
		try {
			outputStream.write(bits);
			outputStream.flush();
		} finally {
			close(outputStream);
		}

	}

	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(bytes)) > 0) {
				outputStream.write(bytes, 0, len);
			}
		} finally {
			close(inputStream);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	public static void readLine(InputStream inputStream, LineConsumer consumer) {
		Reader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				consumer.readLine(line);
			}
		} catch (IOException e) {
			consumer.onException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}

	}

}

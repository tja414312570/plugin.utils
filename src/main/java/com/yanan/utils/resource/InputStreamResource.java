package com.yanan.utils.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 输入流资源
 * @author yanan
 *
 */
public class InputStreamResource implements Resource {
	public InputStreamResource(String name, String path, InputStream inputStream) {
		super();
		this.name = name;
		this.path = path;
		this.inputStream = inputStream;
	}

	private String name;
	private String path;
	private InputStream inputStream;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean isDirect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long lastModified() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long size() throws IOException {
		return inputStream.available();
	}

	@Override
	public List<? extends Resource> listResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

}

package com.YaNan.frame.utils.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Files 类
 * @version 1.0.1
 * @since jdk1.7
 * @author YaNan
 *
 */
public class FileUtils {
	/**
	 * filePath String 型的文件地址
	 */
	public String filePath = null;
	/**
	 * file File 型的文件地址
	 */
	public File file = null;
	/**
	 * fileEncoding 读或写文件的编码 默认utf-8
	 */
	public String fileEncoding = "utf-8";
	/**
	 * fileContent 要写入的文件内容
	 */
	public String fileContent = null;
	/**
	 * notExistsCreate 当文件不存在时新建一个文件
	 */
	public boolean notExistsCreate = true;
	/**
	 * autoCR 自动回车，该版本已禁用
	 */
	public boolean autoCR = true;

	/**
	 * Files类 调用此类你将可以更简便的操作文本文件
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * 例如: 1 Files file = new Files();
	 * file.File = new File("test.txt");
	 * file.write("hello files");
	 * 2 Files file = new File();
	 * file.File = new Files("test.txt");
	 * file.fileContent = "hello files";
	 * file.write();
	 */
	public FileUtils() {
	}

	/**
	 * Files类 需要传入一个String型的filePath 调用此类你将可以更简便的操作文本文件
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * 例如: 1 Files file = new Files("test.txt");
	 * file.write("hello files");
	 * 2 Files file = new Files("test.txt");
	 * file.fileContent = "hello files";
	 * file.write();
	 * 
	 * @param file String:file path
	 */
	public FileUtils(String file) {
		this.filePath = file;
	}

	/**
	 * Files类 需要传入一个File型的filePath 调用此类你将可以更简便的操作文本文件
	 * 如果你实例化此类后没有使用参数，你需要先对该类进行赋值
	 * 例如: 1 Files file = new Files(new File("test.txt"));
	 * file.write("hello files");
	 * 2 Files file = new Files(new File("test.txt"));
	 * file.fileContent = "hello files";
	 * file.write();
	 * 
	 * @param file File:file
	 */
	public FileUtils(File file) {
		this.file = file;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，fileContent否则抛出空指针
	 * prepend()
	 * 
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean prepend() throws IllegalAccessException, IOException{
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prepend(this.filePath, this.fileContent);
		if (this.file != null)
			return prepend(this.file, this.fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * prepend(String fileContent)
	 * 
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean prepend(String fileContent) throws IllegalAccessException, IOException{
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prepend(this.filePath, fileContent);
		if (this.file != null)
			return prepend(this.file, fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * prepend(String fileName||filePath,String fileContent)
	 * 
	 * @param fileName file name
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean prepend(String fileName, String fileContent) throws IllegalAccessException, IOException{
		return prepend(new File(fileName), fileContent);
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * prepend(File fileName||filePath,String fileContent)
	 * 
	 * @param file file
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean prepend(File file, String fileContent) throws IOException, IllegalAccessException{
		if (file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (!file.canWrite())
			throw new IllegalAccessException("Cann,t write the file");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null ;
		BufferedWriter bf = null;
		try {
			fileContent = fileContent
					+ (read(file).equals("") ? "" : read(file));
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, fileEncoding);
			bf = new BufferedWriter(osw);
			bf.write(fileContent);
		} finally {
			if(bf != null)
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(osw != null)
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(fos != null)
				fos.close();
		}
		return true;
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，fileContent否则抛出空指针
	 * prependLn()
	 * 
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean prependLn() throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prependLn(this.filePath, this.fileContent);
		if (this.file != null)
			return prependLn(this.file, this.fileContent);
		return false;
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * prependLn(String fileContent)
	 * 
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean prependLn(String fileContent) throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return prependLn(this.filePath, fileContent);
		if (this.file != null)
			return prependLn(this.file, fileContent);
		return false;
	}

	/**
	 * prepend文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * prependLn(String fileName||filePath,String fileContent)
	 * 
	 * @param fileName file name
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean prependLn(String fileName, String fileContent) throws IllegalAccessException, FileException, IOException{
		return prependLn(new File(fileName), fileContent);
	}

	/**
	 * prependLn文件写入，以文本形式写入文件,且写入的内容在原内容之前，默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * prependLn(File fileName||filePath,String fileContent)
	 * 
	 * @param file file
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean prependLn(File file, String fileContent) throws FileException, IllegalAccessException, IOException{
		if (file == null)
			throw new NullPointerException();
		if (fileContent == null)
			throw new NullPointerException();
		if (!file.canRead())
			throw new FileException("Cann,t read the file");
		BufferedWriter bf = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fileContent = fileContent
					+ (read(file).equals("") ? "" : "\n" + read(file));
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, fileEncoding);
			bf = new BufferedWriter(osw);
			bf.write(fileContent);
			
		} finally {
			if(bf != null)
				try {
					bf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(osw != null)
				try {
					osw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * reWrite()
	 * 
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean reWrite() throws IllegalAccessException, FileException, IOException{
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return reWrite(this.filePath, this.fileContent);
		if (this.file != null)
			return reWrite(this.file, this.fileContent);
		return false;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * reWrite(String content)
	 * 
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean reWrite(String fileContent) throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return reWrite(this.filePath, fileContent);
		if (this.file != null)
			return reWrite(this.file, fileContent);
		return false;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * reWrite(File fileName||filePath,String content)
	 * 
	 * @param file file
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 * @throws FileException ex
	 */
	public boolean reWrite(File file, String fileContent) throws IllegalAccessException, IOException, FileException {
		if (!file.exists()) {
			if (!file.canRead())
				throw new IllegalAccessException("Cann,t read the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					write(file, fileContent);
				} else {
					throw new FileException("File is't exists");
				}
			} catch (IOException e) {
				throw new FileException(e.toString());
			}
		} else {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bf = null;
			try {
				fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos,
						fileEncoding);
				bf = new BufferedWriter(osw);
				bf.write(fileContent);
			} finally {
				if(bf != null)
					try {
						bf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(osw != null)
					try {
						osw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} 
		}
		return true;
	}

	/**
	 * reWrite文件覆写，以文本形式覆写文件,默认编码为utf-8
	 * reWrite(String fileName||filePath,String content)
	 * 
	 * @param filePath file path
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 * @throws FileException ex
	 */
	public boolean reWrite(String filePath, String fileContent) throws IllegalAccessException, FileException, IOException{
		return reWrite(new File(filePath), fileContent);
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * write()
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @throws IOException ex
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean write() throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return write(this.filePath, this.fileContent);
		if (this.file != null)
			return write(this.file, this.fileContent);
		return false;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，否则抛出空指针
	 * write(String content)
	 * 
	 * @param fileContent file content
	 * @return boolean:true or false
	 * @throws IOException es
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean write(String fileContent) throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return write(this.filePath, fileContent);
		if (this.file != null)
			return write(this.file, fileContent);
		return false;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * write(File fileName||filePath,String content)
	 * 
	 * @param file File file
	 * @param fileContent String file content
	 * @return boolean:true or false
	 * @throws IllegalAccessException ex
	 * @throws FileException ex
	 * @throws IOException ex
	 */
	public boolean write(File file, String fileContent) throws IllegalAccessException, FileException, IOException {
		if (!file.exists()) {
			if (!file.canWrite())
				throw new IllegalAccessException("Cann,t Write the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					write(file, fileContent);
				} else {
					throw new FileException("File is't exists");
				}
			} catch (IOException e) {
				throw new FileException(e.toString());
			}
		} else {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bf = null;
			try {
				fileContent = (read(file).equals("") ? "" : read(file)
						+ (this.autoCR ? "\r\n" : ""))
						+ fileContent;
				fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos,
						fileEncoding);
				bf = new BufferedWriter(osw);
				bf.write(fileContent);
			} finally {
				if(bf != null)
					try {
						bf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if(osw != null)
					try {
						osw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				if(fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return true;
	}

	/**
	 * write文件写入，以文本形式写入文件,默认编码为utf-8
	 * write(String fileName||filePath,String content)
	 * @param filePath String:file path
	 * @param fileContent String:file content
	 * @return boolean:true or false
	 * @throws IOException ex
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 */
	public boolean write(String filePath, String fileContent) throws IllegalAccessException, FileException, IOException {
		return write(new File(filePath), fileContent);
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * 调用此方法需要先设置file或filePath，fileContent，否则抛出空指针
	 * writeLn()
	 * 
	 * @return boolean:true or false
	 * @return true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean writeLn() throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.fileContent == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return writeLn(this.filePath, this.fileContent);
		if (this.file != null)
			return writeLn(this.file, this.fileContent);
		return false;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8

	 * 调用此方法需要先设置file或filePath，否则抛出空指针

	 * writeLn(String content)
	 * 
	 * @param fileContent String:file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean writeLn(String fileContent) throws IllegalAccessException, FileException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return writeLn(this.filePath, fileContent);
		if (this.file != null)
			return writeLn(this.file, fileContent);
		return false;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * writeLn(File fileName||filePath,String content)
	 * 
	 * @param file File:java.io.file
	 * @param fileContent String:file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean writeLn(File file, String fileContent) throws FileException, IllegalAccessException, IOException{
		if (!file.exists()) {
			if (!file.canWrite())
				throw new IllegalAccessException("Cann,t Write the file");
			try {
				if (this.notExistsCreate) {
					file.createNewFile();
					writeLn(file, fileContent);
				} else {
					throw new FileException("File is't exists");
				}
			} catch (IOException e) {
				throw new FileException(e.toString());
			}
		} else {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null ;
			BufferedWriter bf = null;
			try {
				fileContent = (read(file).equals("") ? "" : read(file)
						+ (this.autoCR ? "\r\n" : ""))
						+ fileContent;
				fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos,
						fileEncoding);
				bf = new BufferedWriter(osw);
				bf.write(fileContent);
			} finally {
				if(bf != null)
					try {
						bf.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				if(osw != null)
					try {
						osw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				if(fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} 
		}
		return true;
	}

	/**
	 * writeLn文件写入，以文本形式写入文件,默认编码为utf-8
	 * writeLn(String fileName||filePath,String content)
	 * 
	 * @param filePath String:file path
	 * @param fileContent String:file content
	 * @return boolean:true or false
	 * @throws FileException ex
	 * @throws IllegalAccessException ex
	 * @throws IOException ex
	 */
	public boolean writeLn(String filePath, String fileContent) throws IllegalAccessException, FileException, IOException{
		return writeLn(new File(filePath), fileContent);
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8

	 * read(File fileName||filePath)
	 * 
	 * @param file File:file
	 * @return String text or error message
	 * @throws IOException ex
	 * @throws IllegalAccessException  ex
	 */
	public String read(File file) throws IOException, IllegalAccessException{
		StringBuffer strBuffer = new StringBuffer();
		String strLine;
		if (!file.exists())
			throw new FileNotFoundException("FileIsn'tExists");
		if (!file.canRead())
			throw new IllegalAccessException("Cann,t read the file");
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, fileEncoding);
			br = new BufferedReader(isr);
			while ((strLine = br.readLine()) != null) {
				if (strBuffer.length() > 0)
					strBuffer.append("\r\n");
				strBuffer.append(strLine);
			}
		} finally  {
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(isr != null)
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return strBuffer.toString();
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8

	 * read(String fileName||filePath);
	 * 
	 * @param fileName String:file name
	 * @return String text or error message
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public String read(String fileName) throws IllegalAccessException, IOException {
		return read(new File(fileName));
	}

	/**
	 * read文件读取，以文本形式读取文件,默认编码为utf-8

	 * read();需要先设置file或filePath，否者抛出空指针
	 * 
	 * @return String text or error message
	 * @throws IOException ex
	 * @throws IllegalAccessException ex
	 */
	public String read() throws IllegalAccessException, IOException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return read(this.filePath);
		if (this.file != null)
			return read(this.file);
		return null;
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * Attribute();需要先设置file或filePath，否者抛出空指针
	 * @return String text or error message
	 * @throws IOException ex;
	 * @throws FileException ex;
	 */
	public String attribute() throws IOException, FileException {
		if (this.filePath == null && this.file == null)
			throw new NullPointerException();
		if (this.filePath != null)
			return attribute(this.filePath);
		if (this.file != null)
			return attribute(this.file);
		throw new FileException("unKnow error");
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * Attribute(String fileName||filePath);针
	 * 
	 * @param fileName String:file name
	 * @return String text or error message
	 * @throws IOException ex
	 */
	public String attribute(String fileName) throws IOException {
		return attribute(new File(fileName));
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(this.file);
		return fos;
	}

	/**
	 * Attribute获取文件属性，以文本形式读取文件,默认编码为utf-8
	 * Attribute(File fileName||filePath);需要先设置file或filePath，否者抛出空指针
	 * 
	 * @return String text or error message
	 * @throws IOException ex
	 * @param file file
	 */
	@SuppressWarnings("deprecation")
	public String attribute(File file) throws IOException {
		StringBuffer fileInfo = new StringBuffer();
		fileInfo.append("Exists : " + file.exists());
		fileInfo.append("\nAbsolute File : " + file.getAbsoluteFile());
		fileInfo.append("\nAbsolute Path : " + file.getAbsolutePath());
		fileInfo.append("\nCanonical Path : " + file.getCanonicalPath());
		fileInfo.append("\nName : " + file.getName());
		fileInfo.append("\nPath : " + file.getPath());
		fileInfo.append("\nParent File : " + file.getParentFile());
		fileInfo.append("\nIs File : " + file.isFile());
		fileInfo.append("\nIs Hidden : " + file.isHidden());
		fileInfo.append("\nCan Execute : " + file.canExecute());
		fileInfo.append("\nCan Read : " + file.canRead());
		fileInfo.append("\nCan Write : " + file.canWrite());
		fileInfo.append("\nFile to Path : " + file.toPath());
		fileInfo.append("\nFile to URI : " + file.toURI());
		fileInfo.append("\nFile to URL : " + file.toURL());
		fileInfo.append("\nFile to String : " + file.toString());
		return fileInfo.toString();
	}

	public static byte[] getBytes(File file) {
		long len = file.length();
		if(len>Integer.MAX_VALUE)
			len = Integer.MAX_VALUE;
		byte[] bytes;
		InputStream is = null;
		try {
			bytes = new byte[(int) file.length()];
			is = new FileInputStream(file);
			is.read(bytes, 0, (int) len);
		} catch (IOException e) {
			throw new RuntimeException("failed to read file \""+file+"\"",e);
		} finally {
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					throw new RuntimeException("failed to clase file inputstream at read file \""+file+"\"",e);
				}
		}
		return bytes;
	}
	public static byte[] getBytes(String fileName) {
		File file = new File(fileName);
		if(!file.exists())
			throw new RuntimeException("could not to read file \""+file+"\"",new FileNotFoundException());
		return getBytes(file);
	}
}

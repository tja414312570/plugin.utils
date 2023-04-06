package com.yanan.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BigMappedByteBuffer{
	private RandomAccessFile randomAccessFile;
//	private long pos = 1;
	private static int FRAGMENT_BITS = 25;
	private static int MAX_FRAGMENT_LEN = 1 << FRAGMENT_BITS;
	private static int MAX_FRAGMENT_LEN_DIV = MAX_FRAGMENT_LEN - 1;
	private MappedByteBuffer[] mapperedByteBuffers;
	//数据大小
	private long size;
	//当前模式
	private boolean currentModel;
	//读写模式指针
	private long read_pos;
	private long write_pos;
	private final int MODE_WRITE = 1<<1;
	private final int MODE_READER = 1<<2;
	public BigMappedByteBuffer(RandomAccessFile randomAccessFile) throws IOException {
		this.randomAccessFile = randomAccessFile;
		this.init();
	}
	private void init() throws IOException {
		this.read_pos = this.write_pos = 1;
		long len = randomAccessFile.length();
		extract(len);
	}
	private void extract(long len) throws IOException {
		int fragments = (int)((len >> FRAGMENT_BITS )+ 1);
		int index = fragments ;
		if(mapperedByteBuffers == null) {
			mapperedByteBuffers = new MappedByteBuffer[fragments];
			index = 0;
		}else if(fragments > mapperedByteBuffers.length){
			MappedByteBuffer[] old = mapperedByteBuffers;
			mapperedByteBuffers = new MappedByteBuffer[fragments];
			for(int i = 0;i < old.length;i++) {
				mapperedByteBuffers[i] = old[i];
			}
			index = old.length;
		}
		while(index < fragments) {
			long lpos = ((long)index )*((long)MAX_FRAGMENT_LEN);
				mapperedByteBuffers[index] = this.randomAccessFile.getChannel()
		                .map(FileChannel.MapMode.READ_WRITE,lpos,MAX_FRAGMENT_LEN);
			mapperedByteBuffers[index].limit(MAX_FRAGMENT_LEN);
			index++;
		}
		
	}
//	public static void main(String[] args) throws IOException {
//		File file = new File("/Users/yanan/Public","test.text");
//		RandomAccessFile ras = new RandomAccessFile(file, "rw");
//		BigMappedByteBuffer mmb = new BigMappedByteBuffer(ras);
//		mmb.position(10737400000l);
//		for(long l = 0;l<Long.MAX_VALUE;l++) {
//			byte[] bytes = ("test:"+l+";").getBytes();
//			mmb.put(bytes);
//			System.out.println(mmb.pos+"["+MAX_FRAGMENT_LEN+"("+(mmb.pos>MAX_FRAGMENT_LEN)+")]"+"--->"+ (mmb.pos / MAX_FRAGMENT_LEN)+"==>"+(mmb.pos % MAX_FRAGMENT_LEN));
////			if((mmb.pos / MAX_FRAGMENT_LEN)>0) {
////				return;
////			}
//		}
//		System.out.println("结束");
//	}
	public byte get() {
		return get(read_pos++);
	}
	public void get(byte[] bs) throws IOException {
		for(int i = 0;i<bs.length;i++)
			bs[i] = get();
	}
	public ByteBuffer put(byte b) throws IOException {
		return put(write_pos++,b);
	}
	public void put(byte[] bs) throws IOException {
		for(byte b : bs)
			put(b);
	}
	public byte get(long index) {
//		System.err.println(index & MAX_FRAGMENT_LEN_DIV);
		return mapperedByteBuffers[(int) (index >> FRAGMENT_BITS) ].get((int) (index & MAX_FRAGMENT_LEN_DIV));
	}
	public ByteBuffer put(long index, byte b) throws IOException {
		extract(index);
		return mapperedByteBuffers[(int) (index >> FRAGMENT_BITS) ].put((int) (index & MAX_FRAGMENT_LEN_DIV),b);
		
	}
	public long position(long newPosition) {
		long old = write_pos;
		this.write_pos = newPosition;
		return old;
	}
	public boolean isDirect() {
		return mapperedByteBuffers[0].isDirect();
	}
	public long position() {
		return write_pos;
	}
}
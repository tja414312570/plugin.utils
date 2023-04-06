package com.yanan.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.yanan.utils.ByteUtils;
import com.yanan.utils.resource.ResourceManager;

/**
 * 基于文件系统的hash map，用于存储文件数据
 * 支持大型数据
 * @author yanan
 *
 */
public class HashFile {
	private File indexFile;
	private File valueFile;
	private File nodeFile;
	private RandomAccessFile indexAccess;
	private RandomAccessFile valueAccess;
	private RandomAccessFile nodeAccess;
	//index表每个数据的宽度
	private static final int BYTES_SIZE_POS = 8;
	private static final int BYTES_SIZE_LEN = 4;
	
	private final static int BYTE_KEY_INDEX = 0;
	private final static int BYTE_KEY_NODE = 1;
	private static final int BYTE_KEY_NODE_VAL_LEN = 2;
	private static final int BYTE_KEY_NODE_KEY_LEN = 3;
	private static final byte[] NULL_POS_BYTES = {0,0,0,0,0,0,0,0};
	//index表的总大小
	private static final int MAX_INDEX_LEN = 1024*1024*1024-1;
	//list表的每个数据的宽度标志位==>valuePos==>keyLen==>valueLen==>nextNodePos
	static final int NODE_BYTES_LEN = 1+8+4+4+8;
	private static final int INDEX_BYTES_LEN = 16;
	private static final byte[] NULL_POINTER_BYTES = {0,
													0,0,0,0,0,0,0,0,
													0,0,0,0,
													0,0,0,0,
													0,0,0,0,0,0,0,0};
	private static String tempDir = ResourceManager.classPath();//"/Users/yanan/Public";//
	private MappedByteBuffer indexByteBuffer;
	private BigMappedByteBuffer nodeByteBuffer;
	private BigMappedByteBuffer valueByteBuffer;
	BigMappedByteBuffer getNodeByteBuffer() {
		return nodeByteBuffer;
	}
	
	static interface IoExecutor<T>{
		void accept(T param) throws IOException;
	}
	public static <T> void try_catch(IoExecutor<T> executor,T param) {
		try {
			executor.accept(param);
		}catch(IOException exception) {
			throw new RuntimeException("failed to read buffered",exception);
		}
	}
	private Hash hashCode = (keys)->{
		 if (keys == null)
	            return 0;
        long result = 1;
        for (byte element : keys)
            result = 31 * result + element;
        return result;
	};
	
	public Hash getHashCode() {
		return hashCode;
	}
	
	public void setHashCode(Hash hash) {
		this.hashCode = hash;
	}

	static interface Hash{
		long hash(byte[] keys);
	}
//	private static ThreadLocal<byte[][]> bytePools = new ThreadLocal<>();
//	public byte[] getThreadBytes(int hash,int width) {
//		hash= hash<<8 | width;
//		byte[][] bytes = bytePools.get();
//		if(bytes==null) {
//			bytes = new byte[1 << 16-1][];
//		}
//		byte[] bytes2 = bytes[hash];
//		if(bytes2==null) {
//			bytes2 = new byte[width];
//			bytes[hash] = bytes2;
//		}
//		return bytes2;
//	}
//	private static ThreadLocal<Map<Integer,byte[]>> bytePools = new ThreadLocal<>();
//	public byte[] getThreadBytes(int hash,int width) {
//		hash= hash<<16 | width;
//		Map<Integer,byte[]> byteMap = bytePools.get();
//		if(byteMap == null) {
//			byteMap = new HashMap<>();
//			bytePools.set(byteMap);
//		}
//		byte[] bytes = byteMap.get(hash);
//		if(bytes==null) {
//			bytes = new byte[width];
//			byteMap.put(hash, bytes);
//		}
//		return bytes;
//	}
	private final static ThreadLocal<byte[][]> bytePools = new ThreadLocal<byte[][]>() {
		protected byte[][] initialValue() {
			byte[][] init = new byte[128][];
			for(int i = 0;i<4;i++) {
				init[(4<<i)  ] = new byte[BYTES_SIZE_LEN];
				init[(4<<i) +1 ] = new byte[BYTES_SIZE_POS];
				init[(4<<i) +2 ] = new byte[INDEX_BYTES_LEN];
				init[(4<<i) +3 ] = new byte[NODE_BYTES_LEN];
			}
			return init;
		};
	};
	static class ValueInfo{
		private long pos;
		private int keyLength;
		private int valueLength;
		public long getPos() {
			return pos;
		}
		public void setPos(long pos) {
			this.pos = pos;
		}
		public int getKeyLength() {
			return keyLength;
		}
		public void setKeyLength(int keyLength) {
			this.keyLength = keyLength;
		}
		public int getValueLength() {
			return valueLength;
		}
		public void setValueLength(int valueLength) {
			this.valueLength = valueLength;
		}
	}
	private final static ThreadLocal<ValueInfo> valueInfoPools = new ThreadLocal<ValueInfo>() {
		protected ValueInfo initialValue() {
			return new ValueInfo();
		};
	};
	private static final int NODE_COVERY = 1;
	private static final int NODE_NEW = 0;
	private static final int NODE_LINK = 2;
	public ValueInfo createValueInfo(long pos,int keyLength,int valueLength) {
		ValueInfo valueInfo = valueInfoPools.get();
		valueInfo.setKeyLength(keyLength);
		valueInfo.setValueLength(valueLength);
		valueInfo.setPos(pos);
		return valueInfo;
	}
	private final byte[] getThreadBytes(int hash,int width) {
		int pos = 4 << hash;
		byte[][] init = bytePools.get();
		switch (width) {
		case BYTES_SIZE_LEN:
			return init[pos];
		case BYTES_SIZE_POS:
			return init[pos+1];
		case INDEX_BYTES_LEN:
			return init[pos+2];
		case NODE_BYTES_LEN:
			return init[pos+3];
		default:
			break;
		}
		return null;
	}
	public HashFile(){
		this(UUID.randomUUID().toString());
	}
	public HashFile(String dir,String name){
		this.indexFile = new File(dir , name + ".index");
		this.valueFile = new File(dir, name + ".value");
		this.nodeFile = new File(dir, name + ".node");
		init();
	}
	public HashFile(String name){
		this(tempDir,name);
	}
	public void init(){
		try_catch((param)->{
			this.indexAccess = new RandomAccessFile(indexFile, "rw");
			indexAccess.setLength(MAX_INDEX_LEN);
			this.valueAccess = new RandomAccessFile(valueFile, "rw");
			this.nodeAccess = new RandomAccessFile(nodeFile, "rw");
			this.indexByteBuffer = indexAccess.getChannel()
	                .map(FileChannel.MapMode.READ_WRITE, 0, indexAccess.length());
			this.valueByteBuffer = new BigMappedByteBuffer(valueAccess);
			this.nodeByteBuffer = new BigMappedByteBuffer(nodeAccess);
		},null);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.indexFile.delete();
			this.valueFile.delete();
			this.nodeFile.delete();
		}));
	}
	public IteratorHashNode entrySet(){
		IteratorHashNode node = readNode(1l,new IteratorHashNode(this));
		return node;
	}
	public void forEach(BiConsumer<byte[],byte[]> action) {
		forEach(node->{
			action.accept(node.getKey(), node.getValue());
		});
	}
	public void forEach(Consumer<HashNode> action) {
		HashNode node = this.entrySet();
		while(node != null) {
			action.accept(node);
			node = node.nextNode();
		}
	}
	private IoExecutor<byte[]> writeValue = (bodyBytes)->{
		valueByteBuffer.put(bodyBytes);
	};
	public ValueInfo writeValue(byte[] keyBytes,byte[] valueBytes){
		byte[] bodyBytes = new byte[valueBytes.length+keyBytes.length];
		System.arraycopy(keyBytes, 0, bodyBytes, 0, keyBytes.length);
		System.arraycopy(valueBytes, 0, bodyBytes, keyBytes.length, valueBytes.length);
		int bodyLen = bodyBytes.length;
		long posValue = valueByteBuffer.position();
		try_catch(writeValue,bodyBytes);
		return createValueInfo(posValue, keyBytes.length,bodyLen);
	}
	public void put(byte[] keyBytes, byte[] valueBytes){
		long hash = hashCode.hash(keyBytes);
		System.err.println("===========");
		System.err.println(hash);
		//获取节点
		HashNode node = getNode(hash,null);
		HashNode lastNode = null;
		//节点不存在
		if(node != null) {
			lastNode = node.getLast();
			while(node != null) {
				if(Arrays.equals(keyBytes, getNodeKey(node))) {
					ValueInfo valueInfo = writeValue(keyBytes, valueBytes);
					writeNode(valueInfo,node.getNodePos(),NODE_COVERY,-1);
					return ;
				}
				node = node.nextNode();
			}
			ValueInfo valueInfo = writeValue(keyBytes, valueBytes);
			long nodePos = writeNode(valueInfo,-1,NODE_NEW,-1);
			if(nodePos==lastNode.getNodePos()) 
				throw new RuntimeException("an unexpected exception appears ["+nodePos+"]");
			nodePos = writeNode(valueInfo,lastNode.getNodePos(),NODE_LINK,nodePos);
			return ;
		}
		ValueInfo valueInfo = writeValue(keyBytes, valueBytes);
		long nodePos = writeNode(valueInfo,-1,NODE_NEW,-1);
		long posIndex = getPosIndex(hash);
		byte[] indexBytes = getIndexBytes(NODE_BYTES_LEN, nodePos);
		indexByteBuffer.position( (int) posIndex);
		indexByteBuffer.put(indexBytes);
	}
	private IoExecutor<byte[]> writeNode = (nodeBytes)->{
		nodeByteBuffer.put(nodeBytes);
	};
	private long writeNode(ValueInfo valueInfo,long pos,int operate, long nextNode){
		byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
		if(operate != NODE_NEW) 
			getByteBuffer(nodeBytes,pos,nodeByteBuffer);
		byte[] valuePosBytes = getThreadBytes(BYTE_KEY_NODE,BYTES_SIZE_POS);
		if(operate != NODE_LINK) {
			byte[] keyLenBytes = getThreadBytes(BYTE_KEY_NODE_KEY_LEN,BYTES_SIZE_LEN);
			byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
			ByteUtils.longToBytes(valueInfo.pos,valuePosBytes);
			ByteUtils.intToBytes(valueInfo.valueLength,valueLenBytes);
			ByteUtils.intToBytes(valueInfo.keyLength,keyLenBytes);
			nodeBytes[0] = 1;
			System.arraycopy(valuePosBytes, 0, nodeBytes, 1, 8);
			System.arraycopy(valueLenBytes, 0, nodeBytes, 9, 4);
			System.arraycopy(keyLenBytes, 0, nodeBytes, 13, 4);
		}
		if(operate == NODE_NEW) {
			System.arraycopy(NULL_POS_BYTES, 0, nodeBytes, 17, 8);
		}else if (operate == NODE_LINK) {
			ByteUtils.longToBytes(nextNode,valuePosBytes);
			System.arraycopy(valuePosBytes, 0, nodeBytes, 17, 8);
		}
		if(pos == -1) {
			pos = nodeByteBuffer.position();
			try_catch(writeNode, nodeBytes);
		}else {
			long oldPos = this.nodeByteBuffer.position();
			this.nodeByteBuffer.position(pos);
			try_catch(writeNode, nodeBytes);
			this.nodeByteBuffer.position(oldPos);
		}
		return pos;
	}
	public HashNode getNode(byte[] keyBytes){
		long hash = hashCode.hash(keyBytes);
		//获取key的值
		return getNode(hash,keyBytes);
	}
	final int maxNodePoolsSize = 1024*1024;
	final HashNode[] nodePools = new HashNode[maxNodePoolsSize];
	private AtomicInteger nodePos = new AtomicInteger(0);
	public HashNode getPoolsNode(long pos) {
		HashNode node = nodePools[nodePos.get()];
		if(node == null) {
			node = new HashNode(this);
			nodePools[nodePos.get()] = node;
		}else {
			node.setBefore(null);
			node.setHashCode(0L);
			node.setKeyLength(0);
			node.setMark(0);
			node.setNext(null);
			node.setNextPos(0L);
			node.setValueLength(0);
			node.setValuePos(0L);
		}
		if(nodePos.incrementAndGet() >= maxNodePoolsSize) {
			nodePos.set(0);
		}
		return node;
	}
	public HashNode getNode(long hashCode,byte[] keyBytes){
		HashNode hashNode = null;
		long nodePos = getNodePos(hashCode);
		while(nodePos >= 1) {
			HashNode node = null;
			//拿到第一个数据的位置
			if(keyBytes == null) {
				node = getPoolsNode(nodePos);
			}else {
				node = new HashNode(this);
			}
			readNode(nodePos,node);
			node.setHashCode(hashCode);
			nodePos = node.getNextPos();
			if(keyBytes != null ) {
				byte[] nodeKey = getNodeKey(node);
				if(Arrays.equals(keyBytes, nodeKey))
					return node;
			} else {
				if(hashNode == null) {
					hashNode = node;
				}else {
					hashNode.getLast().setNext(node);
					node.setBefore(hashNode.getLast());
				}
			}
		}
		if(hashNode(hashNode)>5) {
			HashNode node = hashNode;
			while(node != null) {
				node = node.nextNode(); 
			}
		}
		return hashNode;
	}
	public int hashNode(HashNode node) {
		int i = 0;
		while(node != null) {
			i ++;
			node = node.nextNode();
		}
		return i;
	}
	public <T extends HashNode> T readNode(long nodePos,T node){
			byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, NODE_BYTES_LEN);
			getByteBuffer(bytes,nodePos,nodeByteBuffer);
			//节点标志
			int mark = bytes[0] & 0xff;
			//值的指针
			long valuePos = ByteUtils.bytesToLong(bytes,1);
			//值的总长度
			int valueLength = ByteUtils.bytesToInt(bytes,9);
			//值的key的长度
			int keyLength = ByteUtils.bytesToInt(bytes,13);
			//下一个hash值相同的指针
			node.setKeyLength(keyLength);
			node.setNodePos(nodePos);
			node.setMark(mark);
			node.setValueLength(valueLength);
			node.setValuePos(valuePos);
			long nextPos = ByteUtils.bytesToLong(bytes,17);
			if(nextPos>nodeByteBuffer.position()-NODE_BYTES_LEN) {
				nextPos = -1;
			}
			node.setNextPos(nextPos);
		return node;
	}
	private byte[] getValue(long pos,int length){
		byte[] bytes = new byte[length];
		getByteBuffer(bytes,pos,valueByteBuffer);
		return bytes;
	}
	public byte[] getNodeValue(HashNode node){
		return getValue(node.getValuePos()+node.getKeyLength(),node.getValueLength()-node.getKeyLength());
	}
	public byte[] getNodeKey(HashNode node){
		return getValue(node.getValuePos(),node.getKeyLength());
	}
	private long getNodePos(long hashCode){
		long posIndex = getPosIndex(hashCode);
		byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, INDEX_BYTES_LEN);
		int i= 0;
		int len =  (int) (posIndex+bytes.length);
		for(;posIndex < len;) {
			try {
				bytes[i++] = this.indexByteBuffer.get( (int) posIndex++);
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return ByteUtils.bytesToLong(bytes, 0);
		
	}

	private byte[] getByteBuffer(byte[] bytes,long pos,BigMappedByteBuffer byteBuffer){
			int i= 0;
			long poss = pos;
			long len =  (poss+bytes.length);
			for(;poss < len;) {
				bytes[i++] = byteBuffer.get(poss++);
			}
		return bytes;
		
	}
	@SuppressWarnings("unused")
	private long getByteBuffer(byte[] bytes,int pos,MappedByteBuffer byteBuffer){
		int i= 0;
		int len =  pos+bytes.length;
		for(;pos < len;) {
			bytes[i++] = byteBuffer.get( pos++);
		}
		return ByteUtils.bytesToInt(bytes, 0);
	}
	
	private byte[] getIndexBytes(int len, long pos) {
		byte[] bytes = getThreadBytes(BYTE_KEY_INDEX, INDEX_BYTES_LEN);
		byte[] lens = getThreadBytes(BYTE_KEY_INDEX, BYTES_SIZE_LEN);
		ByteUtils.intToBytes(len,lens);
		byte[] poss = getThreadBytes(BYTE_KEY_INDEX, BYTES_SIZE_POS);
		ByteUtils.longToBytes(pos,poss);
		System.arraycopy(poss, 0, bytes, 0, poss.length);
		System.arraycopy(lens, 0, bytes, poss.length, lens.length);
		return bytes;
	}

	public byte[] get(byte[] keyBytes){
		HashNode node = getNode(keyBytes);
		return node==null?null:getNodeValue(node);
	}
	public byte[] remove(byte[] keyBytes){
		long hash = hashCode.hash(keyBytes);
		HashNode node = getNode(hash,null);
		HashNode before = null;
		HashNode current = null;
		while(node != null) {
			byte[] nodeKey = getNodeKey(node);
			if(Arrays.equals(keyBytes, nodeKey)) {
				current = node;
				break;
			}
			before = node;
			node = node.nextNode();
		}
		long oldPos = this.nodeByteBuffer.position();
		try {
			if(node != null) {
				if(before != null) {
					byte[] nodeBytes = getThreadBytes(BYTE_KEY_NODE,NODE_BYTES_LEN);
					if(!current.hasNext()) {
						//删掉上个节点的下一个节点指针
						getByteBuffer(nodeBytes,before.getNodePos(),nodeByteBuffer);
						System.arraycopy(NULL_POS_BYTES, 0, nodeBytes, 17, 8);
					}else {
						//将上一节点的下一指针指向当前节点的下一个指针
						byte[] valueLenBytes = getThreadBytes(BYTE_KEY_NODE_VAL_LEN,BYTES_SIZE_LEN);
						ByteUtils.longToBytes(current.nextNode().getNodePos(),valueLenBytes);
						System.arraycopy(valueLenBytes, 0, nodeBytes, 17, 8);
					}
					this.nodeByteBuffer.position( before.getNodePos());
					try_catch(writeNode, nodeBytes);
				}
				this.nodeByteBuffer.position(current.getNodePos());
				try_catch(writeNode, NULL_POINTER_BYTES);
			}
		}finally {
			 this.nodeByteBuffer.position(oldPos);
		}
		return node==null?null:getNodeValue(node);
	}
	public void removeAll(){
		this.indexFile.delete();
		this.nodeFile.delete();
		this.valueFile.delete();
		init();
	}
	public void destory(){
		this.indexFile.delete();
		this.nodeFile.delete();
		this.valueFile.delete();
	}
	private long getPosIndex(long hash) {
		long pos = ((hash & (MAX_INDEX_LEN/INDEX_BYTES_LEN)))*INDEX_BYTES_LEN;
		return pos > MAX_INDEX_LEN-INDEX_BYTES_LEN?0:pos;
	}

	
}

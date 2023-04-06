package com.yanan.utils;

public class HashNode {
	protected HashFile hashFile;
	protected HashNode next;
	protected HashNode before;
	protected int mark;
	// 值的指针
	protected long valuePos;
	// 值的总长度
	protected int valueLength;
	// 值的key的长度
	protected int keyLength;
	// 下一个hash值相同的指针
	protected long nodePos;
	// 下一个节点指针
	protected long nextPos;
	
	protected long hashCode;

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public long getValuePos() {
		return valuePos;
	}

	public void setValuePos(long valuePos) {
		this.valuePos = valuePos;
	}

	public int getValueLength() {
		return valueLength;
	}

	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public long getNodePos() {
		return nodePos;
	}

	public void setNodePos(long nodePos) {
		this.nodePos = nodePos;
	}

	public HashNode(HashFile hashFile) {
		super();
		this.hashFile = hashFile;
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public HashNode nextNode() {
		return next;
	}

	public HashNode getNext() {
		return next;
	}

	public void setNext(HashNode next) {
		this.next = next;
	}

	public HashNode getLast() {
		return this.hasNext() ? this.nextNode().getLast() : this;
	}

	@Override
	public String toString() {
		return "HashNode [hashFile=" + hashFile.getHashCode() + ", next=" + next + ", before=" + before + ", mark=" + mark
				+ ", valuePos=" + valuePos + ", valueLength=" + valueLength + ", keyLength=" + keyLength + ", nodePos="
				+ nodePos + ", nextPos=" + nextPos + ", hashCode=" + hashCode + "]";
	}

	public HashNode getBefore() {
		return before;
	}

	public void setBefore(HashNode before) {
		this.before = before;
	}

	public HashFile getHashFile() {
		return hashFile;
	}

	public long getNextPos() {
		return nextPos;
	}

	public void setNextPos(long nextPos) {
		this.nextPos = nextPos;
	}

	public long getHashCode() {
		return hashCode;
	}

	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
	}

	public byte[] getKey() {
		return this.hashFile.getNodeKey(this);
	}
	public byte[] getValue() {
		return this.hashFile.getNodeValue(this);
	}
}

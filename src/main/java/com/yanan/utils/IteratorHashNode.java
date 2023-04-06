package com.yanan.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class IteratorHashNode extends HashNode{
//	private BigMappedByteBuffer nodeByteBuffered;
	public volatile boolean found;
	void found() {
		if(!this.found) {
			synchronized (this) {
				long pos = this.nodePos;
				AtomicBoolean loop = new AtomicBoolean(true);
//				while(loop.get() && (pos+=HashFile.NODE_BYTES_LEN) <= this.nodeByteBuffered.position()+1) {
//					HashNode node = this.hashFile.readNode(pos, new IteratorHashNode(hashFile));
//					if(node.getMark() > 0) {
//						this.next = node;
//						this.nextPos = node.getNodePos();
//						loop.set(false);
//					}
//				}
				while(loop.get()) {
					pos+=HashFile.NODE_BYTES_LEN;
					HashNode node = this.hashFile.readNode(pos, new IteratorHashNode(hashFile));
					if(node.getMark() > 0) {
						this.next = node;
						this.nextPos = node.getNodePos();
						loop.set(false);
					}else {
						break;
					}
				}
				found = true;
			}
		}
	}
	public IteratorHashNode(HashFile hashFile) {
		super(hashFile);
//		this.nodeByteBuffered = hashFile.getNodeByteBuffer();
	}
	public boolean hasNext() {
		found();
		return super.hasNext();
	}
	public HashNode nextNode() {
		found();
		return super.nextNode();
	}

}

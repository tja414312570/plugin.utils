package com.yanan.utils;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import com.yanan.utils.reflect.TypeToken;

/**
 * 缓存表，使用java的引用原理实现 
 * ！！！对字符串无效
 * @author yanan
 * @param <K>
 * @param <V>
 */
public class CacheHashMap<K, V> extends HashMaps<Object, Object> {
	Class<? extends Reference<K>> referenceKeyClass;
	Class<? extends Reference<V>> referenceValClass;
	/**
	 * 
	 */
	private static final long serialVersionUID = -270398289030880480L;
	/**
	 * 默认构造器，使用软引用作为引用类型
	 */
	public CacheHashMap() {
		this(new TypeToken<SoftReference<Object>>() {
		}.getTypeClass());
	}
	/**
	 * 使用一个引用类型作为缓存的引用类型
	 * @param reference 引用类
	 */
	public CacheHashMap(Class<? extends Reference<?>> reference) {
		this(reference, reference);
	}
	/**
	 * 分别是用引用类型对缓存的key和value进行设置
	 * @param keyReferenceClass
	 * @param valReferenceClass
	 */
	@SuppressWarnings("unchecked")
	public CacheHashMap(Class<? extends Reference<?>> keyReferenceClass,
			Class<? extends Reference<?>> valReferenceClass) {
		this.referenceKeyClass = (Class<? extends Reference<K>>) keyReferenceClass;
		this.referenceValClass = (Class<? extends Reference<V>>) valReferenceClass;
	}
	/**
	 * 不支持次方法
	 * 因为不能对key和value进行限制
	 */
	@Override
	public V put(Object key,Object value) {
		throw new UnsupportedOperationException("please use [puts] method");
	}
	/**
	 * 用来代替put方法
	 * @param key map的key
	 * @param value map的value
	 * @return 旧值
	 */
	public V puts(K key, V value) {
		return putVals(hash(key), key, value, false, true);
	}
	/**
	 * 用以替换putVal方法
	 * @param hash hash值
	 * @param key key
	 * @param value value
	 * @param onlyIfAbsent 不管
	 * @param evict 不管
	 * @return 旧值
	 */
	@SuppressWarnings("unchecked")
	final V putVals(int hash, Object key, Object value, boolean onlyIfAbsent, boolean evict) {
		if (key == null || value == null)
			throw new IllegalArgumentException("key or value is null");
		Object refValue = objToReference(this.referenceValClass, value);
		Node<Object, Object>[] tab;
		Node<Object, Object> p;
		int n, i;
		if ((tab = table) == null || (n = tab.length) == 0)
			n = (tab = resize()).length;
		if ((p = tab[i = (n - 1) & hash]) == null) {
			Object refKey = objToReference(this.referenceKeyClass, key);
			tab[i] = newNode(hash, refKey, refValue, null);
		} else {
			Node<Object, Object> e = null;
			K k;
			if (p.hash == hash && ((k = (K) referenceToObj(this.referenceKeyClass, p.key)) == key
					|| (key != null && key.equals(k))))
				e = p;
			else if (p instanceof TreeNode) {
				e = ((TreeNode<Object, Object>) p).putTreeVal(this, tab, hash, key, value);
			} else {
				for (int binCount = 0;; ++binCount) {
					if ((e = p.next) == null) {
						Object refKey = objToReference(this.referenceKeyClass, key);
						p.next = newNode(hash, refKey, refValue, null);
						if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
							treeifyBins(tab, hash);
						break;
					}
					if (e.hash == hash && ((k = (K) referenceToObj(this.referenceKeyClass, p.key)) == key
							|| (key != null && key.equals(k))))
						break;
					p = e;
				}
			}
			if (e != null) { // existing mapping for key
				V oldValue = (V) referenceToObj(this.referenceValClass, e.value);
				if (!onlyIfAbsent || oldValue == null)
					e.value = refValue;
				afterNodeAccess(e);
				return oldValue;
			}
		}
		++modCount;
		if (++size > threshold)
			resize();
		afterNodeInsertion(evict);
		return null;
	}
	/**
	 * 依据treeifyBins方法，用于处理红黑树节点 
	 * @param tab 节点表
	 * @param hash hash值
	 */
	final void treeifyBins(Node<Object, Object>[] tab, int hash) {
		int n, index;
		Node<Object, Object> e;
		if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
			resize();
		else if ((e = tab[index = (n - 1) & hash]) != null) {
			TreeNode<Object, Object> hd = null, tl = null;
			do {
				TreeNode<Object, Object> p = replacementTreeNode(e, null);
				if (tl == null)
					hd = p;
				else {
					p.prev = tl;
					tl.next = p;
				}
				tl = p;
			} while ((e = e.next) != null);
			if ((tab[index] = hd) != null)
				hd.treeify(tab);
		}
	}
	
	/**
	 * 依据父类replaceMentTreeNode方法
	 */
	TreeNode<Object, Object> replacementTreeNode(Node<Object, Object> p, Node<Object, Object> next) {
		return new CacheTreeNode(p.hash, p.key, p.value, next,this);
	}
	/**
	 * 参考父类 get 方法
	 */
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		Node<Object, Object> e;
		return (e = getNodes(hash(key), key)) == null ? null : (V) referenceToObj(this.referenceValClass, e.value);
	}
	/**
	 * 参考父类remove方法
	 */
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		Node<Object, Object> e;
		return (e = removeNode(hash(key), key, null, false, true)) == null ? null : (V) referenceToObj(this.referenceValClass, e.value);
	}
	/**
	 * 参考父类 remove node 方法
	 */
	Node<Object, Object> removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
		Node<Object, Object>[] tab;
		Node<Object, Object> p;
		int n, index;
		if ((tab = table) != null && (n = tab.length) > 0 && (p = tab[index = (n - 1) & hash]) != null) {
			Node<Object, Object> node = null, e;
			Object k;
			Object v;
			if (p.hash == hash && ((k = referenceToObj(referenceKeyClass, p.key)) == key || (key != null && key.equals(k))))
				node = p;
			else if ((e = p.next) != null) {
				if (p instanceof TreeNode)
					node = ((TreeNode<Object, Object>) p).getTreeNode(hash, key);
				else {
					do {
						if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
							node = e;
							break;
						}
						p = e;
					} while ((e = e.next) != null);
				}
			}
			if (node != null && (!matchValue || (v = node.value) == value || (value != null && value.equals(v)))) {
				if (node instanceof TreeNode)
					((TreeNode<Object, Object>) node).removeTreeNode(this, tab, movable);
				else if (node == p)
					tab[index] = node.next;
				else
					p.next = node.next;
				++modCount;
				--size;
				afterNodeRemoval(node);
				return node;
			}
		}
		return null;
	}
	/**
	 * 获取节点方法
	 * @param hash 节点hash值
	 * @param key 节点的key
	 * @return 节点
	 */
	@SuppressWarnings("unchecked")
	Node<Object, Object> getNodes(int hash, Object key) {
		Node<Object, Object>[] tab;
		Node<Object, Object> first, e;
		int n;
		K k;
		if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
			if (first.hash == hash && // always check first node
					((k = (K) referenceToObj(this.referenceKeyClass, first.key)) == key
							|| (key != null && key.equals(k))))
				return first;
			if ((e = first.next) != null) {
				if (first instanceof TreeNode) {
					return ((TreeNode<Object, Object>) first).getTreeNode(hash, key);
				}
				do {
					if (e.hash == hash && ((k = (K) referenceToObj(this.referenceKeyClass, e.key)) == key
							|| (key != null && key.equals(k))))
						return e;
				} while ((e = e.next) != null);
			}
		}
		return null;
	}
	/**
	 * 引用到对象
	 * @param <M> 返回类型
	 * @param <N> 引用类型
	 * @param referenceClass 引用类实现
	 * @param ref 应用对象
	 * @return 对象
	 */
	@SuppressWarnings({ "unchecked" })
	private <M, N extends Reference<?>> M referenceToObj(Class<N> referenceClass, Object ref) {
		if (referenceClass != null)
			return (M) ((Reference<?>) ref).get();
		return (M) ref;
	}
	/**
	 * 对象转引用
	 * @param <N> 引用类型
	 * @param referenceClass 引用类实现
	 * @param obj 原始对象
	 * @return 引用对象
	 */
	private <N extends Reference<?>> Object objToReference(Class<N> referenceClass, Object obj) {
		if (referenceClass == null)
			return obj;
		return getRefernce(this.referenceKeyClass, obj);
	}
	/**
	 * 将对象转引用
	 * @param <N> 引用类型
	 * @param referenceClass 引用类实现
	 * @param object 原始对象
	 * @return 引用对象
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <N extends Reference<?>> N getRefernce(Class<N> referenceClass, Object object) {
		if (referenceClass == null)
			throw new IllegalArgumentException("reference class is null");
		// 软引用
		if (referenceClass.equals(WeakReference.class)) {
			return (N) new WeakReference(object);
		}
		// 弱引用
		if (referenceClass.equals(SoftReference.class)) {
			return (N) new SoftReference(object);
		}
		throw new UnsupportedOperationException("the reference class is not support " + referenceClass);
	}

	@Override
	public String toString() {
		return "CacheHashMap [referenceKeyClass=" + referenceKeyClass + ", referenceValClass=" + referenceValClass + "]"
				+ super.toString();
	}
	/**
	 * 
	 * @author yanan
	 *
	 */
	static class CacheTreeNode extends TreeNode<Object, Object> {
		private CacheHashMap<?, ?> maps;

		CacheTreeNode(int hash, Object key, Object val, Node<Object, Object> next, CacheHashMap<?, ?> maps) {
			super(hash, key, val, next);
			this.maps = maps;
		}

		/**
		 * Forms tree of the nodes linked from this node.
		 */
		final void treeify(Node<Object, Object>[] tab) {
			TreeNode<Object, Object> root = null;
			for (TreeNode<Object, Object> x = this, next; x != null; x = next) {
				next = (TreeNode<Object, Object>) x.next;
				x.left = x.right = null;
				if (root == null) {
					x.parent = null;
					x.red = false;
					root = x;
				} else {
					Object k = maps.referenceToObj(maps.referenceKeyClass, x.key);
					int h = x.hash;
					Class<?> kc = null;
					for (TreeNode<Object, Object> p = root;;) {
						int dir, ph;
						Object pk = maps.referenceToObj(maps.referenceKeyClass, p.key);
						if ((ph = p.hash) > h)
							dir = -1;
						else if (ph < h)
							dir = 1;
						else if ((kc == null && (kc = comparableClassFor(k)) == null)
								|| (dir = compareComparables(kc, k, pk)) == 0)
							dir = tieBreakOrder(k, pk);

						TreeNode<Object, Object> xp = p;
						if ((p = (dir <= 0) ? p.left : p.right) == null) {
							x.parent = xp;
							if (dir <= 0)
								xp.left = x;
							else
								xp.right = x;
							root = balanceInsertion(root, x);
							break;
						}
					}
				}
			}
			moveRootToFront(tab, root);
		}

		final TreeNode<Object, Object> putTreeVal(HashMaps<Object, Object> map, Node<Object, Object>[] tab, int h,
				Object k, Object v) {
			Class<?> kc = null;
			boolean searched = false;
			TreeNode<Object, Object> root = (parent != null) ? root() : this;
			for (TreeNode<Object, Object> p = root;;) {
				int dir, ph;
				Object pk;
				if ((ph = p.hash) > h)
					dir = -1;
				else if (ph < h)
					dir = 1;
				else if ((pk = maps.referenceToObj(maps.referenceKeyClass, p.key)) == k || (k != null && k.equals(pk)))
					return p;
				else if ((kc == null && (kc = comparableClassFor(k)) == null)
						|| (dir = compareComparables(kc, k, pk)) == 0) {
					if (!searched) {
						TreeNode<Object, Object> q, ch;
						searched = true;
						if (((ch = p.left) != null && (q = ch.find(h, k, kc)) != null)
								|| ((ch = p.right) != null && (q = ch.find(h, k, kc)) != null))
							return q;
					}
					dir = tieBreakOrder(k, pk);
				}

				TreeNode<Object, Object> xp = p;
				if ((p = (dir <= 0) ? p.left : p.right) == null) {
					Node<Object, Object> xpn = xp.next;
					TreeNode<Object, Object> x = map.newTreeNode(h, k, v, xpn);
					if (dir <= 0)
						xp.left = x;
					else
						xp.right = x;
					xp.next = x;
					x.parent = x.prev = xp;
					if (xpn != null)
						((TreeNode<Object, Object>) xpn).prev = x;
					moveRootToFront(tab, balanceInsertion(root, x));
					return null;
				}
			}
		}

		final TreeNode<Object, Object> find(int h, Object k, Class<?> kc) {
			TreeNode<Object, Object> p = this;
			do {
				int ph, dir;
				Object pk;
				TreeNode<Object, Object> pl = p.left, pr = p.right, q;
				if ((ph = p.hash) > h)
					p = pl;
				else if (ph < h)
					p = pr;
				else if ((pk = maps.referenceToObj(maps.referenceKeyClass, p.key)) == k || (k != null && k.equals(pk)))
					return p;
				else if (pl == null)
					p = pr;
				else if (pr == null)
					p = pl;
				else if ((kc != null || (kc = comparableClassFor(k)) != null)
						&& (dir = compareComparables(kc, k, pk)) != 0)
					p = (dir < 0) ? pl : pr;
				else if ((q = pr.find(h, k, kc)) != null)
					return q;
				else
					p = pl;
			} while (p != null);
			return null;
		}

		final TreeNode<Object, Object> getTreeNode(int h, Object k) {
			return ((parent != null) ? root() : this).find(h, k, null);
		}
	}
}

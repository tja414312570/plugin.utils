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

	public CacheHashMap() {
		this(new TypeToken<SoftReference<Object>>() {
		}.getTypeClass());
	}

	public CacheHashMap(Class<? extends Reference<?>> reference) {
		this(reference, reference);
	}

	@SuppressWarnings("unchecked")
	public CacheHashMap(Class<? extends Reference<?>> keyReferenceClass,
			Class<? extends Reference<?>> valReferenceClass) {
		this.referenceKeyClass = (Class<? extends Reference<K>>) keyReferenceClass;
		this.referenceValClass = (Class<? extends Reference<V>>) valReferenceClass;
	}
	@Override
	public V put(Object key,Object value) {
		throw new UnsupportedOperationException("please use [puts] method");
	}
	public V puts(K key, V value) {
		return putVals(hash(key), key, value, false, true);
	}

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
				/**
				 * 这里待处理
				 */
//				System.err.println("待处理");
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

	TreeNode<Object, Object> replacementTreeNode(Node<Object, Object> p, Node<Object, Object> next) {
		return new TreeNode<>(p.hash, p.key, p.value, next);
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		Node<Object, Object> e;
		return (e = getNodes(hash(key), key)) == null ? null : (V) referenceToObj(this.referenceValClass, e.value);
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		Node<Object, Object> e;
		return (e = removeNode(hash(key), key, null, false, true)) == null ? null : (V) referenceToObj(this.referenceValClass, e.value);
	}

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
//					System.err.println("战未处理");
//                	return null;
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

	@SuppressWarnings({ "unchecked" })
	private <M, N extends Reference<?>> M referenceToObj(Class<N> referenceClass, Object ref) {
		if (referenceClass != null)
			return (M) ((Reference<?>) ref).get();
		return (M) ref;
	}

	private <N extends Reference<?>> Object objToReference(Class<N> referenceClass, Object obj) {
		if (referenceClass == null)
			return obj;
		return getRefernce(this.referenceKeyClass, obj);
	}

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
			return (N) new WeakReference(object);
		}
		throw new UnsupportedOperationException("the reference class is not support " + referenceClass);
	}

	@Override
	public String toString() {
		return "CacheHashMap [referenceKeyClass=" + referenceKeyClass + ", referenceValClass=" + referenceValClass + "]"
				+ super.toString();
	}

}

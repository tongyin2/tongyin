package lab8;

import java.util.*;

/**
 * Created by Tong Yin on 3/20/2017.
 */
public class BSTMap<K extends Comparable<K> , V> implements Map61B<K,V>{

    private Node root;

    private class Node {
        private K key;
        private V value;
        private int N;
        private Node left;
        private Node right;

        public Node(K k, V v) {
            key = k;
            value = v;
            N = 1;
            left = null;
            right = null;
        }
    }

    public BSTMap() {

    }

    @Override
    /* Removes all of the mappings from this map. */
    public void clear() {
        root = null;
    }

    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return get(key) != null;
    }

    @Override
    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return get(root,key);
    }

    private V get(Node n, K key) {
        if (key == null) {
            throw new IllegalArgumentException("the key enterted is null");
        }
        if (n == null) {
            return null;
        }
        int result = key.compareTo(n.key);
        if (result > 0) {
            return get(n.right,key);
        }else if (result < 0) {
            return get(n.left, key);
        }else {
            return n.value;
        }
    }

    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size(root);
    }

    private int size(Node n) {
        if (n == null) {
            return 0;
        }else {
            return n.N;
        }
    }

    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        root = put(root,key,value);
    }

    private Node put(Node n, K key, V value) {
        if (n == null) {
            return new Node(key,value);
        }
        if (key.compareTo(n.key) > 0) {
            n.right = put(n.right,key,value);
        }else if (key.compareTo(n.key) < 0) {
            n.left = put(n.left,key,value);
        }else {
            n.value = value;
        }
        n.N = size(n.left)+size(n.right)+1;
        return n;
    }

    @Override
    /* Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        return keySet(root);
    }

    private Set<K> keySet(Node n) {
        if (n == null) {
            return new TreeSet<K>();
        }
        TreeSet<K> ts = new TreeSet<>();
        ts.add(n.key);
        ts.addAll(keySet(n.left));
        ts.addAll(keySet(n.right));
        return ts;
    }

    @Override
    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        V va = get(key);
        if (va == null) {
            return null;
        }
        remove(root,key);
        return va;
    }

    private Node remove(Node n, K key) {
        if (n == null) {
            return null;
        }
        if (key.compareTo(n.key) > 0) {
            n.right = remove(n.right,key);
        }else if (key.compareTo(n.key) < 0) {
            n.left = remove(n.left,key);
        }else {
            if (n.left == null) {
                return n.right;
            }else if (n.right == null) {
                return n.left;
            }else {
                n.right = swap(n.right, n);
            }
        }
        return n;
    }

    private Node swap(Node T, Node R) {
        if (T.left == null) {
            R.key = T.key;
            R.value = T.value;
            return T.right;
        }else {
            T.left = swap(T.left, R);
            return T;
        }
    }

    @Override
    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}

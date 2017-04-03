package lab9;

import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Tong Yin on 4/2/2017.
 */
public class MyHashMap<K, V> implements Map61B<K,V> {
    private double LoadFactor;
    private ArrayList<Node>[] buckets;
    private int BuckSize;
    private HashSet<K> keyset;

    private class Node {
        private K key;
        private V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMap() {
        BuckSize = 1;
        buckets = new ArrayList[BuckSize];
        LoadFactor = 4;
        for (int i=0; i < BuckSize; i++) {
            buckets[i] = new ArrayList<>();
        }

        keyset = new HashSet<K>();
    }

    public MyHashMap(int initialSize) {
        BuckSize = initialSize;
        buckets = new ArrayList[BuckSize];
        LoadFactor = 4;
        for (int i=0; i < BuckSize; i++) {
            buckets[i] = new ArrayList<>();
        }

        keyset = new HashSet<K>();
    }

    public MyHashMap(int initialSize, double loadFactor) {
        BuckSize = initialSize;
        buckets = new ArrayList[BuckSize];
        LoadFactor = loadFactor;
        for (int i=0; i < BuckSize; i++) {
            buckets[i] = new ArrayList<>();
        }

        keyset = new HashSet<K>();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        for (int i=0; i < BuckSize; i++) {
            buckets[i].clear();
        }
        keyset.clear();
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return keyset.contains(key);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (!keyset.contains(key)) {
            return null;
        }
        int BuckNum = hash(key);
        for (Node n : buckets[BuckNum]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }

        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return keyset.size();
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (keyset.contains(key)) {
            int BuckNum = hash(key);
            for (Node n : buckets[BuckNum]) {
                if (n.key.equals(key)) {
                    n.value = value;
                }
            }
        }else {
            int ratio = size()/BuckSize;
            if (ratio >= LoadFactor) {
                resize(BuckSize*2);
            }
            Node stuff = new Node(key, value);
            int BuckNum = hash(key);
            buckets[BuckNum].add(stuff);
            keyset.add(key);
        }

    }

    public int hash(K key) {
        return (key.hashCode() & 0x7fffffff)%BuckSize;
    }

    public void resize(int size) {
        MyHashMap<K, V> temp = new MyHashMap<K, V>(size, this.LoadFactor);
        for (int i=0; i < BuckSize; i++) {
            for (Node n : buckets[i]) {
                temp.put(n.key, n.value);
            }
        }
        this.BuckSize = temp.BuckSize;
        this.buckets = temp.buckets;
        this.keyset = temp.keyset;
        this.LoadFactor = temp.LoadFactor;
    }

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        return keyset;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new NodeIterator();
    }

    private class NodeIterator implements Iterator<K> {

        private int pr;
        private int i;
        private int j;

        public NodeIterator() {
            pr = 0;
            i = 0;
            j = 0;
        }

        @Override
        public K next() {
            while (j >= buckets[i].size()) {
                i = i+1;
                j = 0;
            }
            K k = buckets[i].get(j).key;
            pr = pr +1;
            j = j + 1;
            return k;
        }

        @Override
        public boolean hasNext() {
            return (pr != size());
        }
    }
}

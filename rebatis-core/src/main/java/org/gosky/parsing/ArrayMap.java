package org.gosky.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayMap<K, V> {

    private int size;

    private K[] keys;
    private V[] values;

    private AtomicInteger useLength = new AtomicInteger(0);

    public ArrayMap(int size) {
        this.size = size;
        this.keys = (K[]) new Object[this.size];
        this.values = (V[]) new Object[this.size];
    }

    public void put(K key, V value) {
        this.keys[this.useLength.get()] = key;
        this.values[this.useLength.get()] = value;
        this.useLength.addAndGet(1);
    }

    public V get(K key) {

        if (key == null) return null;

        int p = this.findPosition(key);

        if (p < 0) return null;

        return values[p];
    }

    private int findPosition(K key) {

        if (key == null) return -1;

        for (int i = 0; i < useLength.get(); i++) {
            if (key.equals(keys[i])) {
                return i;
            }
        }

        return -1;
    }

    public Map<K, V> toMap() {

        Map<K, V> map = new HashMap<>();

        for (int i = 0; i < this.size; i++) map.put(keys[i], values[i]);

        return map;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < this.size; i++) {
            sb.append(keys[i]).append(":").append(values[i]).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");

        return sb.toString();
    }

}

package com.example.core.helpers;

import java.util.Iterator;

public class IterableHelper {
    public static <T> T onlyElementOf(Iterable<T> iterable) {
        Iterator<T> iter = iterable.iterator();
        T item = iter.next();
        if (iter.hasNext()) {
            throw new IllegalArgumentException("Iterable has more than one element!");
        }
        return item;
    }
}

package drones.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class WeakIter<T> implements Iterator<T> {

    private final Iterator<WeakReference<T>> source;

    private T next;

    private WeakIter(Iterator<WeakReference<T>> source) {
        this.source = source;
    }

    private void feed() {
        if (next != null) return;
        while (source.hasNext()) {
            WeakReference<T> next = source.next();
            T t = next.get();
            if (t == null) {
                source.remove();
                continue;
            }
            this.next = t;
            break;
        }
    }

    @Override
    public boolean hasNext() {
        feed();
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        T result = next;
        next = null;
        return result;
    }

    @Override
    public void remove() {
        source.remove();
    }

    public static <T> WeakIter<T> wrap(Iterator<WeakReference<T>> source) {
        return new WeakIter<>(source);
    }

}

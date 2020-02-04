package org.et;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 *
 * @param <E>
 */
public class FastDeletePriorityQueue<E> {
    private E[] items; // stores items at indices 1 to n, starts at 1 to simplify heap operations
    private int size = 0; // number of items in the queue
    private final Map<E, Integer> itemToIndex = new HashMap<>(); //extra map to allow fast O(log n) random deletes
    private final Comparator<E> comparator;

    public FastDeletePriorityQueue(Comparator<E> comparator) {
        this(10, comparator);
    }

    public FastDeletePriorityQueue(int initialCapacity, Comparator<E> comparator) {
        this.size = 0;
        //noinspection unchecked,SuspiciousArrayCast
        items = (E[]) new Object[initialCapacity + 1];
        this.comparator = comparator;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E peek() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return items[1];
    }

    public void add(E item) {
        if (size == items.length - 1) {
            resize(2 * items.length);
        }
        // add item, and percolate it up to maintain heap invariant
        size++;
        items[size] = item;
        itemToIndex.put(item, size);
        swim(size);

        // for internal validation during unit testing
        assert isMinHeap();
        assert indexesAreValid();
    }

    public E poll() {
        return deleteAt(1);
    }

    public boolean remove(E item) {
        Integer idx = itemToIndex.get(item);
        return idx != null && deleteAt(idx) != null;
    }

    public boolean removeIf(Predicate<E> filter) {
        return Arrays.stream(items).filter(filter).allMatch(this::remove);
    }

    private E deleteAt(int index) {
        E deleted = items[index];
        exch(index, size);
        size--;
        if (index == 1 || greater(index, index / 2)) {
            sink(index);
        } else {
            swim(index);
        }
        items[size + 1] = null; // avoid loitering and help with garbage collection
        if ((size > 0) && (size == (items.length - 1) / 4)) {
            resize(items.length / 2);
        }
        itemToIndex.remove(deleted);

        // for internal validation during unit testing
        assert isMinHeap();
        assert indexesAreValid();

        return deleted;
    }

    /**
     * standard "swim" operation in heap
     */
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k /= 2;
        }
    }

    /**
     * standard "sink" operation in heap
     */
    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }

    // swaps items i and j in heap array
    private void exch(int i, int j) {
        E swap = items[i];
        items[i] = items[j];
        items[j] = swap;
        itemToIndex.put(items[i], i);
        itemToIndex.put(items[j], j);
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > size;
        //noinspection unchecked,SuspiciousArrayCast
        E[] temp = (E[]) new Object[capacity];
        if (size >= 0) {
            System.arraycopy(items, 1, temp, 1, size);
        }
        items = temp;
    }

    // for internal validation during unit testing
    // is pq[1..N] a min heap?
    private boolean isMinHeap() {
        return isMinHeap(1);
    }

    // for internal validation during unit testing
    // is subtree of pq[1..n] rooted at k a min heap?
    private boolean isMinHeap(int k) {
        if (k > size) {
            return true;
        }
        int left = 2 * k;
        int right = 2 * k + 1;
        if (left <= size && greater(k, left)) {
            return false;
        }
        if (right <= size && greater(k, right)) {
            return false;
        }
        return isMinHeap(left) && isMinHeap(right);
    }

    // used to properly order timeouts
    private boolean greater(int i, int j) {
        return comparator.compare(items[i], items[j]) > 0;
    }

    // for internal validation during unit testing
    // checks if item indexes correspond to array position
    private boolean indexesAreValid() {
        int counter = 0;
        for (int i = 1; i <= size; i++) {
            counter++;
            if (itemToIndex.get(items[i]) != counter) {
                return false;
            }
        }
        return true;
    }
}

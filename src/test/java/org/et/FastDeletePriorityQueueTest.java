package org.et;

import org.testng.annotations.Test;

import java.util.Comparator;

import static org.testng.Assert.assertEquals;

public class FastDeletePriorityQueueTest {

    @Test
    public void testAddRemove() {
        FastDeletePriorityQueue<Integer> pq = new FastDeletePriorityQueue<>(Comparator.comparingInt(Integer::intValue));
        pq.add(123);
        pq.add(3);
        pq.add(5);
        assertEquals(pq.getSize(), 3);
        assertEquals(pq.peek().intValue(), 3);
        assertEquals(pq.poll().intValue(), 3);
        assertEquals(pq.getSize(), 2);

        pq.add(7);
        assertEquals(pq.getSize(), 3);

        assertEquals(pq.peek().intValue(), 5);
        assertEquals(pq.poll().intValue(), 5);
        assertEquals(pq.getSize(), 2);

        assertEquals(pq.peek().intValue(), 7);
        assertEquals(pq.poll().intValue(), 7);
        assertEquals(pq.getSize(), 1);

        assertEquals(pq.peek().intValue(), 123);
        assertEquals(pq.poll().intValue(), 123);
        assertEquals(pq.getSize(), 0);
    }
}

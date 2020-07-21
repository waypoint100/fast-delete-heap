package org.et;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1, jvmArgs = {"-da"})
public class PerfTestFastPQ {

    @Benchmark
    public int fastPqInsert(TestCase testCase) {
        var pq = new FastDeletePriorityQueue<>(Comparator.comparingInt(Integer::intValue));
        testCase.data.forEach(pq::add);
        return pq.size();
    }

    @Benchmark
    public int slowPqInsert(TestCase testCase) {
        var pq = new PriorityQueue<>(Comparator.comparingInt(Integer::intValue));
        pq.addAll(testCase.data);
        return pq.size();
    }

    @Benchmark
    public int fastPqDelete(TestCase testCase) {
        testCase.data.forEach(testCase.fastPq::remove);
        return testCase.fastPq.size();
    }

    @Benchmark
    public int slowPqDelete(TestCase testCase) {
        testCase.data.forEach(testCase.slowPq::remove);
        return testCase.slowPq.size();
    }

    @State(Scope.Benchmark)
    public static class TestCase {
        @Param({"10000", "100000", "200000"})
        private int count;
        private FastDeletePriorityQueue<Integer> fastPq;
        private PriorityQueue<Integer> slowPq;
        private List<Integer> data;

        @Setup(Level.Iteration)
        public void setup() {
            fastPq = new FastDeletePriorityQueue<>(Comparator.comparingInt(Integer::intValue));
            slowPq = new PriorityQueue<>(Comparator.comparingInt(Integer::intValue));
            data = IntStream.range(0, count).boxed().collect(Collectors.toList());
            Collections.shuffle(data);
            data.forEach(fastPq::add);
            slowPq.addAll(data);
        }
    }
}

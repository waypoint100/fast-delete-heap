# fast-delete-heap
Priority queue / heap implementation which allows fast O(log N) random deletes.

Java's standard PriorityQueue has O(N) deletion, which makes it pretty inefficient in case
if queue is large and there is a lot of delete operations.

To allow fast deletes, this implementation maintains an extra item-to-heap-index map, which slows down
inserts a bit.

Base heap implementation is inspired by https://algs4.cs.princeton.edu/24pq/

#### PriorityQueue vs FastDeletePriorityQueue benchmark:

**op**|**impl**|**10K**|**100K**|**200K**|**1M**
:-----:|:-----:|:-----:|:-----:|:-----:|:-----:
**insert**|Java PQ|0.57 ± 0.20 ms|6.7 ± 2.4 ms|16.1 ± 6.8 ms|115 ±  48 ms
&nbsp;|Fast PQ|1.74 ± 0.70 ms|26.8 ± 16.9 ms|75 ± 22 ms|442 ± 102 ms
**delete**|Java PQ|20.9 ± 2.1 ms|3600 ± 670 ms|17540 ± 6990 ms|N/A
&nbsp;|Fast PQ|2.84 ± 1.32 ms|38 ± 12 ms|103 ± 53 ms|771 ± 163  ms

How to run benchmark: `gw jmh`



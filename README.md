After lecturing on the Looper, Handler, and HandlerThread chapter, a student asked how can we pass streaming data (from a sensor) from a background thread back to the main (UI) thread?

For this use case, the `HandlerThread` is unneccessary for several reasons:

1. Our background thread is just doing one thing: reading the sensor data. It doesn't need its own queue. We don't need to post messages to this queue. So a `Looper` and `MessageQueue` are unused.
2. We need a new `Handler` on each thread we want to read sensor data. The `HandlerThread` would have to post messages to each of these threads. Right now we only read data from the UI thread. Imagine that we read on the UI thread and another background thread that wrote to a database. The background thread would have to post messages to two Handlers.
3. We can't adjust the priority of the messages after they are sent to target. The message queue is FIFO.
4. The background thread is just dumping messages into the queue, without any concern if they are being read in fast enough. If the UI thread isn't processing messages fast enough, we'll end up with an ever-growing backlog of messages.

Instead, we can scrap the `HandlerThread` and use a basic `Thread` instead. We can pass data via a `Queue` from the background thread to the UI thread. We'll use a `ConcurrentLinkedQueue`. Each thread shares this object and the synchronization and thread safety is taken care of for us.

1. This removes the unused `Looper` and `MessageQueue` from our background thread.
2. We can read from the queue on as many threads as we want.
3. We can adjust the priority of each item by relying on a `PriorityQueue`, instead of a `ConcurrentLinkedQueue`.
4. We can limit the number of items in the queue by using a `BlockingQueue`, instead of a `ConcurrentLinkedQueue`.


No matter how we process these data points on the UI (via a `Queue` or a `HandlerThread`) we will interfere with other UI events. So we need to take care that our processing doesn't block the UI thread.

Suggested Reading:

* How to use BlockingQueue:
http://www.journaldev.com/1034/java-blockingqueue-example-implementing-producer-consumer-problem

* PriorityQueue:
http://docs.oracle.com/javase/7/docs/api/java/util/PriorityQueue.html

* How to properly shutdown thread:
http://www.javaspecialists.eu/archive/Issue056.html

* Understanding thread states:
http://journals.ecs.soton.ac.uk/java/tutorial/java/threads/states.html

* Very thorough book:
http://shop.oreilly.com/product/0636920029397.do
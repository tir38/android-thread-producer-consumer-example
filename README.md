After lecturing on the Looper, Handler, and HandlerThread chapter, a student asked how can we pass streaming data (from a sensor) from a background thread back to the main (UI) thread?

For this use case, the `HandlerThread` is overkill. Posting individual data points via `obtainMessage().sendToTarget()` will fill up the UI's message queue with lots of data points, resulting in lots of `Message` objects being created/recycled.

Instead, we can scrap the `HandlerThread` and use a basic `Thread` instead. We can pass data via a `BlockingQueue` from the background thread to the UI thread. Each thread shares this object and the synchronization and thread safety is taken care of for us.

No matter how we process these data points on the UI (via the `BlockingQueue` or a custom `Handler`) we will interfere with other UI events. So we need to take care that our processing doesn't block the UI thread.

Suggested Reading:

* How to use BlockingQueue:
http://www.journaldev.com/1034/java-blockingqueue-example-implementing-producer-consumer-problem

* How to properly shutdown thread:
http://www.javaspecialists.eu/archive/Issue056.html

* Understanding thread states:
http://journals.ecs.soton.ac.uk/java/tutorial/java/threads/states.html

* Very thorough book:
http://shop.oreilly.com/product/0636920029397.do
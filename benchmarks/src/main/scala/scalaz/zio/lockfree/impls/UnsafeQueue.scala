package scalaz.zio.lockfree.impls

import scalaz.zio.lockfree.MutableConcurrentQueue

import scala.reflect.ClassTag

class UnsafeQueue[A: ClassTag](override val capacity: Int) extends MutableConcurrentQueue[A] {
  private val buf: Array[A] = Array.ofDim[A](capacity)
  var head: Long            = 0
  var tail: Long            = 0

  override def offer(a: A): Boolean =
    if (isFull()) {
      false
    } else {
      tail += 1
      buf((tail % capacity).asInstanceOf[Int]) = a
      true
    }

  override def poll(default: A): A =
    if (isEmpty()) {
      default
    } else {
      val el = buf((head % capacity).asInstanceOf[Int])
      head += 1
      el
    }

  override def isEmpty(): Boolean =
    head == tail

  override def isFull(): Boolean =
    head + capacity - 1 == tail

  override def enqueuedCount(): Long = tail

  override def dequeuedCount(): Long = head

  override def size(): Int = capacity - (head - tail).toInt
}

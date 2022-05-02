package assignment

import java.util.concurrent.{Executors, Future, TimeUnit}
import scala.annotation.tailrec

object Aufgabe1 {
  /* Sequentielle map Methode. */
  def map[A, B](l: List[A], f: A => B): List[B] = {
    @tailrec
    def doIt(list: List[A], acc: List[B]): List[B] =
      list match {
        case Nil => acc
        case x :: xs => doIt(xs, f(x) :: acc)
      }

    doIt(l, Nil)
  }

  /* Parallele map Methode. */
  def parmap[A, B](list: List[A], func: A => B): List[B] = {
    val ex = Executors.newFixedThreadPool(4)
    val futures = map[A, Future[B]](list, a => ex.submit(() => func(a)))
    val result = map[Future[B], B](futures, f => f.get)
    ex.shutdown()
    ex.awaitTermination(Long.MaxValue, TimeUnit.DAYS)
    result
  }

  def filter[A](list: List[A], predicate: A => Boolean): List[A] = {

    @tailrec
    def doIt(l: List[A], acc: List[A]): List[A] = {
      l match {
        case Nil => acc.reverse
        case x :: xs => if (predicate(x)) doIt(xs, x :: acc) else doIt(xs, acc)
      }
    }

    doIt(list, Nil)
  }

  def parFilter[A](list: List[A], predicate: A => Boolean): List[A] = {
    val ex = Executors.newFixedThreadPool(4)
    val futures = map[A, (A, Future[Boolean])](list, (a: A) => (a, ex.submit(() => predicate(a))))
    val filtered = filter[(A, Future[Boolean])](futures, e => e._2.get)
    val result = map[(A, Future[Boolean]), A](filtered, _._1)
    ex.shutdown()
    ex.awaitTermination(Long.MaxValue, TimeUnit.DAYS)
    result
  }

  def main(args: Array[String]): Unit = {
    val l = Range(0, 100_000_000).toList
    val start = System.currentTimeMillis()
    //val res = parFilter(l, (i: Int) => i % 2 == 0)
    val res = filter(l, (i: Int) => i % 2 == 0)
    //val res = map(l, (i: Int) => i * 2)
    val stop = System.currentTimeMillis()
    println(s"Duration ${(stop - start)} ms")
  }
}
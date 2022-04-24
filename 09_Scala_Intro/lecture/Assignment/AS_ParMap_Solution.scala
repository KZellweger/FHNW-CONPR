package collections

import java.util.concurrent.{CompletableFuture, Executors, Future, TimeUnit}

import scala.annotation.tailrec

object EXParMap_Solution {
  /* Sequentielle map Methode. */
  def map[A, B](l: List[A], f: A => B): List[B] = l match {
    case Nil => Nil
    case x :: xs => f(x) :: map(xs, f)
  }

  /* Parallele map Methode. */
  def parmap[A, B](l: List[A], f: A => B): List[B] = {
    val ex = Executors.newFixedThreadPool(4)
    val futures = map[A, Future[B]](l, a => ex.submit(() => f(a)))
    val result = map[Future[B], B](futures, f => f.get)
    ex.shutdown()
    ex.awaitTermination(Long.MaxValue, TimeUnit.DAYS)
    result
  }

  /* Sequentielle filter Methode. */
  def filter[A](l: List[A], f: A => Boolean): List[A] = {
    l match {
      case Nil => Nil
      case x :: xs => if (f(x)) x :: filter(xs, f) else filter(xs, f)
    }
  }

  /* Parallele filter Methode. */
  def parfilter[A](l: List[A], f: A => Boolean): List[A] = {
    val ex = Executors.newFixedThreadPool(4)
    val futures = map[A, (A, Future[Boolean])](l, (a: A) => (a, ex.submit(() => f(a))))
    val filtered = filter[(A, Future[Boolean])](futures, e => e._2.get)
    val result = map[(A, Future[Boolean]), A](filtered, _._1)
    ex.shutdown()
    ex.awaitTermination(Long.MaxValue, TimeUnit.DAYS)
    result
  }

  /* Sequentielle reduce (right) Methode.
   * Nicht tail-rekursiv!
   */
  def reduce[A](as: List[A], r: (A, A) => A): A = {
    as match {
      case Nil      => sys.error("reduce(Nil,r)")
      case a :: Nil => a
      case a :: as  => r(a, reduce(as, r))
    }
  }

  /* Parallele reduce Methode.  ;-) */
  // Requires: org.scala-lang.modules:scala-parallel-collections_2.13:0.2.0
  // def parreduce0[A](as: List[A], r: (A, A) => A): A = as.par.reduce(r)


  /* Bottom up parallele reduce Methode.
   *
   *Level:2        R
   *              /r\
   *Level:1      A   B
   *            /r\ /r\
   *Level:0    C  D E  F
   *
   * Die Methode reduceLayer reduziert eine Liste auf Level n in eine neue Liste auf Level n+1.
   * Die Methode repeatReduce wiederholt reduceLayer bis nur noch ein Resultat übrig ist.
   *
   * Bei top-down Lösungen muss man vorsichtig sein, dass nicht Threads auf höheren Levels auf
   * Resultate aus tieferen Levels warten.
   *
   * Die übergebene Funktion r muss assoziativ sein: ((A r B) r C) == (A r (B r C))
   */
  def parreduce[A](as: List[A], r: (A, A) => A): A = {
    val ex = Executors.newFixedThreadPool(4)

    /* This implementation is NOT tail recursive an blows the stack on large input lists.
    @tailrec
    def reduceLayer(as: List[Future[A]]): List[Future[A]] = as match {
      case a :: b :: rest => ex.submit(() => r(a.get, b.get)) :: reduceLayer(rest)
      case other          => other
    }
    */

    @tailrec
    def reduceLayer(as: List[Future[A]], acc: List[Future[A]]): List[Future[A]] = as match {
      case Nil            => acc
      case a :: Nil       => acc :+ a
      case a :: b :: rest => reduceLayer(rest, acc :+ ex.submit(() => r(a.get, b.get)))
    }

    @tailrec
    def repeatReduce(as: List[Future[A]]): Future[A] = as match {
      case Nil      => sys.error("reduce emptyList")
      case a :: Nil => a
      case more     => repeatReduce(reduceLayer(more, List()))
    }

    val prep = map[A, Future[A]](as, a => CompletableFuture.completedFuture(a))
    val result = repeatReduce(prep).get

    ex.shutdown
    ex.awaitTermination(Long.MaxValue, TimeUnit.DAYS)
    result
  }
}
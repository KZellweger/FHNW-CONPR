package functions

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

object Einleitung {
  def call(): Unit = println(":-)")

  def async(action: () => Unit): Unit = {
    new Thread() {
      override def run(): Unit = {
        action() // Hier wird die übergebene Funktion aufgerufen
      }
    }.start()
  }

  def main(args: Array[String]): Unit = {
    async(call)
  }
}

object Aufgabe1 {

  def clock(i: Int): Unit = println(s"Tick(${i})")

  def everySecond(action: Int => Unit): Unit = {
    new Thread() {
      override def run(): Unit = {
        var counter = 0
        while (counter < 20) {
          action(counter)
          counter = counter + 1
          Thread.sleep(1000)
        }
      }
    }.start()
  }

  def main(args: Array[String]): Unit = {

    //    everySecond(clock)

    /* Anonyme Funktionen */
    //    everySecond(i => println("Tick(" + i + ")"))

    everySecond(i => {
      println("Tock()")
      println("Tick(" + i + ")")
    })
  }
}

object Aufgabe2 {
  // By Name Parameter
  def time(block: => Unit): Unit = {
    val startTime = System.currentTimeMillis();
    block
    val endTime = System.currentTimeMillis();
    println(s"[${endTime - startTime} ms]")
  }

  def time[A](block: () => A): A = {
    val startTime = System.currentTimeMillis();
    val res: A = block()
    val endTime = System.currentTimeMillis();
    println(s"[${endTime - startTime} ms]")
    res
  }

  def main(args: Array[String]): Unit = {
    time {
      Thread.sleep(100)
    }

    val a = time(() => {
      Thread.sleep(100)
      "Done"
    })
    println(s"Returned ${a}")

  }
}

object Aufgabe3 {
  class NiceAtomicInt(init: Int) {
    val value = new AtomicInteger(init)

    def modify(calc: Int => Int) {
      while (true) {
        val oldValue = value.get()
        val newValue = calc(oldValue)
        if (value.compareAndSet(oldValue, newValue)) return
      }
    }

    def get(): Int = value.get()
  }


  class NiceAtomic[A](init: A) {
    val value = new AtomicReference[A](init)

    def modify(calc: A => A) {
      while (true) {
        val oldValue = value.get()
        val newValue = calc(oldValue)
        if (value.compareAndSet(oldValue, newValue)) return
      }
    }

    def get(): A = value.get()
  }


  def main(args: Array[String]): Unit = {
    val balance = new NiceAtomicInt(0)
    balance.modify(b => b + 10 * 42)
    println(balance.get())
    val str = new NiceAtomic[String]("ollah")
    str.modify(b => b.reverse)
    println(str.get())
  }
}

/* ==Lösungen==
 
 Die Lösungen können mit NSACryptoTool.scala decodiert werden. 
 
 
 Aufgabe1:
 
 bowrpg Nhstnor1 {

  qrs pybpx(v: Vag) = cevagya("Gvpx(" + v + ")")

  qrs rirelFrpbaq(npgvba: (Vag) => Havg): Havg = {
    arj Guernq() {
      bireevqr qrs eha(): Havg = {
        ine v = 0
        juvyr (gehr) {
          npgvba(v)
          v += 1
          Guernq.fyrrc(1000)
        }
      }
    }.fgneg()
  }

  qrs znva(netf: Neenl[Fgevat]) {
    rirelFrpbaq(pybpx)

    /* Nabalzr Shaxgvbara */
    rirelFrpbaq(v => cevagya("Gvpx(" + v + ")"))

    rirelFrpbaq(v => {
      cevagya("Gbpx()")
      cevagya("Gvpx(" + v + ")")
    })
  }
}


Aufgabe 2:

bowrpg Nhstnor2 {
  qrs gvzr(oybpx: () => Havg): Havg = {
    iny fgneg = Flfgrz.pheeragGvzrZvyyvf()
    oybpx()
    iny raq = Flfgrz.pheeragGvzrZvyyvf()
    cevagya("[" + (raq - fgneg) + " zf]")
  }

  qrs gvzr[N](oybpx: () => N): N = {
    iny fgneg = Flfgrz.pheeragGvzrZvyyvf()
    iny erfhyg = oybpx()
    iny raq = Flfgrz.pheeragGvzrZvyyvf()
    cevagya("[" + (raq - fgneg) + " zf]")
    erfhyg
  }

  qrs znva(netf: Neenl[Fgevat]): Havg = {
    gvzr(() => {
      Guernq.fyrrc(100)
    })

    iny n = gvzr(() => {
      Guernq.fyrrc(100)
      "Qbar"
    })
  }
}


Aufgabe 3:

bowrpg Nhstnor3 {
  pynff AvprNgbzvpVag(vavg: Vag) {
    iny ers = arj NgbzvpVagrtre(vavg)
    qrs zbqvsl(s: Vag => Vag): Havg = {
      juvyr (gehr) {
        iny pheeragInyhr = ers.trg()
        iny arjInyhr = s(pheeragInyhr)
        vs (ers.pbzcnerNaqFrg(pheeragInyhr, arjInyhr)) erghea
      }
    }
  }
  
  pynff AvprNgbzvp[N](vavg: N) {
    iny ers = arj NgbzvpErsrerapr[N](vavg)
    qrs zbqvsl(s: N => N): Havg = {
      juvyr (gehr) {
        iny pheeragInyhr = ers.trg()
        iny arjInyhr = s(pheeragInyhr)
        vs (ers.pbzcnerNaqFrg(pheeragInyhr, arjInyhr)) erghea
      }
    }
    
    bireevqr qrs gbFgevat(): Fgevat = 
      ers.trg().gbFgevat()
  }
    
  qrs znva(netf: Neenl[Fgevat]): Havg = {
    iny onynapr = arj AvprNgbzvpVag(0)
    onynapr.zbqvsl(o => o + 10)
    
    iny yvfg = arj AvprNgbzvp(Yvfg(1,2,3))
    yvfg.zbqvsl(y => 0 :: y)
    cevagya(yvfg)
  }
}

*/


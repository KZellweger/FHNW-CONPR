package bank.stm

import scala.concurrent.stm._
import scala.jdk.CollectionConverters._
import bank.Bank
import bank.BankDriver
import bank.Account
import bank.OverdrawException
import bank.InactiveException


class STMBankDriver extends BankDriver {
  def connect(args: Array[String]): Unit = {}
  def disconnect(): Unit =  {}
  def getBank(): Bank = STMBank
}


object STMBank extends Bank {
  private val id = Ref(0L)
  private val numberToAccount = Ref(Map[String, STMAccount]())
  
  def createAccount(owner: String): String = atomic { implicit txn =>
    val number = owner + id()
    val acc = new STMAccount(number, owner)
    numberToAccount.transform( _ + (number -> acc))
    id.transform(_ + 1)
    number
  }
  
  def closeAccount(number: String): Boolean =  atomic { implicit txn =>
    numberToAccount().get(number).map(_.deactivate()).getOrElse(false)
  }
  
  def getAccount(number: String): Account = atomic { implicit txn =>
    numberToAccount().getOrElse(number, null)
  }
  
  def getAccountNumbers(): java.util.Set[String] = atomic { implicit txn =>
    numberToAccount().filter(_._2.isActive()).keySet.asJava
  }
  
  def transfer(from: Account, to: Account, amount: Double): Unit = ???
}


class STMAccount(number: String, owner: String) extends Account {
  private val balance = Ref(0d)
  private val active = Ref(true)
  
  def getNumber(): String = number
  def getOwner(): String = owner
  def isActive(): Boolean = active.single()
  def deactivate(): Boolean = atomic { implicit txn =>
    if(balance() != 0 || active() == false) false
    else { active() = false; true }
  }
  
  def getBalance(): Double = balance.single.get /* Single operation transaction. */
 
  def deposit(amount: Double): Unit = ???
  def withdraw(amount: Double): Unit = ???
}
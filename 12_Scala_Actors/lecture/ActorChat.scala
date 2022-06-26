package as

import scala.collection._
import java.util.concurrent.CountDownLatch
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

object ActorChat extends App {

  val as = ActorSystem("as")
  
  // User -> Server
  case class NewRoom(name: String)
  case class JoinRoom(name: String)

  case object ListRoomsReq
  case class ListRoomsRes(rooms: List[String])

  case class MsgReq(text: String)
  case class MsgRes(text: String)

  case class EnterRoomReq(user: ActorRef)
  case class EnterRoomRes(room: ActorRef)

  class Server extends Actor {
    val rooms = mutable.Map[String, ActorRef]()

    implicit val timeout = Timeout(2 seconds)
    import as.dispatcher

    def receive = {
      case ListRoomsReq => {
        sender() ! ListRoomsRes(rooms.keys.toList)
      }
      case JoinRoom(name) => {
        joinRoom(name)
      }
      case NewRoom(name) => {
        rooms += ((name, as.actorOf(Props[Room])))
        joinRoom(name)
      }
    }
    
    def joinRoom(name: String) : Unit = {
      val roomResponse = rooms(name) ? EnterRoomReq(sender())
        roomResponse.pipeTo(sender())
    }
  }

  class Room extends Actor {
    //val users =  mutable.ListBuffer[ActorRef]()
    var users = List[ActorRef]()
    def receive = {
      case MsgReq(text) => {
        //for (u <- users if u != sender()) { u ! MsgRes(text) }
        users.foreach(u => if(u != sender()) u ! MsgRes(text))
        
      }
      case EnterRoomReq(user) => {
        users = user :: users
        sender() ! EnterRoomRes(self)
      }
    }
  }

  val server = as.actorOf(Props[Server])

  as.actorOf(Props(new Actor() {
    server ! NewRoom("Actors")
    def receive = {
      case EnterRoomRes(room) =>
        Thread.sleep(100)
        room ! MsgReq("Hi list! I have a question regarding Scala actors.")
      case MsgRes(text) => println("U1: " + text)
    }
  }))

  as.actorOf(Props(new Actor() {
    Thread.sleep(50)
    server ! JoinRoom("Actor")
    def receive = {
      case MsgRes(text) => println("U2: " + text)
    }
  }))

  as.actorOf(Props(new Actor() {
    Thread.sleep(50)
    server ! JoinRoom("Actors")
    def receive = {
      case MsgRes(text) => println("U3: " + text)
    }
  }))
}
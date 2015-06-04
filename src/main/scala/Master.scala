import akka.actor.{Actor, ActorLogging}

case class Task()
case class Result()

class Master() extends Actor with ActorLogging {

  override def preStart() = {
    log.info("Master starting")
  }

  def receive = {
    case result: Result => {
    }
  }
}

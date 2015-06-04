import akka.actor.{ActorLogging, Actor}
import akka.actor.Actor.Receive
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor

class Worker extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart() = {
    log.info("Worker starting")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent])
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
  }
}

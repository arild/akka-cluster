import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.contrib.pattern.ClusterSingletonManager

object Application extends App {
  val system = ActorSystem("TspSystem")

  val masterProps = ClusterSingletonManager.defaultProps(
    singletonProps = Props(new Master),
    singletonName = "masterSingleton",
    terminationMessage = PoisonPill,
    role = null)
  system.actorOf(masterProps, "masterSingleton")

  system.actorOf(Props(new Worker))
}

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.contrib.pattern.ClusterSingletonManager

object Application extends App {

  val cities = Cities(11)
  cities.print()

  //println(Tsp.getSubRoutes(cities, 4))

  println(Tsp.findShortestRoute(cities, Route()))

  val system = ActorSystem("TspSystem")

  val masterProps = ClusterSingletonManager.defaultProps(
    singletonProps = Props(new Master),
    singletonName = "masterSingleton",
    terminationMessage = PoisonPill,
    role = null)
  system.actorOf(masterProps, "masterSingleton")

  system.actorOf(Props(new Worker))
}

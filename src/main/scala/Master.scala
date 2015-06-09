import akka.actor.{RootActorPath, Actor, ActorLogging}
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp, MemberEvent, InitialStateAsEvents}

import scala.collection.mutable

case class GetSubRoute()

class Master() extends Actor with ActorLogging {
  val members = mutable.Set[Member]()
  val cluster = Cluster(context.system)
  val cities = Cities(Cities.generateDistanceMatrix(numCities = 11))
  val subRoutes: mutable.Buffer[Route] = cities.getSubRoutes(depth = 2).toBuffer
  var shortestRoute = Route(distance = Int.MaxValue)

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])
    log.info("Generated {} sub routes", subRoutes.size)
  }

  def receive = {
    case MemberUp(member) => {
      log.info("Member is Up: {}", member.address)
      members.add(member)
      println(members)
      context.actorSelection(RootActorPath(member.address) / "user" / "worker") ! cities
    }
    case MemberRemoved(member, previousStatus) => {
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
      members.remove(member)
      println(members)
    }
    case GetSubRoute() => {
      if (subRoutes.nonEmpty) {
        sender() ! subRoutes.remove(0)
        if (subRoutes.size % 10 == 0)
          log.info("{} sub routes left", subRoutes.size)
      }
      else {
        println("No more tasks")
      }
    }
    case route: Route => if (route.distance < shortestRoute.distance) shortestRoute = route
  }
}
import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

import scala.collection.mutable

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  val members = mutable.TreeSet[Member]()(Ordering.fromLessThan[Member](_.isOlderThan(_)))
  var cities: Cities = null

  override def preStart() = {
    log.info("Worker starting")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])
  }

  override def receive: Receive = {
    case MemberUp(member) => members.add(member)
    case MemberRemoved(member, previousStatus) => members.remove(member)
    case cities: Cities => {
      log.info("Received cities")
      this.cities = cities
      sender() ! GetSubRoute()
    }
    case subRoute: Route => {
      log.info("Received route {}", subRoute)
      sender() ! cities.findShortestRoute(subRoute)
      sender() ! GetSubRoute()
    }
  }
}

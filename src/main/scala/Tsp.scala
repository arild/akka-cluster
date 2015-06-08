import scala.util.Random

case class Route(cities: List[Int] = List(0), distance: Int = 0) {
  def addCity(city: Int, distance: Int) = {
    Route(cities ::: List(city), this.distance + distance)
  }
}

case class Cities(distanceMatrix: Array[Array[Int]]) {
  val numCities = distanceMatrix.length

  def getSubRoutes(depth: Int, currentRoute: Route = Route()): List[Route] = {
    if (currentRoute.cities.length == depth) {
      List(currentRoute)
    }
    else {
      remainingCities(currentRoute).foldLeft(List[Route]()) { (acc, city) =>
        val dist: Int = distance(currentRoute.cities.last, city)
        acc ::: getSubRoutes(depth, currentRoute.addCity(city, dist))
      }
    }
  }

  def findShortestRoute(currentRoute: Route): Route = {
    if (currentRoute.cities.length == numCities) {
      currentRoute.addCity(0, distance(currentRoute.cities.last, 0))
    }
    else {
      remainingCities(currentRoute).foldLeft(Route(distance = Int.MaxValue)) { (currentShortestRoute, city) =>
        val dist: Int = distance(currentRoute.cities.last, city)
        val route = findShortestRoute(currentRoute.addCity(city, dist))
        if (route.distance < currentShortestRoute.distance) {
          route
        } else {
          currentShortestRoute
        }
      }
    }
  }

  def printDistanceMatrix() = {
    for (i <- 0 until distanceMatrix.length) {
      for (j <- 0 until distanceMatrix.length) {
        printf("%d ", distanceMatrix(i)(j))
      }
      println()
    }
  }

  private def distance(from: Int, to: Int): Int = {
    distanceMatrix(from)(to)
  }

  private def remainingCities(route: Route): List[Int] = {
    (0 until numCities).filterNot(route.cities.toSet).toList
  }
}

object Cities {
  def generateDistanceMatrix(numCities: Int) = {
    val distanceMatrix: Array[Array[Int]] = Array.ofDim[Int](numCities, numCities)
    Random.setSeed(8)
    for (i <- 0 until numCities) {
      for (j <- 0 until numCities) {
        distanceMatrix(i)(j) = 1 + Random.nextInt(5)
      }
    }
    distanceMatrix
  }
}

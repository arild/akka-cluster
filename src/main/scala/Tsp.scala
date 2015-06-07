import scala.util.Random

case class Cities(numCities: Int) {
  val matrix = generateDistanceMatrix(numCities)

  def distance(from: Int, to: Int): Int = {
    matrix(from)(to)
  }

  def remainingCities(route: Route): List[Int] = {
    (0 until numCities).filterNot(route.cities.toSet).toList
  }

  def print(): Unit = {
    for (i <- 0 until matrix.length) {
      for (j <- 0 until matrix.length) {
        printf("%d ", matrix(i)(j))
      }
      println()
    }
  }

  private def generateDistanceMatrix(numVertices: Int) = {
    val distanceMatrix = Array.ofDim[Int](numVertices, numVertices)
    Random.setSeed(8)
    for (i <- 0 until numVertices) {
      for (j <- 0 until numVertices) {
        distanceMatrix(i)(j) = 1 + Random.nextInt(5)
      }
    }
    distanceMatrix
  }
}

case class Route(cities: List[Int] = List(0), distance: Int = 0) {
  def addCity(city: Int, distance: Int) = {
    Route(cities ::: List(city), this.distance + distance)
  }
}

object Tsp {

  def getSubRoutes(cities: Cities, depth: Int, currentRoute: Route = Route()): List[Route] = {
    if (currentRoute.cities.length == depth) {
      List(currentRoute)
    }
    else {
      cities.remainingCities(currentRoute).foldLeft(List[Route]()) { (acc, city) =>
        val distance: Int = cities.distance(currentRoute.cities.last, city)
        acc ::: getSubRoutes(cities, depth, currentRoute.addCity(city, distance))
      }
    }
  }

  def findShortestRoute(cities: Cities, currentRoute: Route): Route = {
    if (currentRoute.cities.length == cities.numCities) {
      currentRoute.addCity(0, cities.distance(currentRoute.cities.last, 0))
    }
    else {
      cities.remainingCities(currentRoute).foldLeft(Route(distance = Int.MaxValue)) { (currentShortestRoute, city) =>
        val distance = cities.distance(currentRoute.cities.last, city)
        val route = findShortestRoute(cities, currentRoute.addCity(city, distance))
        if (route.distance < currentShortestRoute.distance) {
          route
        } else {
          currentShortestRoute
        }
      }
    }
  }
}

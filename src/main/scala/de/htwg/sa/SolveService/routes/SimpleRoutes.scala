package de.htwg.sa.SolveService.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import de.htwg.sa.SolveService.services.GridService

/**
 * Routes can be defined in separated classes like shown in here
 */
trait SimpleRoutes {

  // This `val` holds one route (of possibly many more that will be part of your Web App)
  lazy val simpleRoutes: Route =
    path("hello") { // Listens to paths that are exactly `/hello`
      get {
        complete("Hi!")
      }
    } ~
      path("solve") {
        GridService.route
      }
}

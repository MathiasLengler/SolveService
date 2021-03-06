package de.htwg.sa.SolveService

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.ActorMaterializer
import de.htwg.sa.SolveService.routes.{ BaseRoutes, SimpleRoutes }
import org.apache.log4j.PropertyConfigurator

import scala.io.StdIn

object WebServer extends Directives with SimpleRoutes {
  implicit val system = ActorSystem("my-system")

  def main(args: Array[String]) {
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    PropertyConfigurator.configure("log4j.properties")

    val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)

    println(s"Server online at http://0.0.0.0:8080/")
  }

  // Here you can define all the different routes you want to have served by this web server
  // Note that routes might be defined in separated traits like the current case
  val routes: Route = BaseRoutes.baseRoutes ~ simpleRoutes

}

package de.htwg.sa.SolveService.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{ Directives, Route }
import de.htwg.sa.SolveService.WebServer
import de.htwg.sa.SolveService.protocol.MinesweeperProtocol
import minesweeper.model.impl.GridFactory
import minesweeper.model.{ ICell, IGrid }
import minesweeper.solverplugin.impl.jacop.JacopSolver
import spray.json._

import scala.concurrent.Future

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val cellFormat = MinesweeperProtocol.CellJsonFormat
  implicit val gridFormat = MinesweeperProtocol.GridJsonFormat
  implicit val solveResultFormat = MinesweeperProtocol.SolveResultJsonFormat
}

object SolveService extends Directives with JsonSupport {

  private implicit val blockingDispatcher = WebServer.system.dispatchers.lookup("blocking-dispatcher")

  val route: Route =
    post {
      entity(as[IGrid[ICell]]) { grid =>
        complete(Future {
          // TODO: error handling
          JacopSolver.buildSolveResult(grid)
        })
      }
    }
}

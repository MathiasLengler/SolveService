package de.htwg.sa.SolveService.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import de.htwg.sa.SolveService.protocol.MinesweeperProtocol
import minesweeper.model.impl.GridFactory
import minesweeper.model.{ICell, IGrid}
import minesweeper.solverplugin.impl.jacop.JacopSolver
import spray.json._


// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val cellFormat = MinesweeperProtocol.CellJsonFormat
  implicit val gridFormat = MinesweeperProtocol.GridJsonFormat
  implicit val solveResultFormat = MinesweeperProtocol.SolveResultJsonFormat
}

object SolveService extends Directives with JsonSupport {
  val route: Route =
    get {
      complete(new GridFactory(2, 2).getGrid)
    } ~
      post {
        entity(as[IGrid[ICell]]) { grid =>
          complete(JacopSolver.buildSolveResult(grid))
        }
      }
}

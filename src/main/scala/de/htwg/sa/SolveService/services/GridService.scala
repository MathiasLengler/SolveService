package de.htwg.sa.SolveService.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import de.htwg.sa.SolveService.protocol.MinesweeperProtocol
import minesweeper.model.impl.{Cell, Grid, GridFactory}
import minesweeper.model.{ICell, IGrid}
import spray.json._


// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val cellFormat = MinesweeperProtocol.CellJsonFormat
  implicit val gridFormat = MinesweeperProtocol.GridJsonFormat
  implicit val solveResultFormat = MinesweeperProtocol.SolveResultJsonFormat
}

object GridService extends Directives with JsonSupport {
  val route: Route =
    get {
      complete(new GridFactory(2, 2).getGrid)
    } ~
      post {
        entity(as[IGrid[ICell]]) { grid =>
          complete(getPostResponse(grid))
        }
      }

  private def getPostResponse(grid: IGrid[ICell]) = {
    import minesweeper.solverplugin.impl.jacop.JacopSolver

    JacopSolver.buildSolveResult(grid)
  }
}

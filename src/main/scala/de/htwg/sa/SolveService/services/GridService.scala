package de.htwg.sa.SolveService.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import minesweeper.model.impl.{Cell, Grid, GridFactory}
import minesweeper.model.{ICell, IGrid}
import spray.json._

import scala.collection.JavaConverters._

object MinesweeperProtocol extends DefaultJsonProtocol {

  implicit object CellJsonFormat extends RootJsonFormat[ICell] {
    def write(c: ICell): JsObject = {
      JsObject(
        "hasMine" -> JsBoolean(c.isMine),
        "isFlagged" -> JsBoolean(c.isFlag),
        "isRevealed" -> JsBoolean(c.isOpened),
        "position" -> JsObject(
          "row" -> JsNumber(c.getRow),
          "col" -> JsNumber(c.getCol)
        ),
        "surroundingMines" -> JsNumber(c.getMines)
      )
    }

    def read(value: JsValue): ICell = {
      value.asJsObject.getFields("hasMine", "isFlagged", "isRevealed", "position", "surroundingMines") match {
        case Seq(JsBoolean(hasMine),
          JsBoolean(isFlagged),
          JsBoolean(isRevealed),
          position: JsObject,
          JsNumber(surroundingMines)) =>
          position.getFields("row", "col") match {
            case Seq(JsNumber(row), JsNumber(col)) =>
              val state = if (isRevealed) {
                ICell.State.OPENED
              } else if (isFlagged) {
                ICell.State.FLAG
              } else {
                ICell.State.CLOSED
              }
              new Cell(row.toInt, col.toInt, state, surroundingMines.toInt, hasMine)
            case _ => throw DeserializationException("Position expected")
          }
        case _ => throw DeserializationException("Cell expected")
      }
    }
  }

  implicit object GridJsonFormat extends RootJsonFormat[IGrid[ICell]] {
    def write(g: IGrid[ICell]): JsArray = {
      implicit val p = MinesweeperProtocol

      val cells = g.getNestedCells

      JsArray(cells.asScala
        .map(_.asScala)
        .map(_.map(cell => cell.toJson))
        .map(row => JsArray(row.to[Vector]))
        .to[Vector])
    }

    def read(value: JsValue): IGrid[ICell] = {
      val cells = value match {
        case JsArray(outer_vec: Vector[JsValue]) =>
          outer_vec.map {
            case JsArray(inner_vec: Vector[JsValue]) =>
              inner_vec.map(_.convertTo[ICell])
            case _ => throw DeserializationException("Rows expected")
          }
        case _ => throw DeserializationException("Grid expected")
      }

      val mines = cells.map(_.count(_.isMine)).sum[Int]

      new Grid[ICell](cells.map(_.toArray).toArray, mines)
    }
  }

}

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val cellFormat = MinesweeperProtocol.CellJsonFormat
  implicit val gridFormat = MinesweeperProtocol.GridJsonFormat
}

object GridService extends Directives with JsonSupport {
  val route: Route =
    get {
      complete(new GridFactory(2, 2).getGrid)
    } ~
      post {
        entity(as[IGrid[ICell]]) { grid =>
          complete(grid)
        }
      }
}

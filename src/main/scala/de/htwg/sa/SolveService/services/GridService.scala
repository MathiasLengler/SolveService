package de.htwg.sa.SolveService.services

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import minesweeper.model.impl.Cell
import minesweeper.model.ICell


object MyJsonProtocol extends DefaultJsonProtocol {

  implicit object CellJsonFormat extends RootJsonFormat[Cell] {
    def write(c: Cell): JsObject = {
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

    def read(value: JsValue): Cell = {
      value.asJsObject.getFields("hasMine", "isFlagged", "isRevealed", "position", "surroundingMines")
      match {
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
            case _ => throw DeserializationException("Cell expected")
          }
        case _ => throw DeserializationException("Cell expected")
      }
    }
  }

}

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val cellFormat: RootJsonFormat[Cell] = MyJsonProtocol.CellJsonFormat
}

object GridService extends Directives with JsonSupport {
  val route: Route =
      get {
        complete(new Cell(1, 2))
      } ~
        post {
          entity(as[Cell]) { cell =>
            complete(cell.mkString())
          }
        }
}

package models
import collection.mutable
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable.ArrayDeque
/* 
object ItemProtocol {
    implicit val itemWrites = new Writes[Item] {
      override def writes(item: Item): JsValue = Json.obj(
        "id" -> item.id,
        "value" -> item.value,
        "x" -> item.x,
        "y" -> item.y
      )
    }


   implicit val itemReads: Reads[Item] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "value").read[String] and
        (JsPath \ "x").read[Int] and
        (JsPath \ "y").read[Int]
      ) (Item.apply _)

} */
case class Item (id: Int, value: String, x: Int, y: Int){

}

object ItemsModel {
    var items = List(
        new Item(0, "itemA", 0, 100), 
        new Item(1, "itemB ", 400, 100)
    )

    def getItems() : List[Item] = {
        this.items
    }

    

    def addTextItem(x:String): Item ={
            val newId = ItemsModel.getItems().length
            val item = Item(newId, x, newId*300, 100)

            items = item :: items;
            item
    }

}

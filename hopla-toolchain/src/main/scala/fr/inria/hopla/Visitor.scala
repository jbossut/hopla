package fr.inria.hopla

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by JIN Benli on 26/03/14.
 */
trait Visitor

class LevelVisitor(parser: Parser) extends Visitor {

  private var _root: String = ""

  def root_(value: String) = _root = value

  def root = _root

  def visitByLevel() {
    var maxElem: Element = parser.elementList(0).asInstanceOf[Element]
    for (pa <- parser.elementList) {
      val p = pa.asInstanceOf[Element]
      if (p.ref) {
        print("Visiting reference element to element: " + p.getName + " level: " + p.level)
        if (p.level > maxElem.level)
          maxElem = p
        if (p.parent != null)
          println(" parent: " + p.parent.getName)
        else
          println()
      }
      else {
        print("Visiting element: " + p.getName + " level: " + p.level)
        if (p.level > maxElem.level)
          maxElem = p
        if (p.parent != null)
          println(" parent: " + p.parent.getName)
        else
          println()
      }
    }
    root_(maxElem.getName)
  }

  def getMaxName = root
}

class ChildVisitor(parser: Parser) extends Visitor {

  val map = new mutable.HashMap[String, ListBuffer[String]]

  def visitChild() {
    for (pa <- parser.elementList) {
      val p = pa.asInstanceOf[Element]
      val buffer = new ListBuffer[String]
      print("Visiting element: " + p.getName + " level: " + p.level)
      if (p.hasChild) {
        println(" whose child list: ")
        for (pb <- parser.elementList) {
          val px = pb.asInstanceOf[Element]
          if (px.parent == p) {
            println(px.getName)
            buffer += px.getName
          }
        }
        map.put(p.getName, buffer)
      }
      else {
        println(" has no child")
        map.put(p.getName, null)
      }
    }

  }
}

class TagVisitor(parser: Parser) extends Visitor {

  val tagBuffer: ListBuffer[String] = new ListBuffer[String]()
  val s: mutable.StringBuilder = new mutable.StringBuilder()

  def createTagsFromFile() {
    for (p <- parser.elementList) {
      val name = p.getAttributeString("name")

      //      println(name)
      if (!tagBuffer.contains(name)) {
        tagBuffer += name
      }
    }
    tagBuffer.foreach {
      case (value) =>
        s ++= "val " ++= value ++= " = \"" ++= value ++= "\".tag\n\t"
    }
  }

  def getRelationMap = {
    val childVisitor = new ChildVisitor(parser)
    childVisitor.visitChild()
    childVisitor.map
  }

  def printAllTags() {
    println(s.toString())
  }
}

class AttrVisitor(parser: Parser) extends Visitor {
  def visitAttributes(e: Schema) {
    val attributes = e.getAttributes

    //    val buffer: StringBuilder = new StringBuilder()
    for (attr <- attributes) attr.key match {
      case "name" => println("name: " + attr.value)
      case "type" => println("type: " + attr.value)
      case "minOccurs" => println("minOcccurs: " + attr.value)
      case _ =>
    }
  }
}


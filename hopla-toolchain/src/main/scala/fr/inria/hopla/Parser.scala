package fr.inria.hopla

import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.xml.Node

/**
 * Created by JIN Benli on 26/03/14.
 *
 * Simple Parser creating Element from xsd file
 *
 */
class Parser(val fileName: String) {

  // save all sequence element when traverse the xsd file
  lazy val xsdFile = scala.xml.XML.loadFile(fileName)
  // find all element node sequence
  val element = xsdFile
  val elementMap: mutable.HashMap[String, Elem] = new mutable.HashMap[String, Elem]
  val elementList: ListBuffer[Elem] = new ListBuffer[Elem]

  val simpleTypeMap: mutable.HashMap[String, SimpleType] = new mutable.HashMap[String, SimpleType]()
  val complexTypesMap: mutable.HashMap[String, ComplexType] = new mutable.HashMap[String, ComplexType]()
  val attributesGroupMap: mutable.HashMap[String, AttributesGroup] = new mutable.HashMap[String, AttributesGroup]()
  val groupMap: mutable.HashMap[String, Group] = new mutable.HashMap[String, Group]()
  val attributeMap: mutable.HashMap[String, Attribute] = new mutable.HashMap[String, Attribute]()

  val debug = false

  /**
  /**
    * Set the first element as root, creating all element from the root, using
    * ListBuffer temp for storing child list for every iteration of generation,
    * Initially the temp should be empty so we can figure out where is the root
    *
    */
  def parse() {
    //      println(e)
    val elem = new Element(element)
    elementMap ++= elem.generate
    elementMap.foreach {
      case (key, value) => {
//        println(key + " " + value)
        elementList += value
      }
    }
  }
    */

  def parser(node: Node) {
    val label: String = node.label
    /* first time declaration */
    label match {
      case "schema" =>
        for (child <- node.child) {
          parser(child)
          //          println(child)
        }
      case "attributeGroup" =>
        val attrsG = new AttributesGroup(node)
        attributesGroupMap.put(attrsG.getName, attrsG)
      case "group" =>
        val group = new Group(node)
        groupMap.put(group.getName, group)
        val elemList = group.getGroup
        elemList.foreach(elem => elementMap.put(elem.getName, elem.asInstanceOf[Element]))
      case "simpleType" =>
        val s = new SimpleType(node)
        simpleTypeMap.put(s.getName, s)
      case "complexType" =>
        val c = new ComplexType(node)
        complexTypesMap.put(c.getName, c)
      case "element" =>
        val elem = new Element(node)
        elem.handle()
        if (elem.ref) {
          elementMap.put(elem.getName + "ref", elem)
        } else {
          elementMap.put(elem.getName, elem)
        }
      case "attribute" =>
        val att = new Attribute(node)
        attributeMap.put(att.getName, att)
      case "#PCDATA" =>
      case "include" => println("There need a external xsd file: " + node.attribute("schemaLocation"))
      case _ =>
        println("Parser " + label)
    }
  }

  def handler() {
    /* second time */
    elementMap.foreach {
      case (key1, valueE) =>
        if (debug)
          println("Second time: " + key1 + " " + valueE)
        val value1 = valueE.asInstanceOf[Element]
        if (value1.ref) {
          elementMap.foreach {
            case (key2, value2) =>
              if (key2 == value1.getName)
                value1.referenceHandle(value2)
          }
        }
        if (value1.externalType) {
          // TODO choice between simple and complex need to be reconsidered
          simpleTypeMap.foreach {
            case (key4, value4) =>
              if (key4 == value1.externalTypeName)
                value1.typeHandle(value4.asInstanceOf[Type])
          }
          complexTypesMap.foreach {
            case (key3, value3) =>
              if (key3 == value1.externalTypeName)
                value1.typeHandle(value3.asInstanceOf[Type])
          }
        }
    }
    /* finish handle */
  }

  def generateList() {
    //      elementMap.foreach {
    //        case (key, value) => println(key + " " + value)
    //      }
    elementMap.foreach {
      case (key, v) =>
        val value = v.asInstanceOf[Element]
        if (debug)
          println("Element Map to List: " + key + " " + value)
        if (!elementList.contains(value) && !key.contains("ref")) {
          elementList += value
          if (debug)
            println("Add to list: " + key + " " + value)
        }
        if (value.hasChild && !key.contains("ref")) {
          elementList ++= value.getElementList
          if (debug)
            value.getElementList.foreach(e => println("Add to list from child list: "
              + e.getName + " " + e))
        }
    }
    /* finish element list generation */
  }

  def parse() {
    parser(element)
    handler()
    generateList()
  }
}

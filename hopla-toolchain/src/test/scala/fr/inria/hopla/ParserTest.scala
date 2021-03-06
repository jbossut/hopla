package fr.inria.hopla

import org.junit._
import Assert._

/**
 * Created by JIN Benli on 29/04/14.
 */
@Test
class ParserTest {
  @Test
  def lanceTest() {
    val filename1 = "hopla-toolchain/fixml-schema-4-4-20040109rev1/Schema/fixml-allocation-base-4-4.xsd"
    parserTest(filename1)
    println()

    val filename2 = "address.xsd"
    parserTest(filename2)
  }

  def parserTest(filename: String) {
    val parser = new Parser(filename)
    parser.parse()

    assertTrue(!parser.elementMap.isEmpty)
    println("\n* " + filename + " result:")
    println("- Elements: ")
    parser.elementList.foreach(e => println(e.getName +
      " level: " + e.asInstanceOf[Element].level))

    println("\n- Elements Maps: ")
    parser.elementMap.foreach {
      case (key, value) => println(key + " level: " + value.asInstanceOf[Element].level)
    }

    println("\n- Attributes: ")
    parser.attributeMap.foreach {
      case (key, value) => println(key)
    }

    println("\n- ComplexTypes: ")
    parser.complexTypesMap.foreach {
      case (key, value) => println(key)
    }

    println("\n- SimpleTypes:")
    parser.simpleTypeMap.foreach {
      case (key, value) => println(key)
    }

    println("\n- Group:")
    parser.groupMap.foreach {
      case (key, value) => println(key)
    }

    println("\n- Attributes Group:")
    parser.attributesGroupMap.foreach {
      case (key, value) =>
        println("\n" + key)
        val attributesList = value.getAttributeGroup
        var count = 0
        attributesList.foreach(attr => {
          if (count < 5) {
            print("  " + attr.getName)
            count += 1
          }
          else {
            count = 0
            println()
          }
        }
        )
    }
  }
}

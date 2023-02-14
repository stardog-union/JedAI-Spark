package SparkER.Resolvers
import SparkER.DataStructures.{KeyValue, Profile}
import SparkER.Wrappers.CSVWrapper.rowToAttributes
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.{Dataset, Row}

import java.util
import java.util.List
import java.util.stream.Collectors
import scala.collection.mutable.MutableList
import scala.collection.{JavaConverters, mutable}

class RDDEntityResolver {

//  def convertToKeyValue(row:Row):KeyValue = {
//    val aRow: GenericRowWithSchema = row.asInstanceOf[GenericRowWithSchema]
//    KeyValue(aRow.getString(0), aRow.getString(1))
//  }
//
//  def dummy():KeyValue = {
//        KeyValue("hello", "world")
//      }
//
//
//  def getKeyValuePair(row: Row): java.util.List[_root_.SparkER.DataStructures.KeyValue] = {
//    System.out.println("getKeyValuePair")
//    //		List<KeyValue> aKeyValues = new ArrayList<>();
//    System.out.println(row.schema)
//    val attributes: MutableList[KeyValue] = new MutableList()
//    row.getList(0).parallelStream().forEach(
//      attributes + convertToKeyValue(row)
//    )
//    attributes
//  }

  def getProfiles(dataset: Dataset[Row]): RDD[Profile] = {
    print("hello")
    val columnNames = dataset.columns
    println(columnNames)
    val startIDFrom = 0
    val realIDField = "iri"
    dataset.rdd.map(row => rowToAttributes(columnNames, row)).zipWithIndex().map {
      profile =>
        val profileID = profile._2.toInt + startIDFrom
        val attributes = profile._1
        print(attributes)
        attributes.map(kv => {println(kv.key)})
        val realID = {
//          if (realIDField.isEmpty) {
//            ""
//          }
//          else {
            attributes.filter(_.key.toLowerCase() == realIDField.toLowerCase()).map(_.value).mkString("").trim
//          }
        }
        Profile(profileID, attributes.filter(kv => kv.key.toLowerCase() != realIDField.toLowerCase()), realID, 0)
    }
//    dataset.rdd.zipWithIndex().map(p => {
//      System.out.println("print profile");
//      System.out.println(p._1);
//      System.out.println(p._2);
//      val list = getKeyValuePair(p._1)
//      val profile = Profile(p._2.intValue());
//      list.stream().forEach(keyVal => profile.addAttribute(keyVal))
//      profile
//    })
    //    		aProfileJavaRDD.saveAsTextFile("entitydataset");



  }
}

package SparkER.Resolvers
import SparkER.DataStructures.Profile
import SparkER.Wrappers.CSVWrapper.rowToAttributes
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row}

class RDDEntityResolver extends Serializable {

  def getProfiles(dataset: Dataset[Row], realIDField: String): RDD[Profile] = {
    val columnNames = dataset.columns
    val startIDFrom = 0

    dataset.rdd.map(row => rowToAttributes(columnNames, row)).zipWithIndex().map {
      profile =>
        val profileID = profile._2.toInt + startIDFrom
        val attributes = profile._1
        val realID = {
          if (realIDField.isEmpty) {
            ""
          }
          else {
            attributes.filter(_.key.toLowerCase() == realIDField.toLowerCase()).map(_.value).mkString("").trim
          }
        }
        Profile(profileID, attributes.filter(kv => kv.key.toLowerCase() != realIDField.toLowerCase()), realID, 0)
    }
  }
}

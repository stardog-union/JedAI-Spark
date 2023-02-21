package SparkER.Resolvers
import SparkER.DataStructures.Profile
import SparkER.Wrappers.CSVWrapper.rowToAttributes
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row}

class RDDEntityResolver {
  val REAL_ID_FIELD = "iri"
  def getProfiles(dataset: Dataset[Row]): RDD[Profile] = {
    val columnNames = dataset.columns
    val startIDFrom = 0

    dataset.rdd.map(row => rowToAttributes(columnNames, row)).zipWithIndex().map {
      profile =>
        val profileID = profile._2.toInt + startIDFrom
        val attributes = profile._1
        print(attributes)
        attributes.foreach(kv => {println(kv.key)})
        val realID = {
            attributes.filter(_.key.toLowerCase() == REAL_ID_FIELD.toLowerCase()).map(_.value).mkString("").trim
        }
        Profile(profileID, attributes.filter(kv => kv.key.toLowerCase() != REAL_ID_FIELD.toLowerCase()), realID, 0)
    }
  }
}

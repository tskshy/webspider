package app.pipeline

import app.api.DB
import org.apache.log4j.Logger
import us.codecraft.webmagic.{Task, ResultItems}
import us.codecraft.webmagic.pipeline.Pipeline

/**
 * Created by nero on 16/5/12.
 */
class NeiHanSheQu extends Pipeline {
	private val log = Logger.getLogger(this.getClass)
	override def process(result_items: ResultItems, task: Task): Unit = {
		val id		= result_items.get[String]("id")
		val detail_url	= result_items.get[String]("detail_url")
		val content	= result_items.get[String]("content")
		val img_url	= result_items.get[String]("img_url")
		val create_dt	= result_items.get[String]("create_dt")
		val status	= result_items.get[String]("status")

		DB.`with-connection` () { conn =>
			val sql =
				s"""
				INSERT INTO neihanshequ_1 (id,
				                          detail_url,
				                          content,
				                          img_url,
				                          create_dt,
				                          status)
				VALUES (?, ?, ?, ?, now()::timestamp(0) WITHOUT time ZONE, ?)
				"""
			conn.setAutoCommit(false)
			val stmt = conn.prepareStatement(sql)
			stmt.setString(1, id)
			stmt.setString(2, detail_url)
			stmt.setString(3, content)
			stmt.setString(4, img_url)
			stmt.setString(5, status)
			log.info("insert article, id: " + id + "\t detail_url: " + detail_url)
			stmt.execute()
			conn.commit()
		}
	}
}

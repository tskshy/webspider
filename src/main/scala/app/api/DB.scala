package app.api

import java.sql.{DriverManager, Connection}
import Configuration.info

/**
 * Created by nero on 16/5/12.
 */
object DB {
	val url		= info.getString("db.url")
	val username	= info.getString("db.username")
	val password	= info.getString("db.password")
	val driver	= info.getString("db.driver")

	private def `get-connection`: Connection = {
		Class.forName(driver).newInstance()
		DriverManager.getConnection(url, username, password)
	}

	def `with-connection` (conn: Connection = `get-connection`) (fn: Connection => Any): Any = {
		if (conn != null) {
			try {
				fn(conn)
			} catch {
				case ex: Throwable => println(">>>>>>>" + ex)
			} finally {
				if (conn != null) conn.close()
			}
		}
	}
}

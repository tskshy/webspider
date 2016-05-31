package app.api

import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by nero on 16/5/11.
 */
object Configuration {
	/**
	 * see more information: resources/application.conf
	 */
	//val info: Config = ConfigFactory.load("application.conf")
	val info: Config = ConfigFactory.load()

}

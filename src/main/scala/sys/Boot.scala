package sys

import java.text.SimpleDateFormat
import java.util.Date

import app.processor.NeiHanSheQu
import org.apache.log4j.Logger
import org.quartz.impl.StdSchedulerFactory
import org.quartz._

/**
 * Created by nero on 16/5/12.
 */
object Boot {
	val log = Logger.getLogger(Boot.getClass)

	def main(args: Array[String]) {
		val sc_fact = new StdSchedulerFactory()
		val sc = sc_fact.getScheduler

		sc.start()

		val job = org.quartz.JobBuilder.newJob(new Job {
			override def execute(jobExecutionContext: JobExecutionContext): Unit = {
				log.info("=======================")
				log.info(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				)
				log.info("=======================")
				NeiHanSheQu.`get-spider`.run()
			}
		}.getClass).withIdentity("spider-job", "group1").build()

		val trigger = org.quartz.TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
			.startNow()
			//.withSchedule(
			//	SimpleScheduleBuilder.simpleSchedule()
			//		.withIntervalInSeconds(10)
			//		.repeatForever()
			//)
			.withSchedule(CronScheduleBuilder.cronSchedule("0,30 * * * * ?"))
			.build()

		sc.scheduleJob(job, trigger)
	}
}


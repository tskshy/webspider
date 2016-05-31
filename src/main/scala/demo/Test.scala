package demo

import app.{QiushibaikePageProcessor, ToutiaoPageProcessor}
import us.codecraft.webmagic.pipeline.{ConsolePipeline, JsonFilePipeline}
import us.codecraft.webmagic.{Spider, Page, Site}
import us.codecraft.webmagic.processor.PageProcessor

/**
 * Created by nero on 16/5/9.
 */
object Test {
	def run (): Unit = {
		val t = new Test()
		val tt = new ToutiaoPageProcessor()
		val qsbk = new QiushibaikePageProcessor()
		Spider.create(qsbk)
			.addUrl(qsbk.start_url)
			.addPipeline(new ConsolePipeline())
			.addPipeline(new JsonFilePipeline("testresource"))
			.thread(5).run()
	}
}

class Test extends PageProcessor {
	private val site = Site.me().setRetryTimes(3).setSleepTime(1000)

	override def process (page: Page): Unit = {
		page.addTargetRequests(page.getHtml.links.regex("(https://github\\.com/\\w+/\\w+)").all)

		page.putField("author", page.getUrl.regex("https://github\\.com/(\\w+)/.*").toString)
		page.putField("name", page.getHtml.xpath("//h1[@class='entry-title public']/strong/a/text()").toString)

		if (page.getResultItems.get("name") == null) page.setSkip(true)

		page.putField("readme", page.getHtml.xpath("//div[@id='readme']/tidyText()"))
	}

	override def getSite (): Site = {
		return site
	}

}

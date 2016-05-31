package app.processor

import app.pipeline
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Logger
import us.codecraft.webmagic.pipeline.{JsonFilePipeline, ConsolePipeline}
import us.codecraft.webmagic.scheduler.component.{HashSetDuplicateRemover, BloomFilterDuplicateRemover}
import us.codecraft.webmagic.scheduler.{QueueScheduler, FileCacheQueueScheduler}
import us.codecraft.webmagic.{Spider, Site, Page}
import us.codecraft.webmagic.processor.PageProcessor

/**
 * Created by nero on 16/5/11.
 * 内涵社区数据抓取
 * HOST: http://neihanshequ.com/
 */
object NeiHanSheQu {
	val sc = new BloomFilterDuplicateRemover(10000000)
	def `get-spider` = {
		val nhsq: NeiHanSheQu = new NeiHanSheQu()
		Spider.create(nhsq)
			.addUrl(nhsq.start_url)
			//.scheduler(new FileCacheQueueScheduler("urlcache"))
			.scheduler(new QueueScheduler().setDuplicateRemover(sc))
			//.scheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
			//.addPipeline(new ConsolePipeline())
			.addPipeline(new pipeline.NeiHanSheQu())
			.thread(5)
	}
}

class NeiHanSheQu extends PageProcessor {
	private val log = Logger.getLogger(this.getClass)

	private val site = Site.me()
		//.setRetryTimes(3)
		.setSleepTime(3000)
		.setUserAgent(app.api.Configuration.info.getString("user_agent"))
		.addCookie("neihanshequ.com", "csrftoken=c181f2245c70cb42eb1cc7f710f09eda; tt_webid=15992028713; uuid=\"w:33f97536e4974c8fb1b15fd3472976f0\"; _ga=GA1.2.1593207238.1463456402; Hm_lvt_773f1a5aa45c642cf87eef671e4d3f6a=" + System.currentTimeMillis())

	val u = scala.util.Random.nextInt(2) match {
		case 0 => ""
		case 1 => "pic/"
	}
	val start_url = "http://neihanshequ.com/" + u + "?t=" + System.currentTimeMillis()

	math.random

	override def process (page: Page): Unit = {
		page.getUrl.regex("^http://neihanshequ.com/(pic/){0,1}\\?t=\\d+$").`match`() match {
			case true	=> processList(page)
			case false	=> processDetail(page)
		}
	}

	private def processList (page: Page): Unit = {
		page.setSkip(true)
		val url_select = "#detail-list li div.detail-wrapper div.content-wrapper a.image.share_url"
		page.getHtml.$(url_select).xpath("//a/@href").all().toArray map { url =>
			page.addTargetRequest(url.toString)
		}
		//page.addTargetRequest("http://neihanshequ.com/p6275564852/")
	}

	private def processDetail (page: Page): Unit = {
		val html = page.getHtml

		val id = DigestUtils.md5Hex(page.getRequest.getUrl)
		page.putField("id", id)
		//val user_select = "div.detail-wrapper div.header div.name-time-wrapper.left span.name"
		//val user_name = html.$(user_select).xpath("//span/text()").get()
		//page.putField("user_name", user_name)

		val detail_url = page.getRequest.getUrl
		page.putField("detail_url", detail_url)

		val content_select = "div.detail-wrapper div.content-wrapper a div.upload-txt h1.title p"
		val content = html.$(content_select).xpath("//p/text()").get()
		page.putField("content", content match {case null => ""; case _ => content})

		val img_select = "div.detail-wrapper div.content-wrapper a div.img-wrapper img"
		val img_url = html.$(img_select).xpath("//img/@src").get()
		page.putField("img_url", img_url match {case null => ""; case _ => img_url})

		page.putField("create_dt", "")
		page.putField("status", "N")

	}

	override def getSite (): Site = {
		log.info(site.getCookies.toString)
		return site
	}
}

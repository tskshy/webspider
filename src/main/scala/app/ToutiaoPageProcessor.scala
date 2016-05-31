package app

import us.codecraft.webmagic.{Site, Page}
import us.codecraft.webmagic.processor.PageProcessor

/**
 * Created by nero on 16/5/10.
 * 今日头条
 */
class ToutiaoPageProcessor extends PageProcessor {
	private val site = Site.me()
		.setRetryTimes(3)
		.setSleepTime(3000)
		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")

	val url = "http://www.toutiao.com/api/article/recent/"

	override def process (page: Page): Unit = {
		if (page.getUrl.regex(url).`match`()) {

			page.getJson.jsonPath("data").all().toArray.map {e =>
				println(e)
				println(">>>>>>>>>>")
			}
			page.addTargetRequest("http://toutiao.com/group/6277451851743281409/")
		} else {
			//page.addTargetRequests(page.getHtml().links().regex("http://www.toutiao.com/a\\d+/").all())
			page.putField("url", page.getHtml.toString)
		}
	}

	override def getSite (): Site = {
		return site;
	}
}

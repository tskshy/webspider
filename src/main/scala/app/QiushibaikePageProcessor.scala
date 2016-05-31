package app

import org.apache.commons.codec.digest.DigestUtils
import us.codecraft.webmagic.{Page, Site}
import us.codecraft.webmagic.processor.PageProcessor

/**
 * Created by nero on 16/5/11.
 * 糗事百科
 */
class QiushibaikePageProcessor extends PageProcessor {
	private val site = Site.me()
		.setRetryTimes(3)
		.setSleepTime(3000)
		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")

	val start_url = "http://www.qiushibaike.com/8hr/page/1"
	//val start_url = "http://www.qiushibaike.com/textnew/"

	private val regex_url = "http://www.qiushibaike.com/article/\\d+"

	override def process (page: Page): Unit = {
		page.getUrl.regex(regex_url).`match`() match {
			case false	=> processList(page)
			case true	=> processDetail(page)
		}
	}

	private def processList (page: Page): Unit = {
		/**
		 * 选择有评论的
		 * 因为有评论的才有详情链接
		 * 方便统一处理
		 */
		val css_select = "#content-left div.article.block.untagged.mb15 div.stats span.stats-comments a.qiushi_comments"
		page.getHtml.$(css_select).xpath("//a/@href").all().toArray map { e =>
			page.addTargetRequest(e.toString)
		}
		//page.addTargetRequest("http://www.qiushibaike.com/article/116297657")//message
		//page.addTargetRequest("http://www.qiushibaike.com/article/116297951")//message and picture

	}

	private def processDetail (page: Page): Unit = {
		page.putField("id", DigestUtils.md5Hex(page.getRequest.getUrl))

		val user_name_css_select = "div.author.clearfix a h2"
		val user_name = page.getHtml.$(user_name_css_select).xpath("//h2/text()").get()
		page.putField("user_name", user_name)

		val content_css_select = "#single-next-link div.content"
		val content = page.getHtml.$(content_css_select).xpath("//div/text()").get()
		page.putField("content", content)

		val img_css_select = "#single-next-link div.thumb img"
		val img_url = page.getHtml.$(img_css_select).xpath("//img/@src").get()
		page.putField("img_url", img_url match {case null => "404"; case _ => img_url})

		page.putField("create_dt", "now()")
		page.putField("status", "Y")
	}

	override def getSite (): Site = {
		return site;
	}
}

package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import io.jyp.crawler.repository.MemberRepository
import io.jyp.crawler.util.HtmlParser
import kotlinx.coroutines.*;
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class NoticeCrawlerService(
    private val memberRepository: MemberRepository,
    private val emailService: EmailService
) {
    private val log = LoggerFactory.getLogger(NoticeCrawlerService::class.java)

    suspend fun checkTodayNotice() = coroutineScope {
        try {
            val noticeList = mutableListOf<String>()

            // 1~3페이지 공지사항을 비동기로 크롤링
            val urls = listOf(
                "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=1&searchType=0&searchWrd=",
                "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=2&searchType=0&searchWrd=",
                "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=3&searchType=0&searchWrd="
            )

            val crawlJobs = urls.map { url ->
                async { crawlNotices(url) }
            }

            // 모든 크롤링 작업 완료 후 결과 병합
            crawlJobs.awaitAll().forEach { noticeList.addAll(it) }

            // 공지사항이 있으면 이메일 발송
            if (noticeList.isNotEmpty()) {
                val htmlContent = HtmlParser.createNoticeInfoHtml(noticeList)
                val members = memberRepository.findByNoticeFlagOrderByIdDesc(true)
                notifyNoticeMembers(htmlContent, members)
                log.info("Notice Send")
            } else {
                log.info("There is No Notice Today")
            }
        } catch (e: IOException) {
            log.error("Error Notice Crawling", e)
        }
    }

    private suspend fun crawlNotices(url: String): List<String> {
        val noticeList = mutableListOf<String>()

        try {
            val doc: Document = Jsoup.connect(url).get()
            val notices: Elements = doc.select(".tbl-body .tbl-row")

            for (notice in notices) {
                val date = notice.select(".col-5 span").text().trim().replace("등록일 ", "")
                if (isToday(date)) {
                    val title = notice.select(".col-2 a").text().trim()
                    val link = notice.select(".col-2 a").attr("href").trim()
                    val author = notice.select(".col-3 span").text().trim().replace("작성자 ", "")

                    val noticeInfo = HtmlParser.createNoticeRowHtml(title, link, author)
                    noticeList.add(noticeInfo)
                }
            }
        } catch (e: Exception) {
            log.error("크롤링 실패: URL = $url", e)
        }

        return noticeList
    }

    private fun isToday(date: String, currentDate: LocalDate = LocalDate.now()): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val noticeDate = LocalDate.parse(date, formatter)
        return noticeDate.isEqual(currentDate)
    }

    suspend fun notifyNoticeMembers(noticeInfo: String, members: List<Member>, maxRetries: Int = 3) {
        val chunkSize = 100
        val memberChunks = members.chunked(chunkSize)
        val failedIndexes = mutableListOf<Int>()
    
        memberChunks.forEachIndexed { index, chunk ->
            try {
                emailService.sendBulkEmail(chunk, noticeInfo)
            } catch (e: Exception) {
                failedIndexes.add(index)
            }
        }
    
        var attempt = 1
        while (failedIndexes.isNotEmpty() && attempt <= maxRetries) {
            delay(15000L * 2.0.pow(attempt - 1).toLong()) // 지수 백오프
            val current = failedIndexes.toList()
            failedIndexes.clear()
    
            current.forEach { index ->
                try {
                    emailService.sendBulkEmail(memberChunks[index], noticeInfo)
                } catch (e: Exception) {
                    failedIndexes.add(index)
                }
            }
            attempt++
        }
    }


}

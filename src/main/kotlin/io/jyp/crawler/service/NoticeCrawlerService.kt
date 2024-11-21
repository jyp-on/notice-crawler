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

            // 1페이지와 2페이지 공지사항을 비동기로 크롤링
            val urls = listOf(
                "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=1&searchType=0&searchWrd=",
                "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=2&searchType=0&searchWrd="
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
                log.info("당일 공지사항이 이메일로 발송되었습니다.")
            } else {
                log.info("오늘의 새로운 공지사항이 없습니다.")
            }
        } catch (e: IOException) {
            log.error("공지사항 페이지를 불러오는 중 오류 발생", e)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun notifyNoticeMembers(noticeInfo: String, members: List<Member>) = coroutineScope {
        val emailDispatcher = Dispatchers.IO.limitedParallelism(20)

        // 각 멤버의 이메일 발송을 병렬로 처리
        val jobs = members.map { member ->
            launch(emailDispatcher) {
                retrySendEmail(member, noticeInfo)
            }
        }

        // 모든 작업 완료 대기
        jobs.joinAll()
    }

    private suspend fun retrySendEmail(member: Member, noticeInfo: String) {
        var attempt = 0
        while (attempt < 10) {
            try {
                emailService.sendEmail(member, noticeInfo)
                log.info("[이메일 발송 성공] {} {}", member.email, member.id)
                return
            } catch (e: Exception) {
                attempt++
                log.warn(
                    "[이메일 발송 재시도] 시도 {}회 | 오류: {} | 이메일: {} | ID: {}",
                    attempt,
                    e.message,
                    member.email,
                    member.id
                )
                val waitTime = (1 shl attempt) * 1500L // 지수 백오프 방식
                delay(waitTime) // 코루틴 친화적 대기
            }
        }
        log.error("이메일 발송 실패: {}", member.email)
    }
}

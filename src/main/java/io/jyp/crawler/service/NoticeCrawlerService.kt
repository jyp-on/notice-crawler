package io.jyp.crawler.service

import io.jyp.crawler.entity.Member
import io.jyp.crawler.repository.MemberRepository
import io.jyp.crawler.util.HtmlParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class NoticeCrawlerService(
    private val memberRepository: MemberRepository,
    private val emailService: EmailService
) {

    private val emailExecutor: ExecutorService = Executors.newFixedThreadPool(10) // 테스트 결과 적정 개수 10개
    private val log = LoggerFactory.getLogger(NoticeCrawlerService::class.java)

    fun checkTodayNotice() {
        try {
            val noticeList = mutableListOf<String>()

            // 1페이지와 2페이지에서 공지사항을 수집
            val mainP1 = "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=1&searchType=0&searchWrd="
            val mainP2 = "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=2&searchType=0&searchWrd="
            crawlNotices(mainP1, noticeList)
            crawlNotices(mainP2, noticeList)

            // 수집한 공지사항이 있으면 이메일 발송
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

    private fun crawlNotices(url: String, noticeList: MutableList<String>) {
        val doc: Document = Jsoup.connect(url).get()
        val notices: Elements = doc.select(".tbl-body .tbl-row")

        for (notice in notices) {
            val date = notice.select(".col-5 span").text().trim().replace("등록일 ", "")

            if (isToday(date)) {
//            if (isToday(date, LocalDate.of(2024, 11, 15))) {
                val title = notice.select(".col-2 a").text().trim()
                val link = notice.select(".col-2 a").attr("href").trim()
                val author = notice.select(".col-3 span").text().trim().replace("작성자 ", "")

                val noticeInfo = HtmlParser.createNoticeRowHtml(title, link, author)
                noticeList.add(noticeInfo)
            }
        }
    }

    private fun isToday(date: String): Boolean {
        return isToday(date, LocalDate.now())
    }

    private fun isToday(date: String, currentDate: LocalDate): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val noticeDate = LocalDate.parse(date, formatter)
        return noticeDate.isEqual(currentDate)
    }

    fun notifyNoticeMembers(noticeInfo: String, members: List<Member>) {
        val futures = members.map { member ->
            CompletableFuture.runAsync({
                retrySendEmail(member, noticeInfo) // 최대 10회 재시도
            }, emailExecutor)
        }

        // 모든 비동기 작업들이 완료될 때까지 대기
        CompletableFuture.allOf(*futures.toTypedArray()).join()
    }

    private fun retrySendEmail(member: Member, noticeInfo: String) {
        var attempt = 0
        while (attempt < 10) {
            try {
                emailService.sendEmail(member, noticeInfo)
                log.info("[이메일 발송 성공] {} {}", member.email, member.id)
                return // 성공 시 종료
            } catch (e: Exception) {
                attempt++
                log.warn(
                    "[이메일 발송 재시도] 시도 {}회 | 오류: {} | 이메일: {} | ID: {}",
                    attempt,
                    e.message,
                    member.email,
                    member.id
                )
                val waitTime = (1 shl attempt) * 15000L // 지수 백오프 방식
                try {
                    Thread.sleep(waitTime)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    log.error("재시도 중 인터럽트 발생 - {}", member.email)
                    return
                }
            }
        }
        log.error("이메일 발송 실패: {}", member.email)
    }
}

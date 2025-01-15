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

    fun notifyNoticeMembers(noticeInfo: String, members: List<Member>, maxRetries: Int = 3) {
        val chunkSize = 100 // 한 번에 발송 가능한 최대 수신자 수
        val memberChunks = members.chunked(chunkSize) // 100명씩 나누기
        val failedIndexes = mutableListOf<Int>() // 실패한 그룹의 인덱스를 저장

        // 첫 번째 시도
        memberChunks.forEachIndexed { index, chunk ->
            try {
                emailService.sendBulkEmail(chunk, noticeInfo)
            } catch (e: Exception) {
                log.error(
                    "[그룹 이메일 발송 실패] 그룹 크기: {} | 그룹 순서: {} | 오류: {}",
                    chunk.size, index, e.message
                )
                failedIndexes.add(index) // 실패한 인덱스 추가
            }
        }

        // 실패한 그룹 재시도
        var attempt = 1
        while (failedIndexes.isNotEmpty() && attempt <= maxRetries) {
            log.info("재시도 시도 {}회 - 실패한 그룹 수: {}", attempt, failedIndexes.size)

            val currentFailedIndexes = failedIndexes.toList() // 현재 실패한 인덱스를 복사
            failedIndexes.clear() // 재시도 전 초기화

            currentFailedIndexes.forEach { index ->
                val chunk = memberChunks[index]
                try {
                    emailService.sendBulkEmail(chunk, noticeInfo)
                    log.info("[그룹 이메일 재시도 성공] 그룹 순서: {}", index)
                } catch (e: Exception) {
                    log.error(
                        "[그룹 이메일 재시도 실패] 그룹 크기: {} | 그룹 순서: {} | 오류: {}",
                        chunk.size, index, e.message
                    )
                    failedIndexes.add(index) // 다시 실패한 그룹 추가
                }
            }
            attempt++
        }

        // 최종 실패 결과
        if (failedIndexes.isNotEmpty()) {
            log.error("최종적으로 실패한 그룹 인덱스: {}", failedIndexes)
        }
    }

}

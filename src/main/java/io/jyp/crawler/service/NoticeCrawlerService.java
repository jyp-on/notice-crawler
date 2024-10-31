package io.jyp.crawler.service;

import static io.jyp.crawler.util.HtmlParser.createNoticeInfoHtml;
import static io.jyp.crawler.util.HtmlParser.createNoticeRowHtml;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import io.jyp.crawler.service.EmailService;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class NoticeCrawlerService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // 생성자를 통한 의존성 주입
    public NoticeCrawlerService(MemberRepository memberRepository, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.emailService = emailService;
    }

    public void checkTodayMainNotice() {
        try {
            List<String> noticeList = new ArrayList<>();

            // 1페이지와 2페이지에서 공지사항을 수집
            String main_p1 = "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=1&searchType=0&searchWrd=";
            String main_p2 = "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html?nttId=0&bbsTyCode=BBST00&bbsAttrbCode=BBSA03&authFlag=N&pageIndex=2&searchType=0&searchWrd=";
            crawlNotices(main_p1, noticeList);
            crawlNotices(main_p2, noticeList);

            // 수집한 공지사항이 있으면 이메일 발송
            if (!noticeList.isEmpty()) {
                String htmlContent = createNoticeInfoHtml(noticeList);
                notifyNoticeMembers(htmlContent, "MAIN");
                log.info("당일 공지사항이 이메일로 발송되었습니다.");
            } else {
                log.info("오늘의 새로운 공지사항이 없습니다.");
            }
        } catch (IOException e) {
            log.error("공지사항 페이지를 불러오는 중 오류 발생", e);
        }
    }

    // URL을 사용하여 공지사항을 크롤링하고 당일 공지만 noticeList에 추가하는 메서드
    private void crawlNotices(String url, List<String> noticeList) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements notices = doc.select(".tbl-body .tbl-row");

        for (Element notice : notices) {
            String date = notice.select(".col-5 span").text().trim().replace("등록일 ", "");

            if (isToday(date)) {
//            if(isToday(date, LocalDate.of(2024, 10, 31))) { // Test
                String title = notice.select(".col-2 a").text().trim();
                String link = notice.select(".col-2 a").attr("href").trim();
                String author = notice.select(".col-3 span").text().trim().replace("작성자 ", "");

                String noticeInfo = createNoticeRowHtml(title, link, author);
                noticeList.add(noticeInfo);
            }
        }
    }

    public boolean isToday(String date) {
        return isToday(date, LocalDate.now());
    }

    public boolean isToday(String date, LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate noticeDate = LocalDate.parse(date, formatter);
        return noticeDate.isEqual(currentDate);
    }

    private void notifyNoticeMembers(String noticeInfo, String noticeType) {
        List<Member> mainNoticeMembers = memberRepository.findByNoticeTypeAndNoticeFlag(noticeType, true);
        for (Member member : mainNoticeMembers) {
            try {
                emailService.sendEmail(member, noticeInfo);
                log.info("[이메일 발송] {}", member.getEmail());
            } catch (MessagingException e) {
                log.error("[이메일 발송 실패] {}", member.getEmail(), e);
            }
        }
    }
}

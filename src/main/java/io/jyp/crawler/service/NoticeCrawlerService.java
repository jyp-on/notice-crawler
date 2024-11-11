package io.jyp.crawler.service;

import static io.jyp.crawler.util.HtmlParser.createNoticeInfoHtml;
import static io.jyp.crawler.util.HtmlParser.createNoticeRowHtml;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final ExecutorService emailExecutor = Executors.newFixedThreadPool(10); // 테스트 결과 적정개수 10개
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // 생성자를 통한 의존성 주입
    public NoticeCrawlerService(MemberRepository memberRepository, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.emailService = emailService;
    }

    public void checkTodayNotice() {
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
                List<Member> members = memberRepository.findByNoticeFlagOrderByIdDesc(true);
                notifyNoticeMembers(htmlContent, members);
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

    public void notifyNoticeMembers(String noticeInfo, List<Member> members) {
        List<CompletableFuture<Void>> futures = members.stream()
            .map(member -> CompletableFuture.runAsync(() -> {
                // 예외 처리를 모두 내부에서 수행하도록 수정
                retrySendEmail(member, noticeInfo, 10); // 최대 10회 재시도
            }, emailExecutor))
            .toList();

        // 모든 비동기 작업들이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    // 재시도 로직을 포함한 이메일 발송 메서드
    private void retrySendEmail(Member member, String noticeInfo, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                emailService.sendEmail(member, noticeInfo);
                log.info("[이메일 발송 성공] {} {}", member.getEmail(), member.getId());
                return; // 성공 시 종료
            } catch (Exception e) {
                attempt++;
                log.warn("[이메일 발송 재시도] {} {} - 시도 {}회", member.getEmail(), member.getId(), attempt);
                long waitTime = (long) Math.pow(2, attempt) * 15000; // 지수 백오프 방식 (30초, 60초, 120초, 240초, 480초)
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("재시도 중 인터럽트 발생 - {}", member.getEmail());
                    return;
                }
            }
        }
        log.error("오류로 인해 이메일 발송 실패: {}", member.getEmail());
    }
}

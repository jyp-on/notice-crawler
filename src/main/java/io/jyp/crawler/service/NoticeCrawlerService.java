package io.jyp.crawler.service;

import static io.jyp.crawler.util.HtmlParser.createNoticeInfoHtml;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeCrawlerService {

    private final StringRedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    private final String main = "https://www.hallym.ac.kr/hallym_univ/sub05/cP3/sCP1.html";
    private final String soft = "https://sw.hallym.ac.kr/index.php";

    public void checkMainNotice() {
        try {
            // 1. 공지사항 페이지 크롤링
            Document doc = Jsoup.connect(main).get();
            Element firstNotice = doc.selectFirst(".tbl-body .tbl-row");

            String noticeId = firstNotice.select(".col-1 span").text().trim();
            String title = firstNotice.select(".col-2 a").text().trim();
            String link = firstNotice.select(".col-2 a").attr("href").trim();
            String author = firstNotice.select(".col-3 span").get(1).text().trim();
            String date = firstNotice.select(".col-5 span").get(1).text().trim();

            // 2. Redis에서 마지막 공지 ID와 비교
            String lastNoticeId = redisTemplate.opsForValue().get("notice:main");
            if (!noticeId.equals(lastNoticeId)) {
                redisTemplate.opsForValue().set("notice:main", noticeId); // 새로운 공지 ID 갱신
                String noticeInfo = createNoticeInfoHtml(title, link, author, date); // 이메일 내용 생성
                notifyMainNoticeMembers(noticeInfo, "MAIN");
                log.info("[MAIN] 새로운 공지사항이 발견되어 이메일을 발송했습니다.");
            } else {
                log.info("[MAIN] 새로운 공지사항이 없습니다. 공지 ID : " + noticeId);
            }
        } catch (IOException e) {
            log.error("공지사항 페이지를 불러오는 중 오류 발생", e);
        }
    }

    public void checkSoftNotice() {
        try {
            // 1. 공지사항 페이지 크롤링
            Document doc = Jsoup.connect(soft).get();

            // 공지사항 목록의 첫 번째 공지사항을 선택
            Element firstNotice = doc.selectFirst(".latest .tab.t1 ul li"); // CSS 선택자 수정

            // 공지사항의 ID, 제목, 링크, 등록일 추출
            String noticeId = firstNotice.select("a").attr("href").split("BID=")[1].split("&")[0].trim(); // BID 값 추출
            String title = firstNotice.select("a").text().trim();
            String link = "https://sw.hallym.ac.kr/" + firstNotice.select("a").attr("href").trim(); // 링크에 도메인 추가
            String date = firstNotice.select(".date").text().trim();

            // 2. Redis에서 마지막 공지 ID와 비교
            String lastNoticeId = redisTemplate.opsForValue().get("notice:soft"); // Redis 키를 변경
            if (!noticeId.equals(lastNoticeId)) {
                redisTemplate.opsForValue().set("notice:soft", noticeId); // 새로운 공지 ID 갱신
                String noticeInfo = createNoticeInfoHtml(title, link, "관리자", date); // 이메일 내용 생성
                notifyMainNoticeMembers(noticeInfo, "SOFT"); // 알림 발송
                log.info("[SOFT] 새로운 공지사항이 발견되어 이메일을 발송했습니다.");
            } else {
                log.info("[SOFT] 새로운 공지사항이 없습니다. 공지 ID : " + noticeId);
            }
        } catch (IOException e) {
            log.error("공지사항 페이지를 불러오는 중 오류 발생", e);
        }
    }



    private void notifyMainNoticeMembers(String noticeInfo, String noticeType) {
        List<Member> mainNoticeMembers = memberRepository.findByNoticeTypeAndNoticeFlag(noticeType, true);
        for (Member member : mainNoticeMembers) {
            try {
                emailService.sendEmail(member, noticeInfo);
                log.info("이메일 발송: {}", member.getEmail());
            } catch (MessagingException e) {
                log.error("이메일 발송 실패: {}", member.getEmail(), e);
            }
        }
    }
}

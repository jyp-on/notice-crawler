package io.jyp.crawler.service;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.repository.MemberRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
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
                log.info("새로운 공지사항이 발견되어 이메일을 발송했습니다.");
            } else {
                log.info("최신 공지사항 ID : " + lastNoticeId);
                log.info("새로운 공지사항이 없습니다. 공지 ID : " + noticeId);
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
                log.info("새로운 공지사항이 발견되어 이메일을 발송했습니다.");
            } else {
                log.info("최신 공지사항 ID : " + lastNoticeId);
                log.info("새로운 공지사항이 없습니다. 공지 ID : " + noticeId);
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

    // 이메일 내용 생성 메서드
    private String createNoticeInfoHtml(String title, String link, String author, String date) {
        return """
        <html>
            <body>
                <h2 style="color: #4CAF50; font-family: Arial, sans-serif;">새로운 공지사항이 있습니다!</h2>
                <table style="border-collapse: collapse; width: 100%%; font-family: Arial, sans-serif;">
                    <tr>
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">제목</th>
                        <td style="border: 1px solid #ddd; padding: 8px;">%s</td>
                    </tr>
                    <tr style="background-color: #f2f2f2;">
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">URL</th>
                        <td style="border: 1px solid #ddd; padding: 8px;">
                            <a href="%s" style="text-decoration: none; color: #2196F3;">공지사항 바로가기</a>
                        </td>
                    </tr>
                    <tr>
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">작성자</th>
                        <td style="border: 1px solid #ddd; padding: 8px;">%s</td>
                    </tr>
                    <tr style="background-color: #f2f2f2;">
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">등록일</th>
                        <td style="border: 1px solid #ddd; padding: 8px;">%s</td>
                    </tr>
                </table>
                <hr>
                <p style="font-size: 0.9em; color: #666;">이 메일은 자동으로 발송되었습니다.</p>
                <p style="font-size: 0.9em; color: #666;">
                    문의사항이 있으시면 
                    <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">이메일</a>로 연락해 주세요.
                </p>
                <p style="font-size: 0.9em; color: #666;">
                    GitHub: <a href="https://github.com/jyp-on" style="text-decoration: none; color: #2196F3;">https://github.com/jyp-on</a>
                </p>
                <p style="font-size: 0.9em; color: #666;">
                    Email: <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">ju0_park@naver.com</a>
                </p>
            </body>
        </html>
        """.formatted(title, link, author, date);
    }

}

package io.jyp.crawler;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class multiThreadingTestRunner {

    @Autowired
    private EmailService emailService;

    @Test
    void multiThreadingTest() {
        runTestWithThreads(30);
    }

    // EmailService Mocking 하여 멀티스레딩 테스트
    private void runTestWithThreads(int threadCount) {
        ExecutorService emailExecutor = Executors.newFixedThreadPool(threadCount); // 스레드 개수 설정

        // 100명의 더미 Member 데이터 생성
        List<Member> dummyMembers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dummyMembers.add(Member.builder()
                .email("ju" + i + "_park@naver.com")
                .noticeFlag(true)
                .noticeType("MAIN")
                .build());
        }

        long startTime = System.nanoTime();

        // CompletableFuture 리스트를 만들어 모든 작업이 완료될 때까지 기다림
        List<CompletableFuture<Void>> futures = dummyMembers.stream()
            .map(member -> CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(member, "test");
                    log.info("[이메일 발송] {}", member.getEmail());
                } catch (MessagingException e) {
                    log.error("[이메일 발송 실패] {}", member.getEmail(), e);
                    throw new RuntimeException(e); // 예외를 명시적으로 던져 CompletionException의 원인을 알 수 있도록 함
                }
            }, emailExecutor))
            .toList();

        // 모든 작업이 끝날 때까지 기다림
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.nanoTime();
        double executionTimeSeconds = (endTime - startTime) / 1_000_000_000.0; // 초 단위로 변환
        log.info("Thread Count: {}, Execution Time: {} seconds", threadCount, executionTimeSeconds);

        // ExecutorService 종료
        emailExecutor.shutdown();
    }
}

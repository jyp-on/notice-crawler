package io.jyp.crawler;

import io.jyp.crawler.entity.Member;
import io.jyp.crawler.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;

@Slf4j
class MultiThreadingTestRunner {

    @Test
    void multiThreadingTest() throws MessagingException {
        for (int threadCount = 30; threadCount <= 50; threadCount += 10) {
            runTestWithThreads(threadCount);
        }
    }

    // EmailService Mocking 하여 멀티스레딩 테스트
    private void runTestWithThreads(int threadCount) throws MessagingException {
        ExecutorService emailExecutor = Executors.newFixedThreadPool(threadCount); // 스레드 개수 설정
        // Mocking EmailService
        EmailService mockEmailService = Mockito.mock(EmailService.class);

        // Mock 객체의 sendEmail 메서드 호출 시 3초의 지연을 추가
        doAnswer(invocation -> {
            Thread.sleep(3000); // 3초 지연
            return null;
        }).when(mockEmailService).sendEmail(any(Member.class), anyString());

        // 100명의 더미 Member 데이터 생성
        List<Member> dummyMembers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
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
                    mockEmailService.sendEmail(member, "test");
                    log.info("[이메일 발송] {}", member.getEmail());
                } catch (MessagingException e) {
                    log.error("[이메일 발송 실패] {}", member.getEmail(), e);
                }
            }, emailExecutor))
            .toList();

        // 모든 작업이 끝날 때까지 기다림
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.nanoTime();
        double executionTimeSeconds = (endTime - startTime) / 1_000_000_000.0; // 초 단위로 변환
        log.info("Thread Count: {}, Execution Time: {:.2f} seconds", threadCount, executionTimeSeconds);

        // Mock 객체의 sendEmail 호출 횟수를 검증
        Mockito.verify(mockEmailService, times(100)).sendEmail(any(Member.class), eq("test"));

        // ExecutorService 종료
        emailExecutor.shutdown();
    }
}

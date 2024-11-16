package io.jyp.crawler.util;

import java.util.List;

public class HtmlParser {

    private HtmlParser() {
        // 유틸리티 클래스는 인스턴스화하지 않음
    }

    public static String createNoticeRowHtml(String title, String link, String author) {
        return """
            <tr>
                <td style="border: 1px solid #ddd; padding: 12px;">
                    <a href="%s" style="text-decoration: none; color: #2196F3;">%s</a>
                </td>
                <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
            </tr>
        """.formatted(link, title, author);
    }

    public static String createNoticeInfoHtml(List<String> noticeRows) {
        return """
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>새로운 공지사항</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; text-align: center;">
                <div style="max-width: 800px; margin: 50px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1); text-align: left;">
                    <div style="border-top: 4px solid #4CAF50; border-radius: 10px 10px 0 0; padding-top: 10px;"></div>
                    <div style="text-align: center; padding-bottom: 20px; border-bottom: 1px solid #ddd; margin-top: 10px;">
                        <h2 style="color: #4CAF50; font-family: Arial, sans-serif; font-size: 24px;">오늘의 공지사항!</h2>
                    </div>
                    <table style="border-collapse: collapse; width: 100%%; font-family: Arial, sans-serif; margin-top: 30px;">
                        <tr>
                            <th style="border: 1px solid #ddd; padding: 12px; background-color: #f9f9f9; color: #333;">제목</th>
                            <th style="border: 1px solid #ddd; padding: 12px; background-color: #f9f9f9; color: #333;">작성자</th>
                        </tr>
                        %s
                    </table>
                    <div style="text-align: center; padding-top: 20px; margin-top:30px; border-top: 1px solid #ddd;">
                        <p style="font-size: 12px; color: #999;">문의사항이 있으시면 아래 이메일로 연락해 주세요.</p>
                        <p style="font-size: 12px; color: #999;">Email: <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">ju0_park@naver.com</a></p>
                        <p style="font-size: 12px; color: #999;">SourceCode: <a href="https://github.com/jyp-on/notice-crawler" style="text-decoration: none; color: #2196F3;">https://github.com/jyp-on/notice-crawler</a></p>
                    </div>
                    <div style="border-top: 4px solid #4CAF50; border-radius: 0 0 10px 10px; margin-top: 20px;"></div>
                </div>
            </body>
            </html>
        """.formatted(String.join("\n", noticeRows));
    }

    public static String createVerifyEmailHtml(String authCode) {
        return """
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>이메일 인증 요청</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; text-align: center;">
                <div style="max-width: 600px; margin: 50px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1); text-align: left;">
                    <div style="border-top: 4px solid #4CAF50; border-radius: 10px 10px 0 0; padding-top: 10px;"></div>
                    <div style="text-align: center; padding-bottom: 20px; border-bottom: 1px solid #ddd; margin-top: 10px;">
                        <h1 style="color: #4CAF50; font-family: Arial, sans-serif; font-size: 24px;">이메일 인증 요청</h1>
                    </div>
                    <div style="padding: 25px 0; text-align: center;">
                        <p style="line-height: 1.6; color: #666; font-size: 16px;">아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요:</p>
                        <span style="display: inline-block; padding: 12px 18px; margin: 20px auto; background: #f0f0f0; border: 1px solid #ddd; border-radius: 8px; font-weight: bold; color: #333; font-family: 'Courier New', Courier, monospace; font-size: 20px;">%s</span>
                        <p style="line-height: 1.6; color: #666; font-size: 16px;">인증 코드는 5분 동안만 유효하니, 그 안에 인증을 완료해 주세요.</p>
                    </div>
                    <div style="text-align: center; padding-top: 20px; border-top: 1px solid #ddd;">
                        <p style="font-size: 12px; color: #999;">문의사항이 있으시면 아래 이메일로 연락해 주세요.</p>
                        <p style="font-size: 12px; color: #999;">Email: <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">ju0_park@naver.com</a></p>
                        <p style="font-size: 12px; color: #999;">SourceCode: <a href="https://github.com/jyp-on/notice-crawler" style="text-decoration: none; color: #2196F3;">https://github.com/jyp-on/notice-crawler</a></p>
                    </div>
                    <div style="border-top: 4px solid #4CAF50; border-radius: 0 0 10px 10px; margin-top: 20px;"></div>
                </div>
            </body>
            </html>
        """.formatted(authCode);
    }
}

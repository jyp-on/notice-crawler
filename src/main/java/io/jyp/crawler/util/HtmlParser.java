package io.jyp.crawler.util;

public class HtmlParser {

    private HtmlParser() {
        // 유틸리티 클래스는 인스턴스화하지 않음
    }

    public static String createNoticeInfoHtml(String title, String link, String author, String date) {
        return """
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>새로운 공지사항</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; text-align: center;">
                <div style="max-width: 600px; margin: 50px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: left;">
                    <div style="text-align: center; padding-bottom: 20px; border-bottom: 1px solid #ddd;">
                        <h2 style="color: #4CAF50; font-family: Arial, sans-serif;">새로운 공지사항이 있습니다!</h2>
                    </div>
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
                    <div style="text-align: center; padding-top: 20px;">
                        <p style="font-size: 0.9em; color: #666;">이 메일은 자동으로 발송되었습니다.</p>
                        <p style="font-size: 0.9em; color: #666;">문의사항이 있으시면 <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">이메일</a>로 연락해 주세요.</p>
                        <p style="font-size: 0.9em; color: #666;">GitHub: <a href="https://github.com/jyp-on" style="text-decoration: none; color: #2196F3;">https://github.com/jyp-on</a></p>
                        <p style="font-size: 0.9em; color: #666;">Email: <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">ju0_park@naver.com</a></p>
                    </div>
                </div>
            </body>
            </html>
                    
        """.formatted(title, link, author, date);
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
                    <div style="max-width: 600px; margin: 50px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: left;">
                        <div style="text-align: center; padding-bottom: 20px; border-bottom: 1px solid #ddd;">
                            <h1 style="color: #333;">이메일 인증 요청</h1>
                        </div>
                        <div style="padding: 20px 0; text-align: center;">
                            <p style="line-height: 1.6; color: #666;">아래의 인증 코드를 사용하여 이메일 인증을 완료해 주세요:</p>
                            <span style="display: block; width: fit-content; padding: 10px 15px; margin: 20px auto; background: #f0f0f0; border: 1px solid #ddd; border-radius: 5px; font-weight: bold; color: #333; font-family: 'Courier New', Courier, monospace;">%s</span>
                            <p style="line-height: 1.6; color: #666;">인증 코드는 5분 동안만 유효하니, 그 안에 인증을 완료해 주세요.</p>
                        </div>
                        <div style="text-align: center; padding-top: 20px; border-top: 1px solid #ddd;">
                            <p style="font-size: 12px; color: #999;">문의사항이 있으시면 <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">이메일</a>로 연락해 주세요.</p>
                            <p style="font-size: 12px; color: #999;">GitHub: <a href="https://github.com/jyp-on" style="text-decoration: none; color: #2196F3;">https://github.com/jyp-on</a></p>
                            <p style="font-size: 12px; color: #999;">Email: <a href="mailto:ju0_park@naver.com" style="text-decoration: none; color: #2196F3;">ju0_park@naver.com</a></p>
                        </div>
                    </div>
                </body>
                </html>
        """.formatted(authCode);
    }
}

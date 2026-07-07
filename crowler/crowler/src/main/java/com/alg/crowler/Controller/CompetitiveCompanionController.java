package com.alg.crowler.Controller; // 패키지명 소문자 주의

import com.alg.crowler.dto.CompanionProblemDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
public class CompetitiveCompanionController {

    @PostMapping("/")
    public String receiveProblem(@RequestBody CompanionProblemDto dto) {
        log.info("==== [1단계: 확장 프로그램 데이터 수신] ====");
        log.info("문제 이름: {}", dto.name());
        log.info("테스트케이스 개수: {}개", dto.tests().size());

        String problemDescription = "본문을 가져오지 못했습니다.";

        if (dto.url() != null && dto.url().contains("atcoder.jp")) {
            try {
                String targetUrl = dto.url().contains("?") ? dto.url() + "&lang=en" : dto.url() + "?lang=en";

                Document doc = Jsoup.connect(targetUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .get();

                Element englishSection = doc.select("#task-statement span.lang-en").first();
                if (englishSection == null) {
                    englishSection = doc.select("#task-statement").first();
                }

                if (englishSection != null) {
                    problemDescription = englishSection.html();
                }
            } catch (IOException e) {
                log.error("AtCoder 본문 크롤링 실패: ", e);
            }
        }

        // 3단계: HTML과 테스트케이스들을 모아서 하나의 ZIP 파일로 압축 생성
        createZipFile(dto, problemDescription);

        return "OK";
    }

    private void createZipFile(CompanionProblemDto dto, String htmlContent) {
        // 파일명에 사용할 수 없는 특수문자 제거
        String safeFileName = dto.name().replaceAll("[\\\\/:*?\"<>|]", "_");
        String zipFileName = safeFileName + ".zip";

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {

            // --- 1. HTML 파일 생성 및 ZIP에 추가 ---
            String htmlFileContent = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n" +
                    "<meta charset=\"UTF-8\">\n" +
                    "<title>" + dto.name() + "</title>\n" +
                    "<style>\n" +
                    "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; padding: 20px; max-width: 900px; margin: 0 auto; color: #333; }\n" +
                    "  h3 { border-bottom: 1px solid #eee; padding-bottom: 5px; margin-top: 30px; }\n" +
                    "  pre { background-color: #f8f9fa; border: 1px solid #eaecf0; border-radius: 4px; padding: 15px; overflow: auto; font-family: Consolas, 'Courier New', monospace; font-size: 14px; }\n" +
                    "  code { background-color: #f8f9fa; border: 1px solid #eaecf0; border-radius: 3px; padding: 2px 4px; font-family: Consolas, 'Courier New', monospace; color: #e83e8c; }\n" +
                    "  pre code { background-color: transparent; border: none; padding: 0; color: inherit; }\n" +
                    "</style>\n" +
                    "<script>\n" +
                    "MathJax = { tex: { inlineMath: [['$', '$'], ['\\\\(', '\\\\)']] }, options: { skipHtmlTags: ['script', 'noscript', 'style', 'textarea', 'annotation', 'annotation-xml'] } };\n" +
                    "</script>\n" +
                    "<script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js\"></script>\n" +
                    "</head>\n<body>\n" +
                    "<h1>" + dto.name() + "</h1>\n<hr>\n" +
                    htmlContent +
                    "\n<script> document.querySelectorAll('var').forEach(function(el) { el.outerHTML = '\\\\(' + el.innerHTML + '\\\\)'; }); </script>\n" +
                    "</body>\n</html>";

            // problem.html 이라는 이름으로 압축 파일 내부에 저장
            ZipEntry htmlEntry = new ZipEntry("problem.html");
            zos.putNextEntry(htmlEntry);
            zos.write(htmlFileContent.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // --- 2. 테스트케이스 파일들 생성 및 ZIP에 추가 ---
            for (int i = 0; i < dto.tests().size(); i++) {
                var test = dto.tests().get(i);

                // Input 파일 (예: 1.in, 2.in)
                ZipEntry inEntry = new ZipEntry((i + 1) + ".in");
                zos.putNextEntry(inEntry);
                zos.write(test.input().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();

                // Output 파일 (예: 1.out, 2.out)
                ZipEntry outEntry = new ZipEntry((i + 1) + ".out");
                zos.putNextEntry(outEntry);
                zos.write(test.output().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }

            log.info("==== [3단계: ZIP 압축 파일 저장 완료] ====");
            log.info("저장된 파일명: {}", zipFileName);

        } catch (IOException e) {
            log.error("ZIP 파일 생성 중 에러 발생: ", e);
        }
    }
}
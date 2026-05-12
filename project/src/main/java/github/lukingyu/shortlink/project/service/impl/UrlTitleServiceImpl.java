package github.lukingyu.shortlink.project.service.impl;

import github.lukingyu.shortlink.project.service.UrlTitleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    private static final int TIMEOUT_MILLIS = 5000;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    @Override
    public String getTitleByUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        String validatedUrl = url.trim();
        try {
            URI.create(validatedUrl).toURL();
        } catch (Exception e) {
            return null;
        }

        try {
            Document document = Jsoup.connect(validatedUrl)
                    .timeout(TIMEOUT_MILLIS)
                    .userAgent(USER_AGENT)
                    .followRedirects(true)
                    .get();
            String title = document.title();
            return !title.isBlank() ? title : null;
        } catch (IOException e) {
            return null;
        }
    }
}
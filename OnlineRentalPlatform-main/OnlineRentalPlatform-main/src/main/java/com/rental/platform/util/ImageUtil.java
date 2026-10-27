package com.rental.platform.util;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ImageUtil {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)([\\da-z\\.-]+)\\.([a-z\\.]{2,6})" +
        "([/\\w \\.-]*)*\\/?\\.(jpg|jpeg|png|gif|bmp|webp)$",
        Pattern.CASE_INSENSITIVE
    );
    
    public boolean isValidImageUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
    
    public List<String> validateImageUrls(List<String> urls) {
        if (urls == null) {
            return List.of();
        }
        
        return urls.stream()
                .filter(url -> url != null && !url.trim().isEmpty())
                .filter(this::isValidImageUrl)
                .collect(Collectors.toList());
    }
}
package org.ctc.util;

import lombok.Data;
import okhttp3.Response;
import org.apache.tika.Tika;
import org.ctc.dao.ProductDao;
import org.ctc.entity.Product;
import org.ctc.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Component
public class GoogleSearchImgUtils {

    private final WebClient webClient;
    private final String apiKey;
    private final String searchEngineId;
    @Autowired
    private ProductDao productDao;

    @Autowired
    public GoogleSearchImgUtils(WebClient webClient,
                                @Value("${google.api.key}") String apiKey,
                                @Value("${google.search.engine.id}") String searchEngineId) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.searchEngineId = searchEngineId;
    }

    public Mono<String> searchProductImage(Integer productId) {
        Optional<Product> byId = productDao.findById(productId);

        if (byId.isPresent()) {
            String productName = byId.get().getProductName();
            String url = String.format(
                    "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s&searchType=image&num=1",
                    apiKey, searchEngineId, productName
            );
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(GoogleSearchResponse.class)
                    .doOnNext(response -> {
                        System.out.println("Received response: " + response);
                        if (response.getItems() != null) {
                            System.out.println("Items: " + response.getItems());
                            response.getItems().forEach(item -> System.out.println("Item link: " + item.getLink()));
                        } else {
                            System.out.println("No items in response");
                        }
                    })
                    .map(response -> {
                        if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                            return response.getItems().get(0).getLink();
                        }
                        return null;
                    })
                    .switchIfEmpty(Mono.empty())
                    .doOnError(e -> System.err.println("Error in searchProductImage: " + e.getMessage()));
        }
        return Mono.empty(); // 返回空的Mono而不是null
    }

    public Mono<byte[]> downloadImageFromUrl(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(dataBuffer -> {
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    return bytes;
                })
                .collectList()
                .map(byteArrays -> {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byteArrays.forEach(bytes -> outputStream.writeBytes(bytes));
                    return outputStream.toByteArray();
                });
    }
    public Mono<byte[]> convertBase64ToBytes(String base64String) {
        String[] parts = base64String.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];
        return Mono.just(Base64.getDecoder().decode(imageString));
    }

    public Mono<String> uploadToImgur(byte[] imageBytes) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("image", imageBytes)
                .filename("image.jpg");

        return webClient.post()
                .uri("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID YOUR_IMGUR_CLIENT_ID")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Void> saveImageToLocal(byte[] imageBytes, String filePath) {
        return Mono.fromCallable(() -> {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, imageBytes);
            return null;
        });
    }
//    public Mono<byte[]> searchNDownloadLocalNUpload(Integer productId, String localPath, ImageService imageService, String sourceType) {
//
//        return searchProductImage(productId)
//                .flatMap(this::downloadImageFromUrl)// 使用搜索到的图片URL下载图片
//                .flatMap(imgBytes->{
//                    saveImageToLocal(imgBytes,localPath);
//                    imageService.uploadNewImageToServer(imgBytes,productId,sourceType)
//                })
//                .onErrorResume(e -> {
//                    e.printStackTrace();
//                    return Mono.empty(); // 在出错时返回空的Mono
//                });
//    }
    // 處理 URL 圖片
    public Mono<String> processImageFromUrl(String imageUrl, String localPath) {
        return downloadImageFromUrl(imageUrl)
                .flatMap(bytes -> saveImageToLocal(bytes, localPath)
                        .then(uploadToImgur(bytes)));
    }

    // 處理 Base64 圖片
    public Mono<String> processImageFromBase64(String base64String, String localPath) {
        return convertBase64ToBytes(base64String)
                .flatMap(bytes -> saveImageToLocal(bytes, localPath)
                        .then(uploadToImgur(bytes)));
    }
    private String detectFileType(String url, byte[] content) {
        // 首先嘗試從 URL 獲取文件類型
        String fileType = URLConnection.guessContentTypeFromName(url);

        if (fileType == null || fileType.isEmpty()) {
            // 如果從 URL 無法獲取，則嘗試從內容檢測
            Tika tika = new Tika();
            try {
                fileType = tika.detect(content);
            } catch (Exception e) {
                e.printStackTrace();
                // 如果檢測失敗，使用默認類型
                fileType = "application/octet-stream";
            }
        }

        return fileType;
    }

    private String getFileExtension(String url, String contentType) {
        // 首先嘗試從 URL 獲取擴展名
        String extension = url.substring(url.lastIndexOf('.') + 1).toLowerCase();

        // 如果 URL 中沒有擴展名，則根據內容類型判斷
        if (extension.length() > 4 || extension.contains("/")) {
            switch (contentType) {
                case "image/jpeg":
                    return "jpg";
                case "image/png":
                    return "png";
                case "image/gif":
                    return "gif";
                // 添加更多類型...
                default:
                    return "jpg"; // 默認擴展名
            }
        }

        return extension;
    }

    private MultipartFile createMultipartFile(byte[] imageBytes, String url) {
        String contentType = detectFileType(url, imageBytes);
        String extension = getFileExtension(url, contentType);
        String fileName = "image_" + System.currentTimeMillis() + "." + extension;

        return new ByteArrayMultipartFile(
                imageBytes,
                "file",
                fileName,
                contentType
        );
    }
    public Mono<Boolean> searchNDownloadLocalNUpload(Integer productId, String localPath, ImageService imageService, String sourceType) {
        return searchProductImage(productId)
                .flatMap(url -> {
                    if (url == null) {
                        System.out.println("No image URL found for product ID: " + productId);
                        return Mono.just(new ImageDownloadResult(null, null));
                    }
                    System.out.println("Image URL for product " + productId + ": " + url);
                    return downloadImageFromUrl(url)
                            .map(imgBytes -> new ImageDownloadResult(url, imgBytes));
                })
                .flatMap(result -> {
                    if (result.getUrl() == null || result.getImageBytes() == null) {
                        System.out.println("No image data available for product ID: " + productId);
                        return Mono.just(false);
                    }
                    MultipartFile multipartFile = createMultipartFile(result.getImageBytes(), result.getUrl());
                    return saveImageToLocal(result.getImageBytes(), localPath)
                            .then(Mono.fromRunnable(() ->
                                    imageService.uploadNewImageToServer(multipartFile, productId, sourceType)
                            ).thenReturn(true));
                })
                .onErrorResume(e -> {
                    System.err.println("Error processing product " + productId + ": " + e.getMessage());
                    return Mono.just(false);
                })
                .defaultIfEmpty(false);
    }

    // 确保 ImageDownloadResult 类正确定义
    private static class ImageDownloadResult {
        private final String url;
        private final byte[] imageBytes;

        ImageDownloadResult(String url, byte[] imageBytes) {
            this.url = url;
            this.imageBytes = imageBytes;
        }

        public String getUrl() { return url; }
        public byte[] getImageBytes() { return imageBytes; }
    }


    // 內部類定義
    @Data
    private static class GoogleSearchResponse {
        private java.util.List<GoogleSearchItem> items;
        // getters and setters
    }
    @Data
    private static class GoogleSearchItem {
        private String link;
        // getters and setters
    }
}


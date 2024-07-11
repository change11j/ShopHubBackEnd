package org.ctc.service;

import jakarta.transaction.Transactional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.ctc.dao.ImageDao;
import org.ctc.dto.ImageDTO;
import org.ctc.dto.Result;
import org.ctc.entity.Image;
import org.ctc.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.ctc.costant.Constance.*;


@Service
public class ImageService {

    private Logger log = LoggerFactory.getLogger(ImageService.class);

    private final RestTemplate restTemplate;

    private ImageDao imageDao;

    private final ImageUtil imageUtil;

    @Value("${image.upload.url}")
    private String IMAGE_SYSTEM_UPLOADURL;
    @Value("${images.upload.url}")
    private String IMAGES_SYSTEM_UPLOADURL;

    @Value("${images.upload.delete.url}")
    private String IMAGE_SYSTEM_DELETEURL;


    public ImageService(RestTemplate restTemplate, ImageDao imageDao, ImageUtil imageUtil) {
        this.restTemplate = restTemplate;
        this.imageDao = imageDao;
        this.imageUtil = imageUtil;
    }

    @Transactional
    public boolean uploadNewImageToServer(MultipartFile file, Integer sourceId, String sourceType) {
        String[] orgFileArr = file.getOriginalFilename().split("\\.");
        int length = orgFileArr.length;
        Image image = new Image();
        image.setExtension(orgFileArr[length - 1]);
        image.setSourceId(sourceId);
        image.setSourceString(SHOP);
        image.setSourceType(sourceType);
        image = imageDao.save(image);
        ImageDTO imageDTO = new ImageDTO();

        imageDTO.setFileName(imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), image.getImageId(), image.getExtension()));

        try {
            InputStream is = file.getInputStream();
            imageDTO.setData(is.readAllBytes());
            is.close();
        } catch (Exception e) {
            log.error("image upload failed...");
            return false;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ImageDTO> requestEntity = new HttpEntity<>(imageDTO, headers);
        restTemplate.postForEntity(IMAGE_SYSTEM_UPLOADURL, requestEntity, String.class).getBody(); //執行上傳API
        return true;
    }
    @Transactional
    public boolean uploadNewImagesToServer(List<MultipartFile> files, List<Integer> sourceIds, String sourceType) {
        List<ImageDTO> imageDTOs = new ArrayList<>();
        for(int index=0;index<files.size();index++){
            String[] orgFileArr = files.get(index).getOriginalFilename().split("\\.");
            int length = orgFileArr.length;
            Image image = new Image();
            image.setExtension(orgFileArr[length - 1]);
            image.setSourceId(sourceIds.get(index));
            image.setSourceString(SHOP);
            image.setSourceType(sourceType);
            image = imageDao.save(image);
            ImageDTO imageDTO = new ImageDTO();

            imageDTO.setFileName(imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), image.getImageId(), image.getExtension()));

            try {
                InputStream is = files.get(index).getInputStream();
                imageDTO.setData(is.readAllBytes());
                is.close();
            } catch (Exception e) {
                log.error("image upload failed...");
                return false;
            }

            imageDTOs.add(imageDTO);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<ImageDTO>> requestEntity = new HttpEntity<>(imageDTOs, headers);
        restTemplate.postForEntity(IMAGE_SYSTEM_UPLOADURL + "s", requestEntity, String.class).getBody(); //執行上傳API
        return true;
    }
    @Transactional
    public boolean updateImageToServer(MultipartFile file, Integer imageId) {
        String[] orgFileArr = file.getOriginalFilename().split("\\.");
        int length = orgFileArr.length;
        Optional<Image> imageOptional = imageDao.findById(imageId);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            String newExt = orgFileArr[length - 1]; //新的附檔名
            if (image.getExtension().equals(newExt)) {    //若附檔名相同，則不須刪除，直接覆蓋即可
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFileName(imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), imageId, image.getExtension()));
                try {
                    InputStream is = file.getInputStream();
                    imageDTO.setData(is.readAllBytes());
                } catch (Exception e) {
                    log.error("image upload failed...");
                    return false;
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ImageDTO> requestEntity = new HttpEntity<>(imageDTO, headers);
                restTemplate.postForEntity(IMAGE_SYSTEM_UPLOADURL, requestEntity, String.class); //執行上傳API

            } else {                                     //若附檔名不同，則須刪除，重新上傳


                MultiValueMap<String, String> deleteFormData = new LinkedMultiValueMap<>();
                deleteFormData.add("fileName", imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), imageId, image.getExtension()));
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(deleteFormData, headers);

                restTemplate.postForEntity(IMAGE_SYSTEM_DELETEURL, requestEntity, String.class);  //執行刪除API

                image.setExtension(newExt);
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFileName(imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), imageId, image.getExtension()));
                try {
                    InputStream is = file.getInputStream();
                    imageDTO.setData(is.readAllBytes());
                } catch (Exception e) {
                    log.error("image upload failed...");
                    return false;
                }
                HttpHeaders uploadheaders = new HttpHeaders();
                uploadheaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ImageDTO> requestUploadEntity = new HttpEntity<>(imageDTO, uploadheaders);
                restTemplate.postForEntity(IMAGE_SYSTEM_UPLOADURL, requestUploadEntity, String.class); //執行上傳API
                imageDao.save(image);

            }
            log.info("image upload success");
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public boolean getUrlImage(String url, Integer sourceId, String sourceType) {
        OkHttpClient getImg = new OkHttpClient();
        //取得圖片區塊
        Request request = new Request.Builder().url(url).build();
        byte[] imageByte = null;
        try (Response okHttpresponse = getImg.newCall(request).execute()) {
            if (!okHttpresponse.isSuccessful()) {
                throw new IOException("Failed to download file: " + okHttpresponse);
            }
            ResponseBody body = okHttpresponse.body();
            if (body != null) {
                try {
                    InputStream inputStream = body.byteStream();
                    imageByte = inputStream.readAllBytes();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image();
        image.setExtension("jpg");
        image.setSourceId(sourceId);
        image.setSourceString(SHOP);
        image.setSourceType(sourceType);
        image = imageDao.save(image);
        ImageDTO imageDTO = new ImageDTO();

        imageDTO.setFileName(imageUtil.generateImageFileName(image.getSourceType(), image.getSourceId(), image.getImageId(), image.getExtension()));
        imageDTO.setData(imageByte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ImageDTO> requestEntity = new HttpEntity<>(imageDTO, headers);
        restTemplate.postForEntity(IMAGE_SYSTEM_UPLOADURL, requestEntity, String.class).getBody(); //執行上傳API

        return true;
    }


}

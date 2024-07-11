package org.ctc.util;

import org.hibernate.boot.jaxb.SourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

import static org.ctc.costant.Constance.*;
@Component
public class ImageUtil {

    @Value("${images.path}")
    private  String IMAGE_PATH;

    public String generateImageFile(String sourceType,Integer sourceId,Integer imageId,String extension){
        StringBuffer deletFileName = new StringBuffer();
        deletFileName.append(IMAGE_PATH);
        deletFileName.append(SHOP);
        deletFileName.append(sourceType);
        deletFileName.append(sourceId);
        deletFileName.append("-");
        deletFileName.append(imageId);
        deletFileName.append(".");
        deletFileName.append(extension);
        return deletFileName.toString();
    }
//    *

    public String generateImageFileName(String sourceType,Integer sourceId,Integer imageId,String extension ){
        StringBuffer newFileName= new StringBuffer();
        newFileName.append(SHOP);
        newFileName.append(sourceType);
        newFileName.append(sourceId);
        newFileName.append("-");
        newFileName.append(imageId);
        newFileName.append(".");
        newFileName.append(extension);
        return newFileName.toString();
    }





}

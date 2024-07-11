package org.ctc.service;

import org.ctc.dao.CategoryDao;
import org.ctc.dao.ImageDao;
import org.ctc.dto.CategoryWithImageDto;
import org.ctc.dto.ProductWithCateNImgDTO;
import org.ctc.dto.Result;
import org.ctc.entity.Category;
import org.ctc.entity.Image;
import org.ctc.entity.Product;
import org.ctc.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.ctc.costant.Constance.*;

@Service

public class CategoryService {
    private CategoryDao categoryDao;
    private ImageDao imageDao;
    private ImageUtil imageUtil;
    private ImageService imageService;

    public CategoryService(CategoryDao categoryDao, ImageDao imageDao, ImageUtil imageUtil, ImageService imageService) {
        this.categoryDao = categoryDao;
        this.imageDao = imageDao;
        this.imageUtil = imageUtil;
        this.imageService = imageService;
    }

    public Result getAllCategories(){
        List<Category> all = categoryDao.findAll();
        return new Result(SUCCESS,all);
    }
    public  Result getAllCateWithImg(){
        List<Category> all = categoryDao.findAll();
        List<Integer> idList=new ArrayList<>();
        for (Category category :
                all) {
            Integer id = category.getCategoryId();
            idList.add(id);
        }
        List<Image> imageList = imageDao.findAllBySourceStringAndSourceTypeAndSourceIdIn(SHOP,CATEGORY_TYPE, idList);
        List<CategoryWithImageDto> dtoList = convertToCwImgDTO(all, imageList);
        return new Result(200,dtoList);
    }
    public Result updateCateWithImg(List<Integer> ids,
                                               List<MultipartFile> files){
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            MultipartFile file = files.get(i);
            if (categoryDao.existsById(id)) {
                // 保存或更新Image
                List<Image> images = imageDao.findBySourceStringAndSourceTypeAndSourceId(SHOP, CATEGORY_TYPE, id);
                if (images.size() > 0) {
                    imageService.updateImageToServer(file, images.get(0).getImageId());
                } else {
                    imageService.uploadNewImageToServer(file, id, CATEGORY_TYPE);
                }
            }

        }


        return new Result(SUCCESS);
    }
    public List<CategoryWithImageDto> convertToCwImgDTO(List<Category> cateList,List<Image> imgList) {
        List<CategoryWithImageDto> DtoList = new ArrayList<>();
        for (int i = 0; i <cateList.size() ; i++) {
            CategoryWithImageDto dto = new CategoryWithImageDto();
            dto.setCategory(cateList.get(i));
            Image image = imgList.get(i);
            String url = imageUtil.generateImageFile(image.getSourceType(), image.getSourceId(), image.getImageId(), image.getExtension());
            dto.setImgUrl(url);
            DtoList.add(dto);
        }

        return DtoList;
    }

}

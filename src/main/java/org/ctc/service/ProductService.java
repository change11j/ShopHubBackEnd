package org.ctc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ctc.dao.ImageDao;
import org.ctc.dao.CategoryDao;
import org.ctc.dao.ProductDao;
import org.ctc.dto.ProdWithCateDto;
import org.ctc.dto.Result;
import org.ctc.dto.ecpay.ProductDTO;
import org.ctc.entity.Category;
import org.ctc.entity.Image;
import org.ctc.entity.Product;
import org.ctc.dao.*;
import org.ctc.dto.ecpay.CartDTO;
import org.ctc.dto.ecpay.CartGroup;
import org.ctc.entity.*;
import org.ctc.dto.*;
import org.ctc.projection.CategorySearchProjection;
import org.ctc.projection.ProdDetailProjection;
import org.ctc.util.GoogleSearchImgUtils;
import org.ctc.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.ctc.costant.Constance.*;



@Service
public class ProductService {
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ImageDao imageDao;
    private final ImageService imageService;
    private final ImageUtil imageUtil;
    private ObjectMapper objectMapper;

    private ShipAddressDao shipAddressDao;

    private CartItemDao cartItemDao;

    private ShipMethodDao shipMethodDao;

    private ProdSpecDao prodSpecDao;
    private JdbcTemplate jdbcTemplate;
    private GoogleSearchImgUtils googleSearchImgUtils;
    @Autowired
    private RestTemplate restTemplate;


    public ProductService(ProductDao productDao,
                          CategoryDao categoryDao,
                          ImageDao imageDao,
                          ImageService imageService,
                          ImageUtil imageUtil,
                          CartItemDao cartItemDao,
                          ShipMethodDao shipMethodDao,
                          ShipAddressDao shipAddressDao,
                          ProdSpecDao prodSpecDao,
                          ObjectMapper objectMapper,
                          JdbcTemplate jdbcTemplate,
                          GoogleSearchImgUtils googleSearchImgUtils) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
        this.imageDao = imageDao;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
        this.cartItemDao=cartItemDao;
        this.shipMethodDao=shipMethodDao;
        this.shipAddressDao=shipAddressDao;
        this.prodSpecDao=prodSpecDao;
        this.objectMapper=objectMapper;
        this.jdbcTemplate=jdbcTemplate;
        this.googleSearchImgUtils=googleSearchImgUtils;
    }

    //    ?應該不會不允許重複就不判斷了
    public Result creatProduct(Product product){
        Product CreatedProduct = productDao.save(product);
        return new Result(SUCCESS,CreatedProduct);
    }
    @Transactional
    public Result createAddedProduct(String jsonStr,List<MultipartFile> files){
        try {
            //* 将 JSON 字符串转换为 DTO 对象
            String utf8Json = new String(jsonStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            AddedProductDTO addedProductDTO = objectMapper.readValue(utf8Json, AddedProductDTO.class);

//            *因為我在前端新增時不傳id值 所以這邊判斷是否等於null
            if (addedProductDTO.getProduct().getProductId()== null) {
//                *前端傳時間很麻煩 所以我在後端賦值
                addedProductDTO.getProduct().setListingDate(LocalDateTime.now());
                Integer id = productDao.save(addedProductDTO.getProduct()).getProductId();
                if ( !(addedProductDTO.getProdSpecs()==null)) {
//                   !性能較差
//                    addedProductDTO.getProdSpec().forEach(prodSpec -> {
//                        prodSpec.setProductId(id);
//                        prodSpecDao.save(prodSpec);
//                    });
                    List<ProdSpec> updatedProdSpecs = addedProductDTO.getProdSpecs().stream()
                            .map(prodSpec -> {
                                prodSpec.setProductId(id);
                                return prodSpec;
                            })
                            .collect(Collectors.toList());
                    prodSpecDao.saveAll(updatedProdSpecs);
                }
                for (int i = 0; i < files.size(); i++) {
//                 *直接限制不能超過五張
                    if (i<IMAGE_UPLOAD_LIMIT) {
                        MultipartFile file=files.get(i);
                        imageService.uploadNewImageToServer(file, id, PRODUCT_TYPE);
                    }
                }
            }


        } catch (Exception e) {
            System.out.println(e);;
        }

        return new Result<>(SUCCESS);
    }
    @Transactional
    public Result updateAddedProduct(String jsonStr,List<MultipartFile> files){
        String utf8Json = new String(jsonStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        try {
            AddedProductDTO addedProductDTO = objectMapper.readValue(utf8Json, AddedProductDTO.class);
            Integer productId = addedProductDTO.getProduct().getProductId();
            Optional<Product> optionalProduct = productDao.findById(productId);
            if(optionalProduct.isPresent()){
                Product preProduct = optionalProduct.get();
//                *前端這邊沒有給日期且上架日期應該與最開始相同
                addedProductDTO.getProduct().setListingDate(preProduct.getListingDate());
                productDao.save(addedProductDTO.getProduct());
//                !這邊用比較簡單的方式處理先刪掉所有相關的prodSpec跟image再重新sava
                prodSpecDao.deleteAllByProductId(productId);
                imageDao.deleteAllBySourceTypeAndAndSourceId(PRODUCT_TYPE,productId);
                if ( !(addedProductDTO.getProdSpecs()==null)) {
                    List<ProdSpec> updatedProdSpecs = addedProductDTO.getProdSpecs().stream()
                            .map(prodSpec -> {
                                prodSpec.setProductId(productId);
                                return prodSpec;
                            })
                            .collect(Collectors.toList());
                    prodSpecDao.saveAll(updatedProdSpecs);
                }
                for (int i = 0; i < files.size(); i++) {
//                 *直接限制不能超過五張
                    if (i<IMAGE_UPLOAD_LIMIT) {
                        MultipartFile file=files.get(i);
                        imageService.uploadNewImageToServer(file, productId, PRODUCT_TYPE);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new Result<>(SUCCESS);
    }
    @Transactional
    public Result createProductWithCateNImgDTO(List<ProdWithCateDto> dtoList){
        for (int i = 0; i < dtoList.size(); i++) {

            ProdWithCateDto dto = dtoList.get(i);
            System.out.println(dto);

            // 保存Product
            Product product = dto.getProduct();
            productDao.save(product);

            // 保存Category
            Category category = dto.getCategory();
            categoryDao.save(category);



        }
        return new Result(SUCCESS);
    }
    @Transactional
    public Result updateProductWithCateNImgDTO(List<Integer> ids,
                                               List<MultipartFile> files){
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            MultipartFile file = files.get(i);
            if (productDao.existsById(id)) {
                // 保存或更新Image
                List<Image> images = imageDao.findBySourceStringAndSourceTypeAndSourceId(SHOP, PRODUCT_TYPE, id);
                if (images.size() > 0) {
                    imageService.updateImageToServer(file, images.get(0).getImageId());
                } else {
                    imageService.uploadNewImageToServer(file, id, PRODUCT_TYPE);
                }
            }

        }


        return new Result(SUCCESS);
    }
    public Result getProduct(String name){
        Product product = productDao.findByProductName(name);
        return new Result(SUCCESS,product);
    }
//    public List<ProductWithCateNImgDTO> convertToPwCDTO(List<Object[]> resultList,List<Image> imageList) {
//        List<ProductWithCateNImgDTO> resultDtoList = new ArrayList<>();
//
//        for (int i = 0; i < resultList.size(); i++) {
//            ProductWithCateNImgDTO dto = new ProductWithCateNImgDTO();
////            *為dto設定product物件
//            dto.setProduct((Product) resultList.get(i)[0]);
////            *為dto設定category物件
//            Category category = new Category();
//            category.setCategoryName((String) resultList.get(i)[1]);
//            dto.setCategory(category);
//
//            Image image = imageList.get(i);
//            dto.setImages(image);
//            String url = imageUtil.generateImageFile(image.getSourceType(), image.getSourceId(), image.getImageId(), image.getExtension());
//            dto.setImgUrls(url);
//            resultDtoList.add(dto);
//        }
//
//        return resultDtoList;
//    }
    public Result getProductDetail(Integer pId){
        System.out.println("OK");
        List<ProductDetailDTO> dtos = productDao.findProdCateImgSpecUserDtoByPId(pId);
        for (ProductDetailDTO dto :
                dtos) {
            dto.getUsers().setSecret(null);
        }
        return new Result(SUCCESS,dtos);
    }

//    public Result getProductsWithCategories(List<ProdWithCateDto> queries) {
//        List<Integer>idList= new ArrayList<>();
//        List<ProductWithCateNImgDTO> resultList=new ArrayList<>();
//
////        *將id提成一個ArrayList 方便大量查詢
//        for (ProdWithCateDto dto :
//                queries) {
//            Product product = dto.getProduct();
//            if (product != null) {
//                Integer productId = product.getProductId();
//                if (productId != null) {
//                    idList.add(productId);
//                    System.out.println(productId);
//                }
//            }
//        }
//        List<Object[]> productList = productDao.findProductsWithCategoryByIds(idList);
//        List<Image> images = imageDao.findAllBySourceStringAndSourceTypeAndSourceIdIn(SHOP,PRODUCT_TYPE,idList);
//
//        List<ProductWithCateNImgDTO> dtoList = convertToPwCDTO(productList,images);
//        return new Result(SUCCESS,dtoList);
//    }
    public Result getAddedProductBySellerId(Integer sellerId){
        List<Object[]> results = productDao.findAddedProductDTOBySellerId(sellerId);
        Map<Integer,AddedProductDTO> map = new HashMap<>();
        for (Object[] result :
                results) {
            Product product= (Product) result[0];
            ProdSpec prodSpec= (ProdSpec) result[1];
            OrderDetail orderDetail= (OrderDetail) result[2];
            Image image= (Image) result[3];
            String imgUrl=
                    null;
            if (image!=null) {
                imgUrl = imageUtil.generateImageFile(PRODUCT_TYPE,product.getProductId(),
                        image.getImageId(),image.getExtension());
            }
            AddedProductDTO dto=map.get(product.getProductId());
            if (dto==null){
                dto=new AddedProductDTO(product,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
                map.put(product.getProductId(),dto);
            }
            if (!dto.getProdSpecs().contains(prodSpec)) {
                dto.getProdSpecs().add(prodSpec);
            }
            if (!dto.getOrderDetails().contains(orderDetail)) {
                dto.getOrderDetails().add(orderDetail);
            }
            if (!dto.getImageList().contains(image)) {
                dto.getImageList().add(image);
            }
            if (!dto.getImgUrlList().contains(imgUrl)) {
                dto.getImgUrlList().add(imgUrl);
            }
        }
        List<AddedProductDTO> list=new ArrayList<>(map.values());

        return new Result(SUCCESS,list);
    }
    @Transactional
    public Result deleteAddedProductByProdId(Integer productId) {
        String sql = "CALL deleteProductAndRelatedData(?)";
        jdbcTemplate.update(sql, productId);
        return new Result(SUCCESS);
    }

    public Result deleteProdSpec(Integer prodSpecId){
        prodSpecDao.deleteById(prodSpecId);
        return new Result(SUCCESS);
    }
//    *search 方法
    public Result searchProductsByName(String name){
        List<Object[]> results = productDao.searchProductsByName(name);
        Map<Integer,ProductSearch> map = new HashMap<>();
        for (Object[] result :
                results) {
            Product product= (Product) result[0];
            Category category= (Category) result[1];
            ProdSpec prodSpec= (ProdSpec) result[2];
            Image image = (Image) result[3];
            String imgUrl = "";
            if (image != null) {
                imgUrl = imageUtil.generateImageFile(PRODUCT_TYPE, product.getProductId(), image.getImageId(), image.getExtension());
            } else {
                // 处理 image 为 null 的情况，比如使用默认图片或者留空
                imgUrl = ""; // 示例：使用默认图片URL
            }
            ProductSearch dto=map.get(product.getProductId());
            if(dto==null){
                dto=new ProductSearch(product,category,new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
                map.put(product.getProductId(),dto);
            }
            if (!dto.getImages().contains(image)){
                dto.getImages().add(image);
            }
            if (prodSpec!=null&&!dto.getProdSpecs().contains(prodSpec)){
                dto.getProdSpecs().add(prodSpec);
            }
            if(!dto.getImgUrls().contains(imgUrl)){
                dto.getImgUrls().add(imgUrl);
            }

        }

        List<ProductSearch> productSearchList = map.values().stream()
                .peek(productSearch -> {
                    if (productSearch.getProdSpecs().isEmpty()) {
                        productSearch.setProdSpecs(null); // 将空的prodSpecs列表设置为null
                    }
                    if (productSearch.getImages().isEmpty()) {
                        productSearch.setImages(null); // 将空的images列表设置为null
                    }
                    if (productSearch.getImgUrls().isEmpty()) {
                        productSearch.setImgUrls(null); // 将空的imgUrls列表设置为null
                    }
                })
                .collect(Collectors.toList());

        return new Result(SUCCESS, productSearchList);

    }


    public Result searchProductsByCategory(String cateName){
        List<CategorySearchProjection> projections = productDao.searchCategoryProduct(cateName);

        // 将结果按产品进行分组
        Map<Product, List<CategorySearchProjection>> groupedByProduct = projections.stream()
                .collect(Collectors.groupingBy(CategorySearchProjection::getProduct));

        List<CategorySearch> collect = groupedByProduct.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<CategorySearchProjection> productProjections = entry.getValue();
                    Category category = productProjections.get(0).getCategory(); // 假设每个分组的类别是相同的

                    // 使用Set集合来排除重复
                    Set<Image> imagesSet = new HashSet<>();
                    Set<String> imgUrlsSet = new HashSet<>();
                    Set<ProdSpec> prodSpecsSet = new HashSet<>();

                    productProjections.forEach(projection -> {
                        if (projection.getImage() != null) {
                            imagesSet.add(projection.getImage());
                            imgUrlsSet.add(projection.getImgUrl());
                        }
                        if (projection.getProdSpec() != null) {
                            prodSpecsSet.add(projection.getProdSpec()); // 假设getProdSpecs()返回List<ProdSpec>
                        }
                    });

                    // 将Set转换为List
                    List<Image> images = imagesSet.isEmpty() ? null : new ArrayList<>(imagesSet);
                    List<String> imgUrls = imgUrlsSet.isEmpty() ? null : new ArrayList<>(imgUrlsSet);
                    List<ProdSpec> prodSpecs = prodSpecsSet.isEmpty() ? null : new ArrayList<>(prodSpecsSet);

                    return new CategorySearch(product, category, prodSpecs, images, imgUrls);
                }).collect(Collectors.toList());

        return new Result(SUCCESS, collect);
    }
    public Result getAllCategories(){
        List<Category> all = categoryDao.findAll();
        return new Result(SUCCESS,all);
    }
    public Result getPcardByProductIds(List<Integer> ids){
        List<Object[]> results = productDao.findPcardByProductIds(ids);
        Map<Integer,Pcard> map= new HashMap<>();
        for (Object[] result :
                results) {
            Product product = (Product) result[0];
            ProdSpec prodSpec= (ProdSpec) result[1];
            Image image = (Image) result[2];
            // 在这里检查 image 是否为 null
            String imgUrl = "";
            if (image != null) {
                imgUrl = imageUtil.generateImageFile(PRODUCT_TYPE, product.getProductId(), image.getImageId(), image.getExtension());
            } else {
                // 处理 image 为 null 的情况，比如使用默认图片或者留空
                imgUrl = ""; // 示例：使用默认图片URL
            }
            Pcard pcard=map.get(product.getProductId());
            if(pcard == null){
                pcard=new Pcard(product,new ArrayList<>(),new ArrayList<>());
                map.put(product.getProductId(),pcard);
            }
            if(!pcard.getProdSpecs().contains(prodSpec)){
                pcard.getProdSpecs().add(prodSpec);
            }
            if(!pcard.getImgUrls().contains(imgUrl)){
                pcard.getImgUrls().add(imgUrl);
            }
        }
        List<Pcard> list = new ArrayList<>(map.values());
        return new Result(SUCCESS,list);
    }
    public Result getProductDetailsById(Integer productId) {
        List<ProdDetailProjection> projections = productDao.findProductDetailsByProductId(productId);
        if (projections.isEmpty()) {
            return new Result(400, "No product details found for the given product ID."); // 或者返回合适的错误/空值处理
        }

        // 因为我们知道只有一个Product，所以可以直接使用第一个ProdDetailProjection
        ProdDetailProjection firstProjection = projections.get(0);
        Product product = firstProjection.getProduct();
        Category category = firstProjection.getCategory();
        Users user = firstProjection.getUser();

        SellerDTO sellerDTO = new SellerDTO(user.getUserId(), user.getUserName(), user.getrDate(), user.getHouseholdCounty(),
                user.getHouseholdArea(), user.getHouseholdAddress(), user.getSellerName(),
                user.getPhone(), user.getSellerImage(), user.getSellerDisc());

        Set<Image> imagesSet = new HashSet<>();
        Set<String> imgUrlsSet = new HashSet<>();
        Set<ProdSpec> prodSpecsSet = new HashSet<>();
        Set<OrderDetail> orderDetailSet = new HashSet<>();
        Set<Rating> ratingSet = new HashSet<>();

        projections.forEach(projection -> {
            if (projection.getImage() != null) {
                imagesSet.add(projection.getImage());
                imgUrlsSet.add(projection.getImgUrl());
            }
            if (projection.getProdSpec() != null) {
                prodSpecsSet.add(projection.getProdSpec());
            }
            if (projection.getOrderDetail() != null) {
                orderDetailSet.add(projection.getOrderDetail());
            }
            if (projection.getRating() != null) {
                ratingSet.add(projection.getRating());
            }
        });

        List<Image> images = imagesSet.isEmpty() ? null : new ArrayList<>(imagesSet);
        List<String> imgUrls = imgUrlsSet.isEmpty() ? null : new ArrayList<>(imgUrlsSet);
        List<ProdSpec> prodSpecs = prodSpecsSet.isEmpty() ? null : new ArrayList<>(prodSpecsSet);
        List<OrderDetail> orderDetails=orderDetailSet.isEmpty()?null:new ArrayList<>(orderDetailSet);
        List<Rating> ratings=ratingSet.isEmpty() ? null :new ArrayList<>(ratingSet);

        // 因为预期只有一个元素，直接创建并返回这个元素
        ProdDetail prodDetail = new ProdDetail(product, category, images, prodSpecs, sellerDTO, imgUrls, orderDetails, ratings);

        return new Result(SUCCESS, prodDetail);
    }
    public Result getRatingByProductId(Integer productId){
        List<RatingDTO> dto = productDao.findRatingByProductId(productId);
        return new Result(SUCCESS,dto);
    }


    public Result createCartItem(CartItem cartItem){
        cartItemDao.save(cartItem);
        return new Result<>(SUCCESS);
    }

    public Result getCartItem(Integer userId){
        //設計回傳給前端之資料，取得這個userId的所有cartitem，並且處理過    []

        List<CartDTO> cartItems=cartItemDao.getCartItemByUserId(userId);
        //建立賣場set
        Map<Integer,CartGroup> map = new HashMap<>();
        Set<Integer> sellerIdSet = new HashSet<>();
        for(CartDTO cartItem:cartItems){
            sellerIdSet.add(cartItem.getProduct().getSellerId());
        }
        //遍歷set來初始化map
        for(Integer sellerId:sellerIdSet){
            List<CartDTO> cartDTOList = new ArrayList<>();
            CartGroup cartGroup = new CartGroup(sellerId,cartDTOList);
             //cartGroup.setSmIdList();
            cartGroup.setSmIdList(shipMethodDao.getUserShipTypes(sellerId));
            map.put(sellerId,cartGroup);
        }
        //
        for(CartDTO cartItem:cartItems){
            ProductDTO thisPrd = cartItem.getProduct();
            CartGroup cartGroup =map.get(thisPrd.getSellerId());
            cartGroup.setSellerName(thisPrd.getSellerName());
           // cartGroups.add(new CartGroup())
            List<CartDTO> cartDTOList=cartGroup.getCartDTOList();
            Image image=imageDao.findFirstBySourceTypeAndSourceStringAndSourceId(PRODUCT_TYPE,SHOP,thisPrd.getProductId());
            if(image!= null){
                String url=imageUtil.generateImageFile(image.getSourceType(),image.getSourceId(),image.getImageId(),image.getExtension());
                cartItem.setPImgURL(url);
            }

            if(cartItem.getPrdSpecId()!=null){
                int prdSpecId =cartItem.getPrdSpecId();
                List<ProdSpec> prodSpecList=prodSpecDao.findByProductId( thisPrd.getProductId());
                thisPrd.setProdSpecList(prodSpecList);

                      Optional<ProdSpec> prodSpecOpt= prodSpecList.stream().filter(item-> item.getProdSpecId() == prdSpecId).findFirst();
                      if(prodSpecOpt.isPresent()){
                        ProdSpec spec= prodSpecOpt.get();
                          thisPrd.setPrice(spec.getPrice());
                          thisPrd.setStock(spec.getStock());

                      }else {
                          return new Result(UNKNOWN_ERROR);
                      }


            }

            cartDTOList.add(cartItem);


        }
        List<CartGroup> res = new ArrayList<>(map.values());



           return new Result(SUCCESS,res);
        }


    public Result delCartItem(Integer cartItemId){
        cartItemDao.deleteById(cartItemId);
        return new Result<>(SUCCESS);
    }


    public Result getaddress(Integer userId){
        List<ShipAddress> shipAddresses=shipAddressDao.findByUserId(userId);
        return new Result<>(SUCCESS,shipAddresses);
    }


    public Result getProductsBySellerId(Integer sellerId){
        List<Product> products=productDao.findProductsBySellerId(sellerId);
        return new Result(SUCCESS,products);
    }


    public Result getCartSimple(Integer userId){
        List<CartDTO> cartItems=cartItemDao.getCartItemByUserId(userId);

        for(CartDTO cartDTO:cartItems){
            ProductDTO thisPrd = cartDTO.getProduct();
            Image image=imageDao.findFirstBySourceTypeAndSourceStringAndSourceId(PRODUCT_TYPE,SHOP,thisPrd.getProductId());
            if(image!= null){
                String url=imageUtil.generateImageFile(image.getSourceType(),image.getSourceId(),image.getImageId(),image.getExtension());
                cartDTO.setPImgURL(url);
            }



        }

        return new Result<>(SUCCESS,cartItems);
    }
    public Mono<Result> googleNUploadNSave() {
        List<Mono<Boolean>> operations = new ArrayList<>();
        for (int i = 100; i < 101; i++) {
            int productId = i;
            String localPath = "src/main/resources/save/image_" + productId + ".jpg";
            System.out.println("Processing product ID: " + productId);
            operations.add(googleSearchImgUtils.searchNDownloadLocalNUpload(productId, localPath, imageService, PRODUCT_TYPE));
        }

        return Flux.fromIterable(operations)
                .flatMap(mono -> mono)
                .collectList()
                .map(results -> {
                    long successCount = results.stream().filter(Boolean::booleanValue).count();
                    System.out.println("Processed " + results.size() + " products, " + successCount + " succeeded");
                    return new Result(SUCCESS, "Processed " + results.size() + " products, " + successCount + " succeeded");
                });
    }
    public Result fetchImage(@RequestParam List<String> urlList) {
        List<Map<String, Object>> imageDataList = new ArrayList<>();
        System.out.println(urlList);
        for (String imageUrl : urlList) {
            try {
                // 使用 RestTemplate 獲取圖片數據
                byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
                System.out.println(imageBytes);
                Map<String, Object> imageData = new HashMap<>();
                imageData.put("url", imageUrl);
                imageData.put("data", imageBytes);
                imageData.put("status", "success");

                imageDataList.add(imageData);
            } catch (Exception e) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("url", imageUrl);
                errorData.put("status", "error");
                errorData.put("message", "File not found or unable to fetch");

                imageDataList.add(errorData);
            }
        }
        System.out.println(imageDataList);
        return new Result(SUCCESS, imageDataList);
    }


}

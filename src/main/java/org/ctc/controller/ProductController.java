package org.ctc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ctc.dto.AddedProductDTO;
import org.ctc.dto.ProdWithCateDto;
import org.ctc.dto.Result;
import org.ctc.entity.CartItem;
import org.ctc.entity.Product;
import org.ctc.service.CategoryService;
import org.ctc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;
    @Autowired
    private RestTemplate restTemplate;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping("/creatProduct")
    public Result creatProduct(@RequestBody Product product) {
        return productService.creatProduct(product);
    }
    @PostMapping(path = "/createAddedProduct",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result createAddedProduct(@RequestPart("addedProduct") String addedProductJson,
                                     @RequestPart("files") List<MultipartFile> files)  {

        return productService.createAddedProduct(addedProductJson,files);
    }
    @PutMapping(path = "/updateAddedProduct",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateAddedProduct(@RequestPart("addedProduct") String addedProductJson,
                                     @RequestPart("files") List<MultipartFile> files)  {

        return productService.updateAddedProduct(addedProductJson,files);
    }
    @DeleteMapping("/deleteAddedProduct/{productId}")
    public Result deleteAddedProductSpec(@PathVariable Integer productId){
        return productService.deleteAddedProductByProdId(productId);
    }
    @DeleteMapping("/deleteProdSpec/{prodSpecId}")
    public Result deleteProdSpec(@PathVariable Integer prodSpecId){
        return productService.deleteProdSpec(prodSpecId);
    }
    @GetMapping("/getProduct/{productName}")
    public Result getProduct(@PathVariable String productName){
        return productService.getProduct(productName);
    }
    @GetMapping("/getProductDetail/{id}")
    public Result getProductDetail(@PathVariable Integer id){
        return productService.getProductDetailsById(id);
    }
    @GetMapping("/getRating/{productId}")
    public Result getRating(@PathVariable Integer productId){
        return productService.getRatingByProductId(productId);
    }
    @GetMapping("/getAddedProduct/{sellerId}")
    public Result getAddedProductBySellerId(@PathVariable Integer sellerId){
        return productService.getAddedProductBySellerId(sellerId);
    }
    @PostMapping("/createProductsWithCategories")
    public Result createProductWithCateNImgDTO(@RequestBody List<ProdWithCateDto> dtoList) {
        return productService.createProductWithCateNImgDTO(dtoList);
    }
    @PostMapping("/updateProductsWithCategories")
    public Result updateProductWithCateNImgDTO(@RequestParam("ids") List<Integer> ids,
                                               @RequestParam("files")List<MultipartFile> files) {
        return productService.updateProductWithCateNImgDTO(ids,files);
    }

//    @PostMapping("/getProductsWithCategories")
//    public Result getProdsNCateNImg(@RequestBody List<ProdWithCateDto> queries) {
//        return productService.getProductsWithCategories(queries);
//    }
    @PostMapping("/getPcards")
    public Result getPcadsByIds(@RequestBody List<Integer> ids){
        return productService.getPcardByProductIds(ids);
    }
//    * search方法
    @GetMapping("/searchProductsByName/{name}")
    public Result searchProductsByName (@PathVariable String name){
        return productService.searchProductsByName(name);
    }
    @GetMapping("/searchProductsByCategoryName/{categoryName}")
    public Result searchProductsByCategoryName(@PathVariable String categoryName){
        return productService.searchProductsByCategory(categoryName);
    }
    @GetMapping("/getAllCategories")
    public Result getCategories(){
        return categoryService.getAllCateWithImg();
    }
    @PostMapping("/updateCategoryWithImage")
    public Result updateCateWithImg(@RequestParam("ids") List<Integer> ids,
                                    @RequestParam("files")List<MultipartFile> files) {
        return categoryService.updateCateWithImg(ids,files);
    }

    @PostMapping("/createCartItem")
    public Result createCartItem(@RequestBody CartItem cartItem){
        return productService.createCartItem(cartItem);
    }


    @GetMapping("/getCartItem/{userId}")
    public Result getCartItem(@PathVariable Integer userId){
        return productService.getCartItem(userId);
    }



    @DeleteMapping("/delCartItem/{cartItemId}")
    public Result delCartItem(@PathVariable Integer cartItemId){
        return productService.delCartItem(cartItemId);
    }


    @GetMapping("/getaddress/{userId}")
    public Result getaddress(@PathVariable Integer userId){
        return productService.getaddress(userId);
    }

    @GetMapping("/seller/getProducts/{sellerId}")
    public Result seller(@PathVariable Integer sellerId){
        return productService.getProductsBySellerId(sellerId);
    }

    @GetMapping("/getCartSimple/{userId}")
    public Result getCartSimple(@PathVariable Integer userId){
        return productService.getCartSimple(userId);
    }

    @GetMapping("/googleImg")
    public Mono<ResponseEntity<Result>> googleImg() {
        return productService.googleNUploadNSave()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/getImgFile")
    public Result getImgFile(@RequestParam List<String> urlList){
        return productService.fetchImage(urlList);
    }
}

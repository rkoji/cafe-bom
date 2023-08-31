package com.zerobase.cafebom.product.service;


import com.zerobase.cafebom.exception.CustomException;
import com.zerobase.cafebom.exception.ErrorCode;
import com.zerobase.cafebom.member.repository.MemberRepository;
import com.zerobase.cafebom.product.controller.form.ProductForm;
import com.zerobase.cafebom.product.domain.entity.Product;
import com.zerobase.cafebom.product.repository.ProductRepository;
import com.zerobase.cafebom.product.service.dto.ProductDto;
import com.zerobase.cafebom.productcategory.domain.entity.ProductCategory;
import com.zerobase.cafebom.productcategory.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.zerobase.cafebom.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final S3UploaderService s3UploaderService;
    private final MemberRepository memberRepository;

    // 관리자 상품 등록-jiyeon-23.08.25
    @Override
    public void addProduct(MultipartFile image, ProductDto productDto) throws IOException {
        String pictureUrl = s3UploaderService.uploadFileToS3(image, "dirName");

        Integer productCategoryId = productDto.getProductCategory();
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT_CATEGORY));

        productRepository.save(Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .productCategory(productCategory)
                .price(productDto.getPrice())
                .soldOutStatus(productDto.getSoldOutStatus())
                .picture(pictureUrl)
                .build());
    }

    // 관리자 상품 수정-jiyeon-23.08.25
    @Override
    @Transactional
    public boolean modifyProduct(MultipartFile image, Integer id, ProductForm productForm) throws IOException {
        Optional<Product> updateProduct = productRepository.findById(id);

        if (updateProduct.isPresent()) {
            Product productToUpdate = updateProduct.get();

            productToUpdate.modifyProductForm(productForm);

            if (image != null && !image.isEmpty()) {
                String oldPicture = productToUpdate.getPicture();
                if (oldPicture != null) {
                    s3UploaderService.deleteFile(oldPicture);
                }

                String newImageUrl = s3UploaderService.uploadFileToS3(image, "dirName");

                productToUpdate = productToUpdate.toBuilder()
                        .picture(newImageUrl)
                        .build();
            }

            System.out.println("productToUpdate = " + productToUpdate.toString());
            return true;
        }

        return false;
    }

    // 관리자 상품 삭제-jiyeon-23.08.25
    @Override
    public void removeProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
        productRepository.deleteById(product.getId());
    }

}

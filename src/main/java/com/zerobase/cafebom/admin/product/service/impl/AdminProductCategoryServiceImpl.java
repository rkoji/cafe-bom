package com.zerobase.cafebom.admin.product.service.impl;

import com.zerobase.cafebom.admin.product.dto.AdminProductCategoryDto;
import com.zerobase.cafebom.admin.product.service.AdminProductCategoryService;
import com.zerobase.cafebom.common.exception.CustomException;
import com.zerobase.cafebom.front.product.domain.ProductCategory;
import com.zerobase.cafebom.front.product.domain.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.cafebom.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AdminProductCategoryServiceImpl implements AdminProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    // 관리자 상품 카테고리 등록-jiyeon-23.09.12
    @Override
    public void addProductCategory(AdminProductCategoryDto.Request productCategoryDto) {

        productCategoryRepository.findByName(productCategoryDto.getName())
                        .ifPresent(name->{
                            throw new CustomException(PRODUCTCATEGORY_ARLEADY_EXISTS);
                        });

        productCategoryRepository.save(
                ProductCategory.builder()
                        .name(productCategoryDto.getName())
                        .build());
    }

    // 상품 카테고리 수정-jiyeon-23.09.12
    @Transactional
    @Override
    public void modifyProductCategory(Integer productCategoryId, AdminProductCategoryDto.Request productCategoryDto) {

        productCategoryRepository.findByName(productCategoryDto.getName())
                .ifPresent(name->{
                    throw new CustomException(PRODUCTCATEGORY_ARLEADY_EXISTS);
                });

        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new CustomException(PRODUCTCATEGORY_NOT_EXISTS));
        productCategory.modifyProductCategory(productCategoryDto.getName());
    }

    // 상품 카테고리 삭제-jiyeon-23.08.31
    @Override
    public void removeProductCategory(Integer productCategoryId) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_EXISTS));
        productCategoryRepository.deleteById(productCategory.getId());
    }

    // 상품 카테고리 전체 조회-jiyeon-23.08.31
    @Override
    public List<AdminProductCategoryDto.Response> findAllProductCategory() {
        List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
        List<AdminProductCategoryDto.Response> productCategoryDtoList = productCategoryList.stream()
                .map(AdminProductCategoryDto.Response::from)
                .collect(Collectors.toList());
        return productCategoryDtoList;
    }

    // 상품 카테고리Id 별 조회-jiyeon-23.08.31
    @Override
    public AdminProductCategoryDto.Response findByIdProductCategory(Integer productCategoryId) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new CustomException(PRODUCTCATEGORY_NOT_EXISTS));
        AdminProductCategoryDto.Response dtoResponse = AdminProductCategoryDto.Response.from(productCategory);
        return dtoResponse;
    }
}


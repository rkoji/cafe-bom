package com.zerobase.cafebom.product.controller.form;

import com.zerobase.cafebom.product.domain.entity.SoldOutStatus;
import com.zerobase.cafebom.productcategory.domain.entity.ProductCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductForm {

    private Integer id;

    private ProductCategory productCategoryId;

    private String name;

    private String description;

    private Integer price;

    private SoldOutStatus soldOutStatus;

    private String picture;

}
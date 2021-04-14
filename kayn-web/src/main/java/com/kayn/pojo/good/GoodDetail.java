package com.kayn.pojo.good;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GoodDetail {
    private Long productId;
    private Double salePrice;
    private String productName;
    private String subTitle;
    private int limitNum;
    private String productImageBig;
    private String detail;
    private List<String> productImageSmall;
}

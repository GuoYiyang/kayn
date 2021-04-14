package com.kayn.pojo.good;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Good {
    private Long productId;
    private Double salePrice;
    private String productName;
    private String subTitle;
    private String productImageBig;
}

package com.kayn.pojo.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_mycart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @TableField("product_id")
    private String productId;
    @TableField("sale_price")
    private Double salePrice;
    @TableField("product_name")
    private String productName;
    @TableField("product_img")
    private String productImg;
    @TableField("product_num")
    private Integer productNum;
    private Integer checked;
}

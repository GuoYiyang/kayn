package com.kayn.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_ordergood")
public class OrderGood {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("product_id")
    private Long productId;
    @TableField("order_id")
    private Long orderId;
    @TableField("sale_price")
    private Double salePrice;
    @TableField("product_num")
    private Integer productNum;
    @TableField("limit_num")
    private Integer limitNum;
    @TableField("checked")
    private Integer checked;
    @TableField("product_name")
    private String productName;
    @TableField("product_img")
    private String productImg;
}

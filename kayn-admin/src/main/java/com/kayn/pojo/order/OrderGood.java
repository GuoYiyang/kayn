package com.kayn.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_ordergood")
public class OrderGood {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long productId;
    private Double salePrice;
    private String productName;
    private String productNum;
    private String productImg;
}

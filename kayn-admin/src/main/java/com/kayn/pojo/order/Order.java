package com.kayn.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("kayn_myorder")
public class Order {
    @TableId(value = "order_id",type = IdType.AUTO)
    private Long orderId;
    @TableField("address_id")
    private Long addressId;
    @TableField("username")
    private String username;
    @TableField("order_total")
    private Double orderTotal;
    @TableField("order_status")
    private Integer orderStatus;
    @TableField(value = "create_date")
    private Date createDate;
    @TableField(value = "end_date")
    private Date endDate;
    @TableField("close_date")
    private Date closeDate;
    @TableField("finish_date")
    private Date finishDate;
    @TableField("pay_date")
    private Date payDate;
}

package com.kayn.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kayn.pojo.address.Address;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("kayn_myorder")
public class MyOrder {
    @TableId(type = IdType.AUTO)
    @TableField("order_id")
    private Long orderId;
    @TableField("address_id")
    private Long addressId;
    @TableField(exist = false)
    private Address addressInfo;
    @TableField("username")
    private String username;
    @TableField(exist = false)
    private List<OrderGood> goodsList;
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

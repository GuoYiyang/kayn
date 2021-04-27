package com.kayn.pojo.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_address")
public class OrderAddress {
    @TableId(type = IdType.AUTO)
    private Integer addressId;
    private String username;
    private String name;
    private String tel;
    private String streetName;
    private Integer isDefault;
}

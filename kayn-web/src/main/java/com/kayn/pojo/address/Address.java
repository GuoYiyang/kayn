package com.kayn.pojo.address;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_address")
public class Address {
    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;
    private String username;
    private String name;
    private String tel;
    @TableField("street_name")
    private String streetName;
    @TableField("is_default")
    private Boolean isDefault;
}

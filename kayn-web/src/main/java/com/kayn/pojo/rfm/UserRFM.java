package com.kayn.pojo.rfm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_rfm")
public class UserRFM {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private Long recency;
    private Integer frequency;
    private Double monetary;
    private Integer rfm;
}

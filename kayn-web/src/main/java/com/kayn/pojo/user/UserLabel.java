package com.kayn.pojo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_user_label")
public class UserLabel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String phone;
    private String address;
    private String live;
    private Integer priceRange;
    private Integer totalPayCnt;
    private Double totalPayMoney;
    private Double totalPayAvg;
    private String totalMostQuery;
    private Integer lastPayCnt;
    private Double lastPayMoney;
    private Double lastPayAvg;
    private String lastMostQuery;
    private String catPrefer;
    private Integer rfm;
}

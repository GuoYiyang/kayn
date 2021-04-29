package com.kayn.pojo.idf;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_cat_cnt")
public class CatCnt {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String cat;
    private Integer cnt;
    private Double score;
}

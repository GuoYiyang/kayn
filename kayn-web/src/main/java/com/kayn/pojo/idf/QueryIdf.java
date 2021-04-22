package com.kayn.pojo.idf;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_idf_query")
public class QueryIdf {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String query;
    private Double score;
}

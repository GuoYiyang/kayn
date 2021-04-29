package com.kayn.pojo.idf;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_query_cnt")
public class QueryCnt {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String query;
    private Integer cnt;
    private Double score;
}

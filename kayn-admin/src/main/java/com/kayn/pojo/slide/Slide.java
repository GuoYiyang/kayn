package com.kayn.pojo.slide;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_slide")
public class Slide {
    @TableId(type = IdType.AUTO)
    private String id;
    private String rfm;
    private String url;
    private String pic;
}

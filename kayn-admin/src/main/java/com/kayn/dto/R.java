package com.kayn.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class R {
    private Integer code;
    private String message;
    private Object data;
}

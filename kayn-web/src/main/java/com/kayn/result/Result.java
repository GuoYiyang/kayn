package com.kayn.result;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result<T> {
    private boolean success;
    private String message;
    private int code;
    private long timestamp;
    private T result;
}

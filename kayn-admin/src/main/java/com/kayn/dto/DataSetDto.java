package com.kayn.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DataSetDto<T> {
    private String label;
    private List<T> data;
}

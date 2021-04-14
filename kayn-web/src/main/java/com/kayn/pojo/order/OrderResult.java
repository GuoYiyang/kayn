package com.kayn.pojo.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderResult {
    private Integer total;
    private List<MyOrder> data;
}

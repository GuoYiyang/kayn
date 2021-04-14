package com.kayn.pojo.good;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GoodPage {
    private int total;
    private List<Good> data;
}

package com.kayn.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class RecommenderDto {
    private List<RecDto> recommenderList;
    private Integer totalCnt;
}

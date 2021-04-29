package com.kayn.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RecommenderDto {
    private List<RecDto> recDto;
    private List<String> queryLabelList;
    private List<DataSetDto> queryDataSetList;
    private List<String> catLabelList;
    private List<DataSetDto> catDataSetList;
}

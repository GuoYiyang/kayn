package com.kayn.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RecDto {
    private String username;
    private String preferQuery;
    private Double queryScore;
    private String preferCat;
    private Double catScore;
    private String rfm;
}

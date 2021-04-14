package com.kayn.pojo.good;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelContents {
    private int id;
    private int panelId;
    private int type;
    private long productId;
    private int sortOrder;
    private String fullUrl;
    private String picUrl;
    private long created;
    private long updated;
    private int salePrice;
    private String productName;
    private String subTitle;
    private String productImageBig;
}

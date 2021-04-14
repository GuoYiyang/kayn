package com.kayn.pojo.good;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PanelResult {
    private int id;
    private String name;
    private int type;
    private int sortOrder;
    private int position;
    private int limitNum;
    private int status;
    private String remark;
    private long created;
    private long updated;
    private List<PanelContents> panelContents;
}

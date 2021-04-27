package com.kayn.dto;

import com.kayn.pojo.order.Order;
import com.kayn.pojo.user.UserInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserListDto {
    private List<UserInfo> userList;
    private Integer totalCnt;
}

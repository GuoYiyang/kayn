package com.kayn.pojo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("kayn_user_role")
public class UserRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String role;
}

package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberRoleType {
    GROUP_MEMBER("group_member"),
    GROUP_ADMIN("group_admin"),
    GROUP_PARENT("group_parent"),
    GROUP_OWNER("group_owner");

    private final String code;
}

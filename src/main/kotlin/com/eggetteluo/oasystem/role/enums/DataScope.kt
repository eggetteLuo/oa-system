package com.eggetteluo.oasystem.role.enums

enum class DataScope {
    ALL,             // 全部数据（ADMIN / HR）
    DEPT_AND_CHILD,  // 本部门及所有子部门（MANAGER）
    DEPT,            // 仅本部门
    SELF,            // 仅本人（EMPLOYEE）
    CUSTOM           // 指定部门
}
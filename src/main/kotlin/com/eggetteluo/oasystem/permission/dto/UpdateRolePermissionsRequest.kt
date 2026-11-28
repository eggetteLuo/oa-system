package com.eggetteluo.oasystem.permission.dto

import java.util.UUID

data class UpdateRolePermissionsRequest(
    val permissionIds: Set<UUID>
)

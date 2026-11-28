-- ==========================================
-- OA System Initial Data
-- Database: PostgreSQL
-- ==========================================

-- 1. 初始化标记表
-- 只用于防止每次应用启动时重复初始化基础数据

CREATE TABLE IF NOT EXISTS sys_initialization_record
(
    initialization_key
    VARCHAR
(
    100
) PRIMARY KEY,
    initialized_at TIMESTAMP
(
    6
) NOT NULL
    );


-- ==========================================
-- 2. 初始化四个默认角色
-- ==========================================

INSERT INTO sys_role (id,
                      code,
                      name,
                      description,
                      enabled,
                      built_in,
                      created_at,
                      updated_at)
SELECT gen_random_uuid(),
       v.code,
       v.name,
       v.description,
       TRUE,
       TRUE,
       LOCALTIMESTAMP,
       LOCALTIMESTAMP
FROM (VALUES ('ADMIN', '超级管理员', '拥有系统全部管理权限'),
             ('HR', '人事专员', '负责人事管理相关工作'),
             ('MANAGER', '部门经理', '负责部门管理相关工作'),
             ('EMPLOYEE', '普通员工', '系统普通员工角色')) AS v(code, name, description)
WHERE NOT EXISTS (SELECT 1
                  FROM sys_initialization_record
                  WHERE initialization_key = 'OA_BASE_DATA_V1') ON CONFLICT (code) DO NOTHING;


-- ==========================================
-- 3. 初始化 16 个系统预置权限
-- ==========================================

INSERT INTO sys_permission (id,
                            code,
                            name,
                            description,
                            enabled,
                            built_in,
                            created_at,
                            updated_at)
SELECT gen_random_uuid(),
       v.code,
       v.name,
       v.description,
       TRUE,
       TRUE,
       LOCALTIMESTAMP,
       LOCALTIMESTAMP
FROM (VALUES ('user:create', '创建用户', '允许创建系统用户'),
             ('user:read', '查看用户', '允许查看系统用户信息'),
             ('user:update', '修改用户', '允许修改系统用户信息'),
             ('user:delete', '删除用户', '允许删除系统用户'),
             ('user:profile:read', '查看个人资料', '允许查看当前登录用户的个人资料'),

             ('department:create', '创建部门', '允许创建部门'),
             ('department:read', '查看部门', '允许查看部门信息'),
             ('department:update', '修改部门', '允许修改部门'),
             ('department:delete', '删除部门', '允许删除部门'),

             ('role:create', '创建角色', '允许创建系统角色'),
             ('role:read', '查看角色', '允许查看系统角色'),
             ('role:update', '修改角色', '允许修改系统角色'),
             ('role:delete', '删除角色', '允许删除系统角色'),

             ('permission:read', '查看权限', '允许查看系统权限'),
             ('role:permission:read', '查看角色权限', '允许查看角色已分配的权限'),
             ('role:permission:assign', '分配角色权限', '允许为角色分配或修改权限')) AS v(code, name, description)
WHERE NOT EXISTS (SELECT 1
                  FROM sys_initialization_record
                  WHERE initialization_key = 'OA_BASE_DATA_V1') ON CONFLICT (code) DO NOTHING;


-- ==========================================
-- 4. 初始化系统管理部
-- ==========================================

INSERT INTO sys_department (id,
                            name,
                            description,
                            sort_order,
                            leader_id,
                            enabled,
                            parent_id,
                            created_at,
                            updated_at)
SELECT '00000000-0000-0000-0000-000000000001',
       '系统管理部',
       '系统默认管理部门',
       0,
       NULL,
       TRUE,
       NULL,
       LOCALTIMESTAMP,
       LOCALTIMESTAMP WHERE NOT EXISTS (
    SELECT 1
    FROM sys_initialization_record
    WHERE initialization_key = 'OA_BASE_DATA_V1'
)
ON CONFLICT (id) DO NOTHING;


-- ==========================================
-- 5. 初始化角色与权限关联
-- ==========================================

WITH role_permission_codes(role_code, permission_code) AS (

    -- HR：人事专员
    VALUES ('HR', 'user:create'),
           ('HR', 'user:read'),
           ('HR', 'user:update'),
           ('HR', 'department:read'),

           -- MANAGER：部门经理
           ('MANAGER', 'user:read'),
           ('MANAGER', 'department:read'),

           -- EMPLOYEE：普通员工
           ('EMPLOYEE', 'user:profile:read')),

-- ADMIN 拥有所有启用的系统内置权限
     all_role_permissions AS (SELECT 'ADMIN' AS role_code,
                                     p.code  AS permission_code
                              FROM sys_permission p
                              WHERE p.built_in = TRUE
                                AND p.enabled = TRUE

                              UNION ALL

                              SELECT role_code,
                                     permission_code
                              FROM role_permission_codes)

INSERT
INTO sys_role_permission (
    id,
    role_id,
    permission_id,
    created_at,
    updated_at
)
SELECT gen_random_uuid(),
       r.id,
       p.id,
       LOCALTIMESTAMP,
       LOCALTIMESTAMP
FROM all_role_permissions rp
         JOIN sys_role r
              ON r.code = rp.role_code
         JOIN sys_permission p
              ON p.code = rp.permission_code
WHERE r.enabled = TRUE
  AND p.enabled = TRUE
  AND NOT EXISTS (SELECT 1
                  FROM sys_initialization_record
                  WHERE initialization_key = 'OA_BASE_DATA_V1')
    ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ==========================================
-- 6. 标记基础数据初始化完成
-- ==========================================

INSERT INTO sys_initialization_record (initialization_key,
                                       initialized_at)
VALUES ('OA_BASE_DATA_V1',
        LOCALTIMESTAMP) ON CONFLICT (initialization_key) DO NOTHING;

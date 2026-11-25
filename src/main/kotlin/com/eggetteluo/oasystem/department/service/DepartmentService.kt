package com.eggetteluo.oasystem.department.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.department.dto.CreateDeptRequest
import com.eggetteluo.oasystem.department.dto.DepartmentTreeResponse
import com.eggetteluo.oasystem.department.dto.DeptResponse
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 创建部门
     */
    @Transactional
    fun createDept(request: CreateDeptRequest): DeptResponse {
        val parentDept = request.parentId?.let {
            departmentRepository.findById(it).orElseThrow { BusinessException(404, "上级部门不存在") }
        }

        val dept = DepartmentEntity(
            name = request.name,
            parentId = request.parentId,
            description = request.description,
            sortOrder = request.sortOrder ?: 0
        )
        val saved = departmentRepository.save(dept)

        return DeptResponse(
            id = saved.id!!,
            name = saved.name,
            parentId = saved.parentId,
            parentName = parentDept?.name
        )
    }

    /**
     * 全量部门列表。
     */
    fun getAllDepts(): List<DeptResponse> {
        val list = departmentRepository.findAllByOrderBySortOrderAscNameAsc()
        if (list.isEmpty()) return emptyList()

        val nameMap: Map<UUID, String> = list.associate { it.id!! to it.name }

        return list.map { dept ->
            DeptResponse(
                id = dept.id!!,
                name = dept.name,
                parentId = dept.parentId,
                parentName = dept.parentId?.let { nameMap[it] }
            )
        }
    }

    fun getDeptById(id: UUID): DeptResponse {
        val dept = departmentRepository.findById(id)
            .orElseThrow { BusinessException(404, "部门不存在") }
        val parentName = dept.parentId?.let {
            departmentRepository.findById(it).orElse(null)?.name
        }
        return DeptResponse(dept.id!!, dept.name, dept.parentId, parentName)
    }

    @Transactional
    fun getOrCreateSystemDepartment(): UUID {
        val department = departmentRepository.findByName("系统管理部")
            ?: departmentRepository.save(
                DepartmentEntity(
                    name = "系统管理部",
                    description = "系统内置的最高权限部门"
                )
            )
        return department.id ?: throw IllegalStateException("系统管理部 ID 不存在")
    }

    /**
     * 获取部门树
     */
    @Transactional(readOnly = true)
    fun getDepartmentTree(): List<DepartmentTreeResponse> {
        // 查询所有部门
        val list = departmentRepository.findAllByOrderBySortOrderAscNameAsc()
        if (list.isEmpty()) return emptyList()

        // 建立 id 索引
        val voMap: Map<UUID, DepartmentTreeResponse> = list.associate { dept ->
            dept.id!! to DepartmentTreeResponse(
                id = dept.id!!,
                name = dept.name,
                description = dept.description,
                parentId = dept.parentId,
                sortOrder = dept.sortOrder,
                leaderId = dept.leaderId
            )
        }

        // 挂载父子关系
        val roots = mutableListOf<DepartmentTreeResponse>()
        for (vo in voMap.values) {
            val pid = vo.parentId
            if (pid == null || pid !in voMap) {
                roots.add(vo)
            } else {
                voMap[pid]!!.children.add(vo)
            }
        }

        // 环检测
        val reachable = mutableSetOf<UUID>()
        fun mark(node: DepartmentTreeResponse) {
            if (!reachable.add(node.id)) return
            node.children.forEach { mark(it) }
        }
        roots.forEach { mark(it) }

        val cyclic = voMap.values.filter { it.id !in reachable }
        if (cyclic.isNotEmpty()) {
            log.error("检测到部门循环引用，节点: {}", cyclic.map { it.id })
            throw BusinessException(401, "部门数据存在循环引用，请检查 parentId 配置")
        }

        // 同级再排一次，保证 children 顺序稳定
        fun sortRecursively(nodes: MutableList<DepartmentTreeResponse>) {
            nodes.sortWith(compareBy({ it.sortOrder }, { it.name }))
            nodes.forEach { sortRecursively(it.children) }
        }
        sortRecursively(roots)

        return roots
    }

}
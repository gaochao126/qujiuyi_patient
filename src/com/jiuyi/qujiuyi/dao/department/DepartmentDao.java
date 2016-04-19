package com.jiuyi.qujiuyi.dao.department;

import java.util.List;

import com.jiuyi.qujiuyi.dto.department.DepartmentDto;

/**
 * @description 医生dao层接口
 * @author zhb
 * @createTime 2015年4月9日
 */
public interface DepartmentDao {
    /**
     * @description 获取科室列表
     * @param departmentDto
     * @return
     * @throws Exception
     */
    public List<DepartmentDto> queryDepartmentList(DepartmentDto departmentDto) throws Exception;

    /**
     * @description 根据科室id获取科室信息
     * @param departmentDto
     * @return
     * @throws Exception
     */
    public DepartmentDto queryDepartmentById(DepartmentDto departmentDto) throws Exception;

    /**
     * @description 获取随机科室
     * @return
     * @throws Exception
     */
    public List<DepartmentDto> getRandomDepartment() throws Exception;

    /**
     * @description 根据线下医生获取医院科室信息
     * @param departmentDto
     * @return
     * @throws Exception
     */
    public DepartmentDto getHospitalDepartmentByHospitalDoctor(DepartmentDto departmentDto) throws Exception;
}
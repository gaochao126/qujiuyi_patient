package com.jiuyi.qujiuyi.service.department;

import java.util.List;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.department.DepartmentDto;

/**
 * @description 医生业务接口
 * @author zhb
 * @createTime 2015年4月8日
 */
public interface DepartmentService {
	/**
	 * @description 获取科室列表
	 * @param departmentDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryDepartmentList(DepartmentDto departmentDto) throws Exception;

	/**
	 * @description 获取推荐科室列表
	 * @param departmentDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryRecommendDepartmentList(DepartmentDto departmentDto) throws Exception;

	/**
	 * @description 刷新推荐科室列表
	 * @return
	 * @throws Exception
	 */
	public List<DepartmentDto> refreshRecommendDepartmentList() throws Exception;

	/**
	 * 查询科室
	 * 
	 * @param departmentDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryDepart(DepartmentDto departmentDto) throws Exception;
}
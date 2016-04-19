package com.jiuyi.qujiuyi.service.register;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.register.RegisterPlanDto;

/**
 * @description 挂号计划业务层接口
 * @author zhb
 * @createTime 2015年8月12日
 */
public interface RegisterPlanService {
	/**
	 * @description 获取医生工作计划
	 * @param registerPlanDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getDoctorRegisterPlan(RegisterPlanDto registerPlanDto) throws Exception;

	/**
	 * 
	 * @number			@description	获取医生工作计划，从院放获取
	 * 
	 * @param registerPlanDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月22日
	 */
	public ResponseDto getDoctorRegisterPlanPlus(RegisterPlanDto registerPlanDto) throws Exception;
}
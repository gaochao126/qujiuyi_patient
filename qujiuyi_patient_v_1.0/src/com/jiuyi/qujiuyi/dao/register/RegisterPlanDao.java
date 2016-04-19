package com.jiuyi.qujiuyi.dao.register;

import java.util.List;

import com.jiuyi.qujiuyi.dto.register.PatientRegisterDto;
import com.jiuyi.qujiuyi.dto.register.RegisterPlanDto;

public interface RegisterPlanDao {
    /**
     * @description 获取医生工作计划
     * @param registerPlanDto
     * @return
     * @throws Exception
     */
    public List<RegisterPlanDto> getDoctorRegisterPlan(RegisterPlanDto registerPlanDto) throws Exception;

    /**
     * @description 根据id获取挂号计划
     * @param registerPlanDto
     * @return
     * @throws Exception
     */
    public RegisterPlanDto getRegisterPlanById(RegisterPlanDto registerPlanDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 更新已经挂号数量（增加）
	 * 
	 * @throws Exception
	 * @Date 2015年11月11日
	 */
	public void updateAlreadyRegisterCountUp(RegisterPlanDto registerPlanDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 更新已经挂号数量（减少）
	 * 
	 * @param registerPlanDto
	 * @throws Exception
	 * @Date 2015年11月11日
	 */
	public void updateAlreadyRegisterCountDown(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 根据医生挂号计划ID减少已挂号数量
	 * 
	 * @param registerPlanDto
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	public void updateAlreadyRegisterCountDownByPlanId(RegisterPlanDto registerPlanDto) throws Exception;
}
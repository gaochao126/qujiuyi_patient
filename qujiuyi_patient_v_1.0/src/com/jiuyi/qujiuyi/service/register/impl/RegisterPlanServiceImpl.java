package com.jiuyi.qujiuyi.service.register.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.department.DepartmentDao;
import com.jiuyi.qujiuyi.dao.register.RegisterPlanDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.department.DepartmentDto;
import com.jiuyi.qujiuyi.dto.register.RegisterPlanDto;
import com.jiuyi.qujiuyi.service.BaseService;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.register.RegisterPlanService;

/**
 * @description 挂号计划业务层实现
 * @author zhb
 * @createTime 2015年8月12日
 */
@Service
public class RegisterPlanServiceImpl implements RegisterPlanService {
	@Autowired
	private RegisterPlanDao registerPlanDao;

	@Autowired
	private DepartmentDao departmentDao;

	/**
	 * @description 获取医生工作计划
	 * @param registerPlanDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getDoctorRegisterPlan(RegisterPlanDto registerPlanDto) throws Exception {
		if (registerPlanDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (registerPlanDto.getWeek() == null) {
			throw new BusinessException("week参数不能为空");
		}

		if (registerPlanDto.getWeek() < 1 || registerPlanDto.getWeek() > 3) {
			throw new BusinessException("week值无效");
		}

		// 设置起始和结束时间
		Date currentTime = new Date();
		Date currentDate = Util.getDateListOfWeek(currentTime).get(Util.getDayOfWeek(currentTime) - 1);
		if (registerPlanDto.getWeek() == 1) {
			registerPlanDto.setStartTime(currentTime);
			registerPlanDto.setEndTime(Util.getDateListOfWeek(currentTime).get(6));
		} else if (registerPlanDto.getWeek() == 2) {
			Date time = new Date(currentTime.getTime() + 7 * 24 * 60 * 60 * 1000L);
			registerPlanDto.setStartTime(Util.getDateListOfWeek(time).get(0));
			registerPlanDto.setEndTime(Util.getDateListOfWeek(time).get(6));
		} else {
			Date time = new Date(currentTime.getTime() + 14 * 24 * 60 * 60 * 1000L);
			registerPlanDto.setStartTime(Util.getDateListOfWeek(time).get(0));
			registerPlanDto.setEndTime(Util.getDateListOfWeek(time).get(6));
		}

		List<RegisterPlanDto> list = registerPlanDao.getDoctorRegisterPlan(registerPlanDto);
		List<RegisterPlanDto> newList = new ArrayList<RegisterPlanDto>();
		if (list != null && !list.isEmpty()) {
			// 获取科室信息
			DepartmentDto departmentDto = new DepartmentDto();
			departmentDto.setDoctorId(registerPlanDto.getDoctorId());
			departmentDto = departmentDao.getHospitalDepartmentByHospitalDoctor(departmentDto);
			String t1 = departmentDto != null && departmentDto.getRegisterOverTime_0() != null ? departmentDto.getRegisterOverTime_0() : "11:00";
			String t2 = departmentDto != null && departmentDto.getRegisterOverTime_1() != null ? departmentDto.getRegisterOverTime_1() : "16:00";
			String t3 = departmentDto != null && departmentDto.getRegisterOverTime_2() != null ? departmentDto.getRegisterOverTime_2() : "20:00";
			long _t1 = currentDate.getTime() + Long.parseLong(t1.split(":")[0]) * 3600000 + Long.parseLong(t1.split(":")[1]) * 60000;
			long _t2 = currentDate.getTime() + Long.parseLong(t2.split(":")[0]) * 3600000 + Long.parseLong(t2.split(":")[1]) * 60000;
			long _t3 = currentDate.getTime() + Long.parseLong(t3.split(":")[0]) * 3600000 + Long.parseLong(t3.split(":")[1]) * 60000;
			for (RegisterPlanDto plan : list) {
				if (currentDate.getTime() < plan.getRegisterDate().getTime()) {
					newList.add(plan);
				} else if (currentDate.getTime() == plan.getRegisterDate().getTime()) {
					if (plan.getTimeZone() != null && plan.getTimeZone() == 0) {
						if (currentTime.getTime() < _t1) {
							newList.add(plan);
						}
					} else if (plan.getTimeZone() != null && plan.getTimeZone() == 1) {
						if (currentTime.getTime() < _t2) {
							newList.add(plan);
						}
					} else if (plan.getTimeZone() != null && plan.getTimeZone() == 2) {
						if (currentTime.getTime() < _t3) {
							newList.add(plan);
						}
					}
				}
			}
		}

		// 返回结果
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("list", newList);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * 
	 * @number 2 @description 获取医生工作计划，从院放获取
	 * 
	 * @param registerPlanDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月22日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDto getDoctorRegisterPlanPlus(RegisterPlanDto registerPlanDto) throws Exception {
		if (registerPlanDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (registerPlanDto.getHospitalId() == null) {
			throw new BusinessException("请输入医院ID");
		}
		if (registerPlanDto.getDoctorId() == null) {
			throw new BusinessException("请输入医生ID");
		}

		if (registerPlanDto.getWeek() == null) {
			throw new BusinessException("week参数不能为空");
		}

		if (registerPlanDto.getWeek() < 1 || registerPlanDto.getWeek() > 3) {
			throw new BusinessException("week值无效");
		}

		// 设置起始和结束时间
		Date currentTime = new Date();
		Date currentDate = Util.nowFormatDate(new Date());
		System.out.println(currentDate);
		if (registerPlanDto.getWeek() == 1) {
			registerPlanDto.setStartTime(currentDate);
			registerPlanDto.setEndTime(Util.getDateListOfWeek(currentTime).get(6));
		} else if (registerPlanDto.getWeek() == 2) {
			Date time = new Date(currentTime.getTime() + 7 * 24 * 60 * 60 * 1000L);
			registerPlanDto.setStartTime(Util.getDateListOfWeek(time).get(0));
			registerPlanDto.setEndTime(Util.getDateListOfWeek(time).get(6));
		} else {
			Date time = new Date(currentTime.getTime() + 14 * 24 * 60 * 60 * 1000L);
			registerPlanDto.setStartTime(Util.getDateListOfWeek(time).get(0));
			registerPlanDto.setEndTime(Util.getDateListOfWeek(time).get(6));
		}

		// 获取挂号计划
		BaseService bservice = new BaseService();
		ResponseDto responseDto = new ResponseDto();
		bservice.getParams().put("startTime", Util.DateToStr(registerPlanDto.getStartTime()));
		bservice.getParams().put("endTime", Util.DateToStr(registerPlanDto.getEndTime()));

		bservice.getParams().put("hospitalId", registerPlanDto.getHospitalId().toString());
		bservice.getParams().put("doctorId", registerPlanDto.getDoctorId().toString());

		bservice.packageData("getNumSource", RegisterPlanDto.class, SysCfg.getString("register.plus.url"));
		if (!bservice.isSuccess()) {
			throw new BusinessException(bservice.getDesc());
		}
		List<RegisterPlanDto> list = (List<RegisterPlanDto>) bservice.getDataList();
		List<RegisterPlanDto> newList = new ArrayList<RegisterPlanDto>();

		if (list != null && !list.isEmpty()) {
			// 获取科室信息
			DepartmentDto departmentDto = new DepartmentDto();
			departmentDto.setDoctorId(registerPlanDto.getDoctorId());
			departmentDto = departmentDao.getHospitalDepartmentByHospitalDoctor(departmentDto);
			String t1 = departmentDto != null && departmentDto.getRegisterOverTime_0() != null ? departmentDto.getRegisterOverTime_0() : "11:00";
			String t2 = departmentDto != null && departmentDto.getRegisterOverTime_1() != null ? departmentDto.getRegisterOverTime_1() : "16:00";
			String t3 = departmentDto != null && departmentDto.getRegisterOverTime_2() != null ? departmentDto.getRegisterOverTime_2() : "20:00";
			long _t1 = currentDate.getTime() + Long.parseLong(t1.split(":")[0]) * 3600000 + Long.parseLong(t1.split(":")[1]) * 60000;
			long _t2 = currentDate.getTime() + Long.parseLong(t2.split(":")[0]) * 3600000 + Long.parseLong(t2.split(":")[1]) * 60000;
			long _t3 = currentDate.getTime() + Long.parseLong(t3.split(":")[0]) * 3600000 + Long.parseLong(t3.split(":")[1]) * 60000;
			for (RegisterPlanDto plan : list) {
				if (currentDate.getTime() < plan.getScheduleDate().getTime()) {
					newList.add(plan);
				} else if (currentDate.getTime() == plan.getScheduleDate().getTime()) {
					if (plan.getTimeRange() != null && plan.getTimeRange() == 0) {
						if (currentTime.getTime() < _t1) {
							newList.add(plan);
						}
					} else if (plan.getTimeRange() != null && plan.getTimeRange() == 1) {
						if (currentTime.getTime() < _t2) {
							newList.add(plan);
						}
					} else if (plan.getTimeRange() != null && plan.getTimeRange() == 2) {
						if (currentTime.getTime() < _t3) {
							newList.add(plan);
						}
					}
				}
			}
		}

		responseDto.setResultDesc("医生挂号计划");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", newList);
		responseDto.setDetail(map);
		return responseDto;
	}
}
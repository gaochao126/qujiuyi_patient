package com.jiuyi.qujiuyi.service.doctor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.MD5;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.department.DepartmentDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.department.DepartmentDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.doctor.DoctorService;

/**
 * @description 医生业务实现类
 * @author zhb
 * @createTime 2015年4月8日
 */
@Service
public class DoctorServiceImpl implements DoctorService {
	@Autowired
	private DoctorDao doctorDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private ServiceDao serviceDao;

	/**
	 * @description 根据科室id获取医生列表
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryDoctorListByDepartmentId(DoctorDto doctorDto) throws Exception {
		/** step1:空异常处理. */
		if (doctorDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验科室. */
		if (doctorDto.getDepartmentId() == null) {
			throw new BusinessException("科室id不能为空");
		}

		/** step3:根据科室id获取科室信息. */
		DepartmentDto departmentDto = new DepartmentDto();
		departmentDto.setId(doctorDto.getDepartmentId());
		departmentDto = departmentDao.queryDepartmentById(departmentDto);

		/** step4:判断科室是否存在. */
		if (departmentDto == null) {
			throw new BusinessException("科室不存在");
		}

		/** step5:获取科室分类. */
		if (departmentDto.getParentId() != null && departmentDto.getParentId().equals(departmentDto.getId())) {
			doctorDto.setDepartmentType(1);
		} else {
			doctorDto.setDepartmentType(2);
		}

		/** step6:获取医生列表. */
		List<DoctorDto> list = doctorDao.queryDoctorListByDepartmentId(doctorDto);

		/** step7:返回结果. */
		list = list == null ? new ArrayList<DoctorDto>() : list;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("page", doctorDto.getPage());
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("医生列表查询成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 获取医生信息
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryDoctorInfo(DoctorDto doctorDto) throws Exception {
		/** step1:空异常处理. */
		if (doctorDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验科室. */
		if (doctorDto.getId() == null) {
			throw new BusinessException("医生id不能为空");
		}

		/** step3:获取医生信息. */
		DoctorDto dto = doctorDao.queryDoctorInfo(doctorDto);
		if (Util.isNotEmpty(dto.getHead()) && !dto.getHead().startsWith("http")) {
			dto.setHead(SysCfg.getString("doctor.head.path") + dto.getHead());
		}

		/** step4:获取医人服务. */
		List<ServiceDto> serviceList = new ArrayList<ServiceDto>();
		if (dto != null) {
			dto.setMd5Id(MD5.getMD5Code(MD5.getMD5Code(dto.getId().toString())));
			ServiceDto serviceDto = new ServiceDto();
			serviceDto.setDoctorId(dto.getId());

			// 获取图文咨询
			List<ServiceDto> list1 = serviceDao.queryConsultServiceByDoctorId(serviceDto);
			if (list1 != null && !list1.isEmpty()) {
				for (ServiceDto service : list1) {
					service.setType(0);
					service.setServiceType(1);
				}
				serviceList.addAll(list1);
			}

			// 获取私人医生
			List<ServiceDto> list2 = serviceDao.queryPersonalDoctorServiceByDoctorId(serviceDto);
			if (list2 != null && !list2.isEmpty()) {
				for (ServiceDto service : list2) {
					service.setServiceType(2);
				}
				serviceList.addAll(list2);
			}

			// 获取配药服务
			List<ServiceDto> list3 = serviceDao.queryPrescribeServiceByDoctorId(serviceDto);
			if (list3 != null && !list3.isEmpty()) {
				for (ServiceDto service : list3) {
					service.setType(0);
					service.setPrice(0);
					service.setServiceType(3);
				}
				serviceList.addAll(list3);
			}

			// 获取预约服务
			List<ServiceDto> list4 = serviceDao.queryAppointmentServiceByDoctorId(serviceDto);
			if (list4 != null && !list4.isEmpty()) {
				for (ServiceDto service : list4) {
					service.setType(0);
					service.setServiceType(4);
				}
				serviceList.addAll(list4);
			}

			dto.setServiceList(serviceList);
		}

		/** step6:返回结果. */

		dto = dto == null ? new DoctorDto() : dto;

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(dto);
		return responseDto;
	}

	/**
	 * @description 获取1元医生列表
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryOneYuanDoctorList(DoctorDto doctorDto) throws Exception {
		doctorDto = doctorDto == null ? new DoctorDto() : doctorDto;
		List<DoctorDto> list = doctorDao.queryOneYuanDoctorList(doctorDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", doctorDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 搜索医生
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto searchDoctors(DoctorDto doctorDto) throws Exception {
		doctorDto = doctorDto == null ? new DoctorDto() : doctorDto;
		List<DoctorDto> list = doctorDao.searchDoctors(doctorDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("搜索成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", doctorDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 获取医院医生列表
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getHospitalDoctors(DoctorDto doctorDto) throws Exception {
		doctorDto = doctorDto == null ? new DoctorDto() : doctorDto;
		List<DoctorDto> list = doctorDao.getHospitalDoctors(doctorDto);
		for (DoctorDto doctor : list) {
			if (Util.isNotEmpty(doctor.getHead())) {
				doctor.setHead(SysCfg.getString("doctor.head.path") + doctor.getHead());
			}
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("搜索成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", doctorDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 获取医院医生详情
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getHospitalDoctorDetail(DoctorDto doctorDto) throws Exception {
		DoctorDto dto = doctorDao.getHospitalDoctorDetail(doctorDto);
		if (dto != null && Util.isNotEmpty(dto.getHead())) {
			dto.setHead(SysCfg.getString("doctor.head.path") + dto.getHead());
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("查询成功");
		responseDto.setDetail(dto);
		return responseDto;
	}

	/**
	 * @description 获取名医推荐
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getFamousDoctors(DoctorDto doctorDto) throws Exception {
		List<DoctorDto> list = doctorDao.getFamousDoctors(doctorDto);

		list = list == null ? new ArrayList<DoctorDto>() : list;
		for (DoctorDto doctor : list) {
			if (Util.isNotEmpty(doctor.getHead()) && !doctor.getHead().startsWith("http")) {
				doctor.setHead(SysCfg.getString("doctor.head.path") + doctor.getHead());
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		ResponseDto responseDto = new ResponseDto();
		map.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 根据条件搜索医生
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryDoctorByConditions(DoctorDto doctorDto) throws Exception {
		if (Util.isNotEmpty(doctorDto.getTitleName())) {
			String[] title = doctorDto.getTitleName().split("#");
			for (int i = 0; i < title.length; i++) {
				if (i == 0) {
					doctorDto.setTitleId(Integer.parseInt(title[i]));
					doctorDto.setFirst(Integer.parseInt(title[i]));
				}
				if (i == 1) {
					doctorDto.setSecond(Integer.parseInt(title[i]));
				}
				if (i == 2) {
					doctorDto.setThird(Integer.parseInt(title[i]));
				}
				if (i == 3) {
					doctorDto.setFour(Integer.parseInt(title[i]));
				}
			}
		}
		List<DoctorDto> list = doctorDao.queryDoctorByConditions(doctorDto);

		list = list == null ? new ArrayList<DoctorDto>() : list;
		for (DoctorDto doctor : list) {
			if (Util.isNotEmpty(doctor.getHead()) && !doctor.getHead().startsWith("http")) {
				doctor.setHead(SysCfg.getString("doctor.head.path") + doctor.getHead());
			}
			if (Util.isNotEmpty(doctor.getDepartmentIcon())) {
				doctor.setDepartmentIcon(SysCfg.getString("department.icon") + doctor.getDepartmentIcon());
			}
			if (doctor.getDoctorType() != null && doctor.getDoctorType() == 0) {
				// 得到当前时间
				Date nowDate = new Date();
				DoctorDto doc = new DoctorDto();
				doc.setNowDate(nowDate);
				doc.setId(doctor.getId());
				doctor.setLaveCount(doctorDao.queryOfflineDoctorRegisterPlanCount(doc));
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		ResponseDto responseDto = new ResponseDto();
		map.put("list", list);
		map.put("page", doctorDto.getPage());
		responseDto.setResultDesc("搜索成功");
		responseDto.setDetail(map);
		return responseDto;
	}
}
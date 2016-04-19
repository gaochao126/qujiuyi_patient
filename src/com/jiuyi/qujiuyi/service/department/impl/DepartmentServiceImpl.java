package com.jiuyi.qujiuyi.service.department.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.department.DepartmentDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.department.DepartmentDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.department.DepartmentService;

/**
 * @description 医生业务实现类
 * @author zhb
 * @createTime 2015年4月8日
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private DoctorDao doctorDao;

	/**
	 * @description 获取科室列表
	 * @param departmentDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryDepartmentList(DepartmentDto departmentDto) throws Exception {
		/** step1:空异常处理. */
		if (departmentDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:判断查询类型是否合法. */
		if (departmentDto.getQueryType() == null || (departmentDto.getQueryType() != 1 && departmentDto.getQueryType() != 2 && departmentDto.getQueryType() != 3)) {
			throw new BusinessException("查询类型未知");
		}

		/** step3:查询数据. */
		List<DepartmentDto> list = departmentDao.queryDepartmentList(departmentDto);
		for (int i = 0; i < list.size(); i++) {
			if (Util.isNotEmpty(list.get(i).getIcon())) {
				list.get(i).setIcon(SysCfg.getString("department.icon") + list.get(i).getIcon());
			}
		}
		/** step4:返回结果. */
		list = list == null ? new ArrayList<DepartmentDto>() : list;
		Map<String, List<DepartmentDto>> map = new HashMap<String, List<DepartmentDto>>();
		map.put("list", list);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("科室列表查询成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 获取推荐科室列表
	 * @param departmentDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryRecommendDepartmentList(DepartmentDto departmentDto) throws Exception {
		/** step4:返回结果. */
		List<DepartmentDto> list = CacheContainer.getRecommendDepartmentList();
		Map<String, List<DepartmentDto>> map = new HashMap<String, List<DepartmentDto>>();
		map.put("list", list);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取推荐科室列表成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 刷新推荐科室列表
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DepartmentDto> refreshRecommendDepartmentList() throws Exception {
		// 随机获取科室
		List<DepartmentDto> list = departmentDao.getRandomDepartment();
		int i = 0;
		while (i < 20) {
			i++;
			if (list != null && list.size() == 2) {
				break;
			}
		}

		if (list == null || list.isEmpty()) {
			return null;
		}

		// 随机获取科室下的医生
		for (DepartmentDto dto : list) {
			DoctorDto doctor = new DoctorDto();
			doctor.setDepartmentId(dto.getId());
			List<DoctorDto> doctorList = null;
			int j = 0;
			while (j < 20) {
				j++;
				doctorList = doctorDao.getRandomDoctorByDepartment(doctor);
				if (doctorList != null && doctorList.size() == 2) {
					break;
				}
			}
			dto.setDoctorList(doctorList);
		}

		List<DepartmentDto> list1 = new ArrayList<DepartmentDto>();
		for (DepartmentDto dto : list) {
			if (dto.getDoctorList() != null && dto.getDoctorList().size() == 2) {
				list1.add(dto);
			}
		}
		return list1 != null && !list1.isEmpty() ? list1 : null;

	}
}
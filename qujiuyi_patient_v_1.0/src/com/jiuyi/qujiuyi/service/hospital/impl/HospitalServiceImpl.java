package com.jiuyi.qujiuyi.service.hospital.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.dao.collect.PatientCollectDao;
import com.jiuyi.qujiuyi.dao.hospital.HospitalDao;
import com.jiuyi.qujiuyi.dto.collect.PatientCollectDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.hospital.HospitalDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.hospital.HospitalService;

/**
 * @description 医院业务层接口实现
 * @author zhb
 * @createTime 2015年8月7日
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalDao hospitalDao;
    
    @Autowired
    private PatientCollectDao patientCollectDao;

    /**
     * @description 获取医院列表
     * @param hospitalDto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto getHospitalList(HospitalDto hospitalDto) throws Exception {
        if (hospitalDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        List<HospitalDto> list = hospitalDao.getHospitalList(hospitalDto);
        if (list != null && !list.isEmpty()) {
            for (HospitalDto dto : list) {
                if (dto.getHead() != null && !dto.getHead().startsWith("http")) {
                    dto.setHead(SysCfg.getString("hospital.head.path") + dto.getHead());
                }
            }
        }
        
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", list);
        detail.put("page", hospitalDto.getPage());
        responseDto.setDetail(detail);
        return responseDto;
    }

	/**
	 * 
	 * @number
	 * @description 查询医院详情
	 * 
	 * @param hospitalDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月3日
	 */
	@Override
	public ResponseDto hospitalDetail(HospitalDto hospitalDto) throws Exception {
		if (hospitalDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (hospitalDto.getId() == null) {
			throw new BusinessException("医院ID不能为空");
		}
		HospitalDto hospital = hospitalDao.hospitalDetail(hospitalDto);
		PatientCollectDto patientCollectDto = new PatientCollectDto();
		if(hospitalDto.getToken()!=null){
			TokenDto token = CacheContainer.getToken(hospitalDto.getToken());
			PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
			patientCollectDto.setPatientId(patient.getId());
		}
		patientCollectDto.setHospitalId(hospitalDto.getId());
		List<PatientCollectDto> list = patientCollectDao.queryCollectHospitalList(patientCollectDto);
		ResponseDto responseDto = new ResponseDto();
		if (list == null || list.isEmpty()) {
			hospital.setIsCollect(0);;
		} else {
			hospital.setIsCollect(1);
		}
		responseDto.setResultDesc("查询成功");
		responseDto.setDetail(hospital);
		return responseDto;
		
	}
}
package com.jiuyi.qujiuyi.service.hospital;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.hospital.HospitalDto;

/**
 * @description 医院业务接口
 * @author zhb
 * @createTime 2015年8月7日
 */
public interface HospitalService {
    /**
     * @description 获取医院列表
     * @param hospitalDto
     * @return
     * @throws Exception
     */
    public ResponseDto getHospitalList(HospitalDto hospitalDto) throws Exception;

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
	public ResponseDto hospitalDetail(HospitalDto hospitalDto) throws Exception;
}
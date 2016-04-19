package com.jiuyi.qujiuyi.dao.hospital;

import java.util.List;

import com.jiuyi.qujiuyi.dto.hospital.HospitalDto;

/**
 * @description 医院dao层接口
 * @author zhb
 * @createTime 2015年8月7日
 */
public interface HospitalDao {
    /**
     * @description 获取医院列表
     * @param hospitalDto
     * @return
     * @throws Exception
     */
    public List<HospitalDto> getHospitalList(HospitalDto hospitalDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 通过ID查询医院
	 * 
	 * @param hospitalDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月3日
	 */
	public HospitalDto hospitalDetail(HospitalDto hospitalDto) throws Exception;

}
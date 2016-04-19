package com.jiuyi.qujiuyi.dao.guide;

import java.util.List;

import com.jiuyi.qujiuyi.dto.guide.GuidPatientDto;

/**
 * @author superb    @Date 2016年1月21日
 * 
 * @Description 	智能导诊
 *
 * @Copyright 2016 重庆柒玖壹健康管理有限公司
 */
public interface GuidingPatientDao {
	/**
	 * 
	 * @number	1		@description	查询身体部位集合
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	public List<GuidPatientDto> queryBodyPart(GuidPatientDto guidPatientDto) throws Exception;

	/**
	 * 
	 * @number	2		@description 查询疾病
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	public List<GuidPatientDto> queryIllness(GuidPatientDto guidPatientDto) throws Exception;
}

package com.jiuyi.qujiuyi.service.guide;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.guide.GuidPatientDto;

/**
 * @author superb    @Date 2016年1月21日
 * 
 * @Description 
 *
 * @Copyright 2016 重庆柒玖壹健康管理有限公司
 */
public interface GuidingPatientService {
	/**
	 * 
	 * @number	1		@description	查询身体部位
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	public ResponseDto queryBodyPart(GuidPatientDto guidPatientDto) throws Exception;

	/**
	 * 
	 * @number	2		@description	查询疾病
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	public ResponseDto queryIllness(GuidPatientDto guidPatientDto) throws Exception;
}

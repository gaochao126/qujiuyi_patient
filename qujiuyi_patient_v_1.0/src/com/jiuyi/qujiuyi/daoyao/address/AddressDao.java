package com.jiuyi.qujiuyi.daoyao.address;

import java.util.List;

import com.jiuyi.qujiuyi.dto.area.AreaDto;

/**
 * @author superb @Date 2015年12月23日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface AddressDao {
	/**
	 * 
	 * @number 1 @description 查询省份
	 * 
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> queryProvice(AreaDto areaDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 查询市
	 * 
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> queryCityByProvince(AreaDto areaDto) throws Exception;

	/**
	 * 
	 * @number 3 @description 查询区县
	 * 
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> queryTownByCity(AreaDto areaDto) throws Exception;
}

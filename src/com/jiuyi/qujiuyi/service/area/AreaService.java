package com.jiuyi.qujiuyi.service.area;

import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.area.AreaDto;
import com.jiuyi.qujiuyi.dto.area.CityDto;
import com.jiuyi.qujiuyi.dto.area.TownDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 区域业务层接口
 * @author zhb
 * @createTime 2015年9月6日
 */
public interface AreaService {
	/**
	 * @description 获取城市列表
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getCityList(CityDto cityDto) throws Exception;

	/**
	 * @description 获取省份列表
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getProvinceList(BaseDto baseDto) throws Exception;

	/**
	 * @description 获取乡镇列表
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getTownList(TownDto townDto) throws Exception;

	/**
	 * 
	 * @number @description 查询级联省列表
	 * 
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public ResponseDto queryProvinceCascade(AreaDto areaDto) throws Exception;

	/**
	 * 
	 * @number @description 查询级联市列表
	 * 
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public ResponseDto queryCityByProvinceCascade(AreaDto areaDto) throws Exception;

	/**
	 * 
	 * @number @description 查询级联区县列表
	 * 
	 * @param adreDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public ResponseDto queryTownByCityCascade(AreaDto areaDto) throws Exception;
}
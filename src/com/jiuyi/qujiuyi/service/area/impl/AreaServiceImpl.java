package com.jiuyi.qujiuyi.service.area.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.area.AreaDao;
import com.jiuyi.qujiuyi.daoyao.address.AddressDao;
import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.area.AreaDto;
import com.jiuyi.qujiuyi.dto.area.CityDto;
import com.jiuyi.qujiuyi.dto.area.ProvinceDto;
import com.jiuyi.qujiuyi.dto.area.TownDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.area.AreaService;

/**
 * @description 区域业务层实现
 * @author zhb
 * @createTime 2015年9月6日
 */
@Service
public class AreaServiceImpl implements AreaService {
	@Autowired
	private AreaDao areaDao;

	@Autowired
	private AddressDao addressDao;
	/** 省份缓存. */
	public static List<AreaDto> provinces = new ArrayList<AreaDto>();

	/** 省-市缓存. */
	public static Map<Integer, List<AreaDto>> no_city = new ConcurrentHashMap<Integer, List<AreaDto>>();

	/** 市-区县缓存. */
	public static Map<Integer, List<AreaDto>> no_town = new ConcurrentHashMap<Integer, List<AreaDto>>();

	/**
	 * @description 获取城市列表
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getCityList(CityDto cityDto) throws Exception {
		if (cityDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (cityDto.getProvinceId() == null) {
			throw new BusinessException("省份id不能为空");
		}

		List<CityDto> list = areaDao.getCityList(cityDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 获取省份列表
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getProvinceList(BaseDto baseDto) throws Exception {
		List<ProvinceDto> list = areaDao.getProvinceList();
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 获取乡镇列表
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getTownList(TownDto townDto) throws Exception {
		if (townDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (townDto.getCityId() == null) {
			throw new BusinessException("城市id不能为空");
		}

		List<TownDto> list = areaDao.getTownList(townDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

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
	@Override
	public ResponseDto queryProvinceCascade(AreaDto areaDto) throws Exception {
		List<AreaDto> areas = getProvince();
		ResponseDto responseDto = new ResponseDto();
		Map<String, List<AreaDto>> map = new HashMap<String, List<AreaDto>>();
		map.put("list", areas);
		responseDto.setDetail(map);
		responseDto.setResultDesc("省份列表");
		return responseDto;
	}

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
	@Override
	public ResponseDto queryCityByProvinceCascade(AreaDto areaDto) throws Exception {
		if (areaDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (areaDto.getNo() == null) {
			throw new BusinessException("请填写省份编号");
		}
		List<AreaDto> areas = getCity(areaDto.getNo(), areaDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, List<AreaDto>> map = new HashMap<String, List<AreaDto>>();
		map.put("list", areas);
		responseDto.setDetail(map);
		responseDto.setResultDesc("城市列表");
		return responseDto;
	}

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
	@Override
	public ResponseDto queryTownByCityCascade(AreaDto areaDto) throws Exception {
		if (areaDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (areaDto.getNo() == null) {
			throw new BusinessException("请填写城市编号");
		}
		List<AreaDto> areas = getTown(areaDto.getNo(), areaDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, List<AreaDto>> map = new HashMap<String, List<AreaDto>>();
		map.put("list", areas);
		responseDto.setDetail(map);
		responseDto.setResultDesc("区县列表");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 获得省
	 * 
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> getProvince() throws Exception {
		if (provinces.isEmpty()) {
			AreaDto areaDto = new AreaDto();
			provinces = addressDao.queryProvice(areaDto);
		}
		return provinces;
	}

	/**
	 * 
	 * @number @description 获得市
	 * 
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> getCity(Integer no, AreaDto areaDto) throws Exception {
		if (!no_city.containsKey(no)) {
			List<AreaDto> areas = addressDao.queryCityByProvince(areaDto);
			no_city.put(no, areas);
			return areas;
		}
		return no_city.get(no);
	}

	/**
	 * 
	 * @number @description 查询区县
	 * 
	 * @param no
	 * @param areaDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月23日
	 */
	public List<AreaDto> getTown(Integer no, AreaDto areaDto) throws Exception {
		if (!no_town.containsKey(no)) {
			List<AreaDto> areas = addressDao.queryTownByCity(areaDto);
			no_town.put(no, areas);
			return areas;
		}
		return no_town.get(no);
	}
}
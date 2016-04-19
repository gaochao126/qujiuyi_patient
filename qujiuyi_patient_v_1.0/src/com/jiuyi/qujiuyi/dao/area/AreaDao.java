package com.jiuyi.qujiuyi.dao.area;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.jiuyi.qujiuyi.dto.area.CityDto;
import com.jiuyi.qujiuyi.dto.area.ProvinceDto;
import com.jiuyi.qujiuyi.dto.area.TownDto;

public interface AreaDao {
    /**
     * @description 获取城市列表
     * @return
     * @throws Exception
     */
    public List<CityDto> getCityList(CityDto cityDto) throws Exception;

    /**
     * @description 获取省份列表
     * @return
     * @throws Exception
     */
    @Select("select provinceId, provinceName from t_province order by displayOrder")
    public List<ProvinceDto> getProvinceList() throws Exception;

    /**
     * @description 获取乡镇列表
     * @return
     * @throws Exception
     */
    public List<TownDto> getTownList(TownDto townDto) throws Exception;
}
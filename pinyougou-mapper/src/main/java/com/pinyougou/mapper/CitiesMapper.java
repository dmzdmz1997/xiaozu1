package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Cities;

import java.util.List;

/**
 * CitiesMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface CitiesMapper extends Mapper<Cities>{

    @Select("select cityid as cityId,city from tb_cities where provinceid = #{provinceId}")
    List<Cities> findCityByProvinceId(String provinceId);
    Cities findOne(String ctiyId);


    List<Cities> findAllCities();

    List<Cities> findCitiesByProvinceId(String parentId);
}
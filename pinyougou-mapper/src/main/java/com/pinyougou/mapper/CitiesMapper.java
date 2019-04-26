package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Cities;

import java.util.List;

/**
 * CitiesMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface CitiesMapper extends Mapper<Cities>{

    Cities findOne(String ctiyId);


    List<Cities> findAllCities();

    List<Cities> findCitiesByProvinceId(String parentId);
}
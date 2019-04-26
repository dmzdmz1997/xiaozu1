package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Provinces;

import java.util.List;

/**
 * ProvincesMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface ProvincesMapper extends Mapper<Provinces>{

    Provinces findOne(String provinceId);

    /** 查询全部 */
    List<Provinces> findAll();

    List<Provinces> findAllProvinces();

}
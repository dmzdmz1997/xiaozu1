package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Address;

import java.util.List;

/**
 * AddressMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface AddressMapper extends Mapper<Address>{

    List<Address> findAll();



}
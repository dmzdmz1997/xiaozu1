package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Areas;

import java.util.List;

/**
 * AreasMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface AreasMapper extends Mapper<Areas>{

    Areas findOne(String areaid);

    /** 查询全部 */
    List<Areas> findAll();

    List<Areas> findAllAreas();
    @Select("SELECT * FROM tb_areas WHERE cityid =#{cityId} ")
    List<Areas> findAreasByCityId(String parentId);
}
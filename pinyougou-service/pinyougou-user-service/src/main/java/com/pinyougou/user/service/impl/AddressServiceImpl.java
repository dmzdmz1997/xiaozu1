package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * 地址服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-20<p>
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private AreasMapper areasMapper;

    @Override
    public void save(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void update(Address address) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        List<Address> addressList = addressMapper.selectAll();
        for (Address address : addressList) {
            /** 把cities封装到address */
            String cityId = address.getCityId();
            Cities cities = citiesMapper.findOne(cityId);
            address.setCities(cities);
            /** 把provinces封装到address */
            String provinceId = address.getProvinceId();
            Provinces provinces = provincesMapper.findOne(provinceId);
            address.setProvinces(provinces);
            /** 把area封装到address */
            String townId = address.getTownId();
            Areas areas = areasMapper.findOne(townId);
            address.setAreas(areas);
        }
        return addressList;
    }


    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    @Override
    public List<Address> findAddressByUser(String userId) {
        try{
            // SELECT * FROM tb_address WHERE user_id = 'itcast' ORDER BY is_default DESC
            // 创建示范对象
            Example example = new Example(Address.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = 'itcast'
            criteria.andEqualTo("userId", userId);
            // ORDER BY is_default DESC
            example.orderBy("isDefault").desc();
            // 条件查询
            return addressMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}

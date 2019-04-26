package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.AreasService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SetAddressController {
    @Reference(timeout = 10000)
    private AddressService addressService;
    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @Reference(timeout = 10000)
    private CitiesService citiesService;
    @Reference(timeout = 10000)
    private AreasService areasService;


    /**  获取登录用户名 */
    @GetMapping("/address/showUserName")
    public Map<String,String> showUserName(){
        //获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        String loginName = context.getAuthentication().getName();

        Map<String,String>data = new HashMap<>();
        data.put("loginName",loginName);
        return data;
    }

    /** 获取用户地址 */
    @GetMapping("/address/showUserAddress")
    public  Map<String,List> showUserAddress(){
        List<Address> addressList = addressService.findAll();
        Map<String,List>data = new HashMap<>();
        data.put("addressList",addressList);
        return data;
    }

    /** 获取所有地址 */
    @GetMapping("/address/findAllAddress")
    public Map<String,List>findAllAddress(){
        Map<String,List> data = new HashMap<>();
        /** 查询所有的省 */
        List<Provinces> provincesList = provincesService.findAllProvinces();
        data.put("provincesList",provincesList);
//        /** 查询所有的市 */
//        List<Cities> citiesList = citiesService.findAllCities();
//        data.put("citiesList",citiesList);
//        /** 查询所有的区 */
//        List<Areas> areasList = areasService.findAllAreas();
//        data.put("areasList",areasList);
        return data;
    }
    /** 通过省份id查询市 */
    @GetMapping("/address/findCitiesByProvinceId")
    public Map<String,List>findCitiesByProvinceId(String parentId){
        List<Cities> citiesList = citiesService.findCitiesByProvinceId(parentId);
        Map<String,List> data = new HashMap<>();
        data.put("citiesList",citiesList);
        return data;
    }
    /** 通过省份id查询市 */
    @GetMapping("/address/findAreasByCityId")
    public Map<String,List>findAreasByCityId(String parentId){
        List<Areas> areasList = areasService.findAreasByCityId(parentId);
        Map<String,List> data = new HashMap<>();
        data.put("areasList",areasList);
        return data;
    }

    /** 添加地址 */
    @PostMapping("/address/addAddress")
    public boolean addAddress(@RequestBody Address address){
        try {
            //获取安全上下文对象
            SecurityContext context = SecurityContextHolder.getContext();
            String loginName = context.getAuthentication().getName();
            address.setUserId(loginName);
            addressService.save(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

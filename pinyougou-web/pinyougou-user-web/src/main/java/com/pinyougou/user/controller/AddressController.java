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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class AddressController {

    @Reference(timeout = 10000)
    private AddressService addressService;

    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request){
        // 获取登录用户名
        String userId = request.getRemoteUser();
        return addressService.findAddressByUser(userId);
    }

    @PostMapping("/saveAddress")
    public boolean save(@RequestBody Address address){
        try {
            addressService.save(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @PostMapping("/updateAddress")
    public boolean update(@RequestBody Address address){
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @PostMapping("/delAddress")
    public boolean delAddress(@RequestBody Address address){
        try {
            addressService.delete(address.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
   /* @PostMapping("/setAddressDefault")
    public boolean setAddressDefault(@RequestBody Integer id){
        try {
            addressService.setAllZero();
            addressService.setAddressDefault(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/


    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @GetMapping("/findProvinces")
    public List<Provinces> findProvinces(){
        return provincesService.findAll();
    }
    @Reference(timeout = 10000)
    private CitiesService citiesService;
   /* @GetMapping("/findCities")
    public List<Cities> findCities(Integer provinceId){
        return citiesService.findById(provinceId);
    }
*/
  /*  @Reference(timeout = 10000)
    private AreasService areasService;
    @GetMapping("/findAreas")
    public List<Areas> findAreas(Integer cityId){
        return areasService.findById(cityId);

    }*/
}

package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-01<p>
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 商家申请入驻 */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try{
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //查询商家详情
    @GetMapping("/findSeller")
    public List<Seller> findSeller(){
        // 获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取用户名
        String sellerId = context.getAuthentication().getName();
        return sellerService.findSeller(sellerId);
    }

    //修改商家信息
    @PostMapping("updateSeller")
    public boolean updateSeller(@RequestBody Seller seller){
        // 获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取用户名
        String sellerId = context.getAuthentication().getName();
        try {
            sellerService.update(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @PostMapping("/findpassword")
    public boolean findpassword(String oldPassWord){
        // 获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取用户名
        String sellerId = context.getAuthentication().getName();
        try {
            String password=sellerService.findPassword(sellerId);
            boolean n = passwordEncoder.matches(oldPassWord, password);
            return n;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/update")
    public boolean update(String newpassword){
        System.out.println("+++++++++"+newpassword);
        // 获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取用户名
        String sellerId = context.getAuthentication().getName();
        try {
            String encode = passwordEncoder.encode(newpassword);
            System.out.println("++++++++++"+encode);
            sellerService.updatePassword(sellerId,encode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

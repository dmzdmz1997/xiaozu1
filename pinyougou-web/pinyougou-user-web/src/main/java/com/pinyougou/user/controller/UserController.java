package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.UserService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-15<p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;
    @Reference(timeout = 10000)
    private OrderService orderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;
    @Autowired
    private HttpServletRequest request;
    /** 用户注册 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String code){
        try{
            // 检验验证码是否正确
            boolean flag = userService.checkSmsCode(user.getPhone(), code);
            if (flag) {
                userService.save(user);
            }
            return flag;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    /** 发送短信验证码 */
    @GetMapping("/sendSmsCode")
    public boolean sendSmsCode(String phone){
        try{
            return userService.sendSmsCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /**查询省*/
    @GetMapping("/findProvince")
    public List<Provinces> findProvince() {
        return userService.findProvince();
    }
    /**通过provinceId查询市*/
    @GetMapping("/findCityByProvinceId")
    public List<Cities> findCityByProvinceId(String provinceId) {
        return userService.findCityByProvinceId(provinceId);
    }
    /**通过cityId查询所有的区*/
    @GetMapping("/findAreaByCityId")
    public List<Areas> findAreaByCityId(String cityId) {

        return userService.findAreaByCityId(cityId);
    }
    //查询个人信息
    @GetMapping("/findInfo")
    public User findInfo(HttpServletRequest request) {
        String userId = request.getRemoteUser();
        return userService.findInfoByUserId(userId);
    }
    //保存用户信息
    @PostMapping("/saveUserInfo")
    public boolean saveUserInfo(@RequestBody User user,HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            user.setUsername(username);
            userService.saveUserInfo(user);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据登录的用户查找用户所有订单
     */
    @PostMapping("/findOrdersByUserId")
    @ResponseBody
    public List<Map<String, Object>> findOrdersByUserId(@RequestBody Map<String,Object> searchParams) {
        String userId = request.getRemoteUser();
        return userService.findOrdersByUserId(userId, (Integer) searchParams.get("page"), (Integer) searchParams.get("rows"));
    }

    /**
     * 生成微信支付
     */
    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode(String orderId) {
        Long totalFee = userService.findOrderTotalFee(orderId);
        return weixinPayService.genPayCode(String.valueOf(orderId), totalFee + "");

    }
    /**
     * 检测支付状态
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo) {
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            // 调用微信支付服务接口
            Map<String, String> resMap = weixinPayService.queryPayStatus(outTradeNo);

            if (resMap != null) {
                // SUCCESS-支付成功
                if ("SUCCESS".equals(resMap.get("trade_state"))) {
                    System.out.println("进来了");
                    data.put("status", 1);
                    // 支付成功，修改支付日志的状态、订单的状态3
                    orderService.updatePayStatus(outTradeNo, resMap.get("transaction_id"));

                }
                // NOTPAY—未支付
                if ("NOTPAY".equals(resMap.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}

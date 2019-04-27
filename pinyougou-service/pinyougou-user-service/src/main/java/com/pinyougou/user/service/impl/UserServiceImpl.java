package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-15<p>
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private AreasMapper areasMapper;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Override
    public void save(User user) {
        try{
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            // 创建时间
            user.setCreated(new Date());
            // 修改时间
            user.setUpdated(user.getCreated());
            // 添加数据
            userMapper.insertSelective(user);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /** 发送短信验证码 */
    public boolean sendSmsCode(String phone){
        try{
            // 1. 随机生成6位数字的验证码 95db9eb9-94e8-48e7-a5b2-97c622644e70
            String code = UUID.randomUUID().toString().replaceAll("-", "")
                    .replaceAll("[a-zA-Z]", "").substring(0,6);
            System.out.println("code= " + code);


            // 2. 调用短信发送接口(HttpClientUtils)
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            // 定义Map集合封装请求参数 18502903967
            Map<String, String> params = new HashMap<>();
            params.put("phone", phone);
            params.put("signName", signName);
            params.put("templateCode", templateCode);
            params.put("templateParam", "{'number' : '"+ code +"'}");
            // 发送post请求
            String content = httpClientUtils.sendPost(smsUrl, params);
            System.out.println("content = " + content);

            // 3. 判断短信是否发送成功，如果发送成功，就需要把验证存储到Redis(时间90秒)
            // {success : true}
            Map map = JSON.parseObject(content, Map.class);
            boolean success = (boolean)map.get("success");
            if (success){
                // 把验证存储到Redis(时间90秒)
                redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            }

            return success;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 检验验证码是否正确 */
    public boolean checkSmsCode(String phone, String code){
        try{
            String oldCode = (String)redisTemplate.boundValueOps(phone).get();
            return code.equals(oldCode);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /**查询所有省*/
    @Override
    public List<Provinces> findProvince() {
        return provincesMapper.findProvincesByProvinceId();
    }
    @Override
    public List<Cities> findCityByProvinceId(String provinceId) {
        try {
            return citiesMapper.findCityByProvinceId(provinceId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Areas> findAreaByCityId(String cityId) {
        try {
            return areasMapper.findAreaByCityId(cityId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public User findInfoByUserId(String username) {
        try {
            return userMapper.findByUserId(username);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void saveUserInfo(User user) {
        try {
            userMapper.updateByUsername(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据登录的用户查找用户所有订单
     */
    @Override
    public List<Map<String, Object>> findOrdersByUserId(String userId, Integer page, Integer rows) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        PageInfo<Object> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                //查询order表数据
                List<Order> orderList = orderMapper.selectByUserId(userId);
                for (Order order : orderList) {
                    /**查询订单日志表单号，商家店铺名称*/
                    String outTradeNo = payLogMapper.selectByOrderId(order.getOrderId());
                    String SellerName = sellerMapper.selectBysellerId(order.getSellerId());
                    order.setOutTradeNo(outTradeNo);
                    order.setSellerName(SellerName);
                    //从orderItem表中查询关联order表的数据，
                    List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
                    for (OrderItem orderItem : orderItems) {
                        //从item表中查询规格数据
                        String spec = itemMapper.selectSpecById(orderItem.getItemId());
                        orderItem.setSpec(spec);
                    }
                    order.setOrderItems(orderItems);
                }
            }
        });
        Map<String, Object> pages = new HashMap<>();
        pages.put("totalPages", pageInfo.getPages());
        Map<String, Object> orders = new HashMap<>();
        orders.put("orders", pageInfo.getList());
        resultMap.add(pages);
        resultMap.add(orders);
        return resultMap;
    }

    /**
     * 根据订单号查找订单金额
     */
    @Override
    public Long findOrderTotalFee(String orderId) {
        PayLog payLog=new PayLog();
        payLog.setOutTradeNo(orderId);
        List<PayLog> payLogList = payLogMapper.select(payLog);
        return payLogList.get(0).getTotalFee();
    }

}

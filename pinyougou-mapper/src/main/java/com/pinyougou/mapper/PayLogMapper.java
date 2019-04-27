package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.PayLog;

/**
 * PayLogMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface PayLogMapper extends Mapper<PayLog>{

    /**查询订单日志表单号，商家店铺名称*/
    @Select("select out_trade_no from tb_pay_log where order_list=#{orderId} ")
    String selectByOrderId(Long orderId);


}
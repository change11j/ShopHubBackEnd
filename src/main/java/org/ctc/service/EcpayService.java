package org.ctc.service;

import org.ctc.dto.ecpay.EcPayReq;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.ctc.costant.Constance.ORDER_EXPIRED_TIME;

@Service
public class EcpayService {

    private RedisTemplate<String, Object> objectRedisTemplate;

    public EcpayService(RedisTemplate<String, Object> objectRedisTemplate){
        this.objectRedisTemplate=objectRedisTemplate;
    }

    public void storeOrder(String merchantTradeNo, EcPayReq ecPayReq) {
        objectRedisTemplate.opsForValue().set(merchantTradeNo, ecPayReq,ORDER_EXPIRED_TIME, TimeUnit.MINUTES);
    }


    public EcPayReq retrieveOrder(String merchantTradeNo) {
        return (EcPayReq) objectRedisTemplate.opsForValue().get(merchantTradeNo);
    }





}

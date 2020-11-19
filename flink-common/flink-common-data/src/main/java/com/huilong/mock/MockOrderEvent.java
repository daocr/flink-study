package com.huilong.mock;

import lombok.Data;

import java.util.Date;

/**
 * @author daocr
 * @date 2020/11/18
 */
@Data
public class MockOrderEvent {


    private Long eventId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 金额
     */
    private Integer amount;
}

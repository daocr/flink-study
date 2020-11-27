package com.huilong.mock.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daocr
 * @date 2020/11/27
 */
@Data
public class StockTransaction implements Serializable {

    /**
     * 交易时间
     */
    private String transactionTime;

    /**
     * 交易价格
     */
    private Double transactionPrice;

    /**
     * 价格浮动
     */
    private Double priceChange;

    /**
     * 交易数量
     */
    private Long transactionCut;

    /**
     * 成交金额
     */
    private Long transactionAmount;

    /**
     * 交易类型
     */
    private String transactionType;


}

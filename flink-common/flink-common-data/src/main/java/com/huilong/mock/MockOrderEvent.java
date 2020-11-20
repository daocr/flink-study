package com.huilong.mock;

import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

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

    @Override
    public String toString() {

        String format = null;

        if (this.payTime != null) {
            format = DateFormatUtils.format(this.payTime, "yyyy-MM-dd HH:mm:ss");
        }

        final StringBuffer sb = new StringBuffer("MockOrderEvent{");
        sb.append("eventId=").append(eventId);
        sb.append(", userId=").append(userId);
        sb.append(", payTime=").append(format);
        sb.append(", goodName='").append(goodName).append('\'');
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}

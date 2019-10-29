package com.lala.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author lala
 * kafka消息结构体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaMessage {
    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private Long houseId;
    private String operation;
    private int retry = 0;
}

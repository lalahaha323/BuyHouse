package com.lala.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.lala.config.EsUtil;
import com.lala.elasticsearch.*;
import com.lala.entity.House;
import com.lala.entity.HouseDetail;
import com.lala.entity.HouseTag;
import com.lala.kafka.KafkaMessage;
import com.lala.mapper.HouseDetailMapper;
import com.lala.mapper.HouseMapper;
import com.lala.mapper.HouseTagMapper;
import com.lala.service.SearchService;
import com.lala.service.result.ServiceResult;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private static final String INDEX_NAME = "house";
    private static final String INDEX_TYPE = "_doc";
    private static final String INDEX_TOPIC = "houseBuild";

    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseDetailMapper houseDetailMapper;
    @Autowired
    HouseTagMapper houseTagMapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    /** 用户在上架的时候会执行到这里，因为是第一次，所以retry = 0 **/
    @Override
    public void index(long houseId) {
        /** 消息会异步进入kafka中进行消费 **/
        this.index(houseId, 0);
    }

    /** 消息入Kafka队列中，完成增加索引 **/
    private void index(long houseId, int retry) {
        if (retry > KafkaMessage.MAX_RETRY) {
            logger.error("Retry Index times over 3 for house: " + houseId, " Please check it!");
            return;
        }

        KafkaMessage kafkaMessage = new KafkaMessage(houseId, KafkaMessage.INDEX, retry);
        try {
            /** 当kafka将消息发送出去的时候，监听器监听到的话就会消费 **/
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(kafkaMessage));
        } catch (JsonProcessingException e) {
            logger.error("Json encode error for " + kafkaMessage);
        }
    }

    /** Kafka监听处理 **/
    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            KafkaMessage kafkaMessage = objectMapper.readValue(content, KafkaMessage.class);
            switch(kafkaMessage.getOperation()) {
                case KafkaMessage.INDEX:
                    this.createOrUpdateIndex(kafkaMessage);
                    break;
                case KafkaMessage.REMOVE:
                    this.reamoveIndex(kafkaMessage);
                    break;
                default:
                    logger.warn("Not support message content " + content);
                    break;
            }
        } catch (JsonProcessingException e) {
            logger.error("Cannot parse json for " + content, e);
        }
    }

    /** 对ｋａｆｋａ的消息做创建或者更新索引 **/
    private void createOrUpdateIndex(KafkaMessage kafkaMessage) {

        Long houseId = kafkaMessage.getHouseId();
        RestHighLevelClient client = EsUtil.create();

        House house = houseMapper.findOneById(houseId);
        if (house == null) {
            logger.error("索引house不存在！", houseId);
            /** 将消息重新入队列 **/
            this.index(houseId, kafkaMessage.getRetry() + 1);
            return;
        }

        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

        HouseDetail houseDetail = houseDetailMapper.findOneById(houseId);
        if (houseDetail == null) {
            //异常情况
        }
        modelMapper.map(houseDetail, houseIndexTemplate);

        List<HouseTag> houseTagList = houseTagMapper.findAllById(houseId);
        if(houseTagList != null && !houseTagList.isEmpty()) {
            List<String> houseTagStringList = new ArrayList<>();
            for (HouseTag houseTag : houseTagList) {
                houseTagStringList.add(houseTag.getName());
            }
            houseIndexTemplate.setTags(houseTagStringList);
        }

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("houseId", houseIndexTemplate.getHouseId());
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        boolean success = false;
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            long totalHit = searchResponse.getHits().getTotalHits();
            if(totalHit == 0) {
                success = create(houseIndexTemplate);
            } else if(totalHit == 1){
                String esId = searchResponse.getHits().getAt(0).getId();
                success = update(esId, houseIndexTemplate);
            } else {
                System.out.println("有多个");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(success) {
            logger.debug("更新索引成功，houseId为：" + houseId);
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 对ｋａｆｋａ的消息做删除索引 **/
    private void reamoveIndex(KafkaMessage kafkaMessage) {

        long houseId = kafkaMessage.getHouseId();
        RestHighLevelClient client = EsUtil.create();
        DeleteByQueryRequest request = new DeleteByQueryRequest(
                INDEX_NAME
        );
        request.setQuery(QueryBuilders.termsQuery("houseId", String.valueOf(houseId)));
        try {
            BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
            long deleted = bulkByScrollResponse.getDeleted();
            if (deleted <= 0) {
                this.remove(houseId, kafkaMessage.getRetry() + 1);
            } else {
                System.out.println("Delete total " + deleted);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** 创建索引 **/
    private boolean create(HouseIndexTemplate houseIndexTemplate) {
        RestHighLevelClient client = EsUtil.create();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("title", houseIndexTemplate.getTitle());
        jsonMap.put("price", houseIndexTemplate.getPrice());
        jsonMap.put("area", houseIndexTemplate.getArea());
        jsonMap.put("createTime", houseIndexTemplate.getCreateTime());
        jsonMap.put("lastUpdateTime", houseIndexTemplate.getLastUpdateTime());
        jsonMap.put("cityEnName", houseIndexTemplate.getCityEnName());
        jsonMap.put("regionEnName", houseIndexTemplate.getRegionEnName());
        jsonMap.put("direction", houseIndexTemplate.getDirection());
        jsonMap.put("distanceToSubWay", houseIndexTemplate.getDistanceToSubWay());
        jsonMap.put("subWayLineName", houseIndexTemplate.getSubwayLineName());
        jsonMap.put("subwayStationName", houseIndexTemplate.getSubwayStationName());
        jsonMap.put("street", houseIndexTemplate.getStreet());
        jsonMap.put("district", houseIndexTemplate.getDistrict());
        jsonMap.put("description", houseIndexTemplate.getDescription());
        jsonMap.put("layoutDesc", houseIndexTemplate.getLayoutDesc());
        jsonMap.put("traffic", houseIndexTemplate.getTraffic());
        jsonMap.put("roundService", houseIndexTemplate.getRoundService());
        jsonMap.put("rentWay", houseIndexTemplate.getRentWay());
        jsonMap.put("tags", houseIndexTemplate.getTags());
        jsonMap.put("houseId", houseIndexTemplate.getHouseId());

        IndexRequest request = new IndexRequest(
                INDEX_NAME,
                INDEX_TYPE
        ).source(jsonMap);

        try {
            client.index(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 更新索引 **/
    private boolean update(String esId, HouseIndexTemplate houseIndexTemplate) {
        RestHighLevelClient client = EsUtil.create();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("title", houseIndexTemplate.getTitle());
        jsonMap.put("price", houseIndexTemplate.getPrice());
        jsonMap.put("area", houseIndexTemplate.getArea());
        jsonMap.put("createTime", houseIndexTemplate.getCreateTime());
        jsonMap.put("lastUpdateTime", houseIndexTemplate.getLastUpdateTime());
        jsonMap.put("cityEnName", houseIndexTemplate.getCityEnName());
        jsonMap.put("regionEnName", houseIndexTemplate.getRegionEnName());
        jsonMap.put("direction", houseIndexTemplate.getDirection());
        jsonMap.put("distanceToSubWay", houseIndexTemplate.getDistanceToSubWay());
        jsonMap.put("subWayLineName", houseIndexTemplate.getSubwayLineName());
        jsonMap.put("subwayStationName", houseIndexTemplate.getSubwayStationName());
        jsonMap.put("street", houseIndexTemplate.getStreet());
        jsonMap.put("district", houseIndexTemplate.getDistrict());
        jsonMap.put("description", houseIndexTemplate.getDescription());
        jsonMap.put("layoutDesc", houseIndexTemplate.getLayoutDesc());
        jsonMap.put("traffic", houseIndexTemplate.getTraffic());
        jsonMap.put("roundService", houseIndexTemplate.getRoundService());
        jsonMap.put("rentWay", houseIndexTemplate.getRentWay());
        jsonMap.put("tags", houseIndexTemplate.getTags());
        jsonMap.put("houseId", houseIndexTemplate.getHouseId());

        UpdateRequest updateRequest = new UpdateRequest(
                INDEX_NAME,
                INDEX_TYPE,
                esId
        ).doc(jsonMap);
        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
            logger.debug("更新索引成功，索引id为 " + esId);
            return true;
        } catch (IOException e) {
            logger.error("更新索引失败，索引id为" + esId, e);
            return false;
        }
    }

    /** 用户在下架，出租的时候会执行到这里，因为是第一次，所以retry = 0 **/
    @Override
    public void remove(long houseId) {
        this.remove(houseId, 0);
    }

    /** 消息队列入Kafka中，完成删除 **/
    private void remove(long houseId, int retry) {
        if (retry > KafkaMessage.MAX_RETRY) {
            logger.error("Retry Index times over 3 for house: " + houseId, " Please check it!");
            return;
        }

        KafkaMessage kafkaMessage = new KafkaMessage(houseId, KafkaMessage.REMOVE, retry);
        try {
            /** 当kafka将消息发送出去的时候，监听器监听到的话就会消费 **/
            this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(kafkaMessage));
        } catch (JsonProcessingException e) {
            logger.error("Json encode error for " + kafkaMessage);
        }
    }

    /** ES查询 **/
    @Override
    public List<Long> esQuery(RentSearch rentSearch) {

        RestHighLevelClient client = EsUtil.create();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        /** 加入筛选条件 **/
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName()));
        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName()));
        }

        /** 关键词搜索，设置可以搜索的条件，标题等 **/
        if (rentSearch.getKeywords() != null && !rentSearch.getKeywords().isEmpty()) {
            boolQueryBuilder.must(
                    QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                            HouseIndexKey.TITLE,
                            HouseIndexKey.TRAFFIC,
                            HouseIndexKey.DISTRICT,
                            HouseIndexKey.ROUND_SERVICE,
                            HouseIndexKey.SUBWAY_LINE_NAME,
                            HouseIndexKey.SUBWAY_STATION_NAME,
                            HouseIndexKey.STREET
                    ));
        }

        /** 面积区间 **/
        if (rentSearch.getAreaBlock() != null && !"*".equals(rentSearch.getAreaBlock())) {
            /** 根据设置的面积区间范围去匹配一个 **/
            RentValueBlock areaValueBlock = RentValueBlock.matchArea(rentSearch.getAreaBlock());
            RangeQueryBuilder areaRangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
            if (areaValueBlock.getMin() > 0) {
                /** 大于最小值 **/
                areaRangeQueryBuilder.gte(areaValueBlock.getMin());
            } else {
                areaRangeQueryBuilder.gte(0);
            }
            if (areaValueBlock.getMax() > 0) {
                /** 小于最大值 **/
                areaRangeQueryBuilder.lte(areaValueBlock.getMax());
            }
            boolQueryBuilder.filter(areaRangeQueryBuilder);
        }

        /** 价格区间 **/
        if (rentSearch.getPriceBlock() != null && !"*".equals(rentSearch.getPriceBlock())) {
            /** 根据设置的价格区间范围去匹配一个 **/
            RentValueBlock priseValueBlock = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
            RangeQueryBuilder priseRangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
            if (priseValueBlock.getMin() > 0) {
                priseRangeQueryBuilder.gte(priseValueBlock.getMin());
            } else {
                priseRangeQueryBuilder.gte(0);
            }
            if (priseValueBlock.getMax() > 0) {
                priseRangeQueryBuilder.lte(priseValueBlock.getMax());
            }
            boolQueryBuilder.filter(priseRangeQueryBuilder);
        }

        /** 朝向 **/
        if (rentSearch.getDirection() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection()));
        }

        /** 租赁方式 **/
        if (rentSearch.getRentWay() > -1) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay()));
        }

        /** 卧室数量 **/
        //卧室数量
        if (rentSearch.getRoom() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.ROOM, rentSearch.getRoom()));
        }

        /** 查询请求 **/
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(rentSearch.getStart());        /** 从多少条开始 **/
        searchSourceBuilder.size(rentSearch.getSize());        /** 每页数量 **/
        //将构造器设置到查询请求中
        searchRequest.source(searchSourceBuilder);
        logger.info("the search condition is {}", searchSourceBuilder);
        try {
            List<Long> houseIds = Lists.newArrayList();
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() != RestStatus.OK) {
                logger.info("Search status is no ok  ");
                return houseIds;
            }
            /** 查询到的结果集 **/
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit item : hits) {
                houseIds.add(Longs.tryParse(item.getSourceAsMap().get(HouseIndexKey.HOUSE_ID).toString()));
            }
            return houseIds;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

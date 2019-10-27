package com.lala.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lala.config.EsUtil;
import com.lala.elasticsearch.HouseIndexTemplate;
import com.lala.entity.House;
import com.lala.entity.HouseDetail;
import com.lala.entity.HouseTag;
import com.lala.mapper.HouseDetailMapper;
import com.lala.mapper.HouseMapper;
import com.lala.mapper.HouseTagMapper;
import com.lala.service.SearchService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseDetailMapper houseDetailMapper;
    @Autowired
    HouseTagMapper houseTagMapper;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public void index(long houseId) {
        RestHighLevelClient client = EsUtil.create();
        House house = houseMapper.findOneById(houseId);
        if (house == null) {
            logger.error("索引house不存在！", houseId);
            return ;
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
    /** 删除索引 **/
    private void remove(HouseIndexTemplate houseIndexTemplate) {

        Long houseId = houseIndexTemplate.getHouseId();
        RestHighLevelClient client = EsUtil.create();
        DeleteByQueryRequest request = new DeleteByQueryRequest(
                INDEX_NAME
        );
        request.setQuery(QueryBuilders.termsQuery("houseId", String.valueOf(houseId)));
        try {
            BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
            long deleted = bulkByScrollResponse.getDeleted();
            System.out.println("Delete total " + deleted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(long houseId) {
        RestHighLevelClient client = EsUtil.create();
        DeleteByQueryRequest request = new DeleteByQueryRequest(
                INDEX_NAME
        );
        request.setQuery(QueryBuilders.termsQuery("houseId", String.valueOf(houseId)));
        try {
            BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
            long deleted = bulkByScrollResponse.getDeleted();
            System.out.println("Delete total " + deleted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

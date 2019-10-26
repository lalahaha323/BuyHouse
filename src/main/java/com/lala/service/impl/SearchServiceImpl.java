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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lala
 */
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
        House house = houseMapper.findOneById(houseId);
        if (house == null) {
            logger.error("索引house不存在！", houseId);
            return ;
        }

        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);




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

    }
}

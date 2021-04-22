package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.client.EsClient;
import com.kayn.mapper.user.UserLabelMapper;
import com.kayn.pojo.user.UserLabel;
import com.kayn.service.RecommenderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RecommenderServiceImpl implements RecommenderService {

    @Resource
    UserLabelMapper userLabelMapper;


    @Override
    public String getPreferTel(String username) {
        Map<String, Object> map = EsClient.getData("prefer_tel", username);
        String res = null;
        if (map != null) {
            List<Map<String, Object>> preferTelList = (List<Map<String, Object>>) map.get("preferTel");
            int maxCnt = 0;
            for (Map<String, Object> tel : preferTelList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("tel").toString();
                }
            }
            userLabelMapper.update(new UserLabel().setPhone(res), new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public String getPreferAddress(String username) {
        Map<String, Object> map = EsClient.getData("prefer_address", username);
        String res = null;
        if (map != null) {
            List<Map<String, Object>> preferAddressList = (List<Map<String, Object>>) map.get("preferAddress");
            int maxCnt = 0;
            for (Map<String, Object> tel : preferAddressList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("address").toString();
                }
            }
            System.out.println(res);
            userLabelMapper.update(new UserLabel().setAddress(res), new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public String getPreferCat(String username) {
        Map<String, Object> map = EsClient.getData("prefer_cat", username);
        String res = null;
        if (map != null) {
            List<Map<String, Object>> preferCatList = (List<Map<String, Object>>) map.get("preferCat");
            int maxCnt = 0;
            for (Map<String, Object> tel : preferCatList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("cat").toString();
                }
            }
            userLabelMapper.update(new UserLabel().setCatPrefer(res), new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public Integer getTotalPayCnt(String username) {
        Integer res = null;
        Map<String, Object> map = EsClient.getData("pay_order", username);
        if (map != null) {
            res = Integer.parseInt(map.get("payOrderCnt").toString());
            userLabelMapper.update(new UserLabel().setTotalPayCnt(res), new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public Double getTotalPayMoney(String username) {
        Double res = null;
        Map<String, Object> map = EsClient.getData("pay_order", username);
        if (map != null) {
            res = Double.parseDouble(map.get("totalPrice").toString());
            userLabelMapper.update(new UserLabel().setTotalPayMoney(res), new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public Double getTotalPayAvg(String username) {
        Double res = null;
        Map<String, Object> map = EsClient.getData("pay_order", username);
        if (map != null) {
            Double totalPrice = Double.parseDouble(map.get("totalPrice").toString());
            Integer payCnt = Integer.parseInt(map.get("payOrderCnt").toString());
            res = totalPrice / payCnt;
            UserLabel userLabel = new UserLabel().setTotalPayAvg(res);
            if (res < 1000) {
                userLabel.setPriceRange(0);
            } else if (res >= 1000 && res < 5000) {
                userLabel.setPriceRange(1);
            } else if (res >= 5000 && res < 10000) {
                userLabel.setPriceRange(2);
            } else if (res >= 10000) {
                userLabel.setPriceRange(3);
            }
            userLabelMapper.update(userLabel, new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }

    @Override
    public String getTotalMostQuery(String username) {
        String res = null;
        Map<String, Object> map = EsClient.getData("last_query", username);
        if (map != null) {
            List<Map<String, Object>> preferTelList = (List<Map<String, Object>>) map.get("query");
            int maxCnt = 0;
            for (Map<String, Object> tel : preferTelList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    String query = tel.get("query").toString();
                    if (! "默认".equals(query)) {
                        res = query;
                        maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    }
                }
            }
            UserLabel userLabel = new UserLabel().setTotalMostQuery(res);
            int cnt = Integer.parseInt(map.get("cnt").toString());
            if (cnt < 20) {
                userLabel.setLive("低活跃度");
            } else if (cnt < 50){
                userLabel.setLive("中活跃度");
            } else {
                userLabel.setLive("高活跃度");
            }
            userLabelMapper.update(userLabel, new UpdateWrapper<UserLabel>().eq("username", username));
        }
        return res;
    }
}

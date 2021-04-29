package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.dto.DataSetDto;
import com.kayn.dto.R;
import com.kayn.dto.RecDto;
import com.kayn.dto.RecommenderDto;
import com.kayn.mapper.idf.CatCntMapper;
import com.kayn.mapper.idf.CatIdfMapper;
import com.kayn.mapper.idf.QueryCntMapper;
import com.kayn.mapper.idf.QueryIdfMapper;
import com.kayn.mapper.slide.SlideMapper;
import com.kayn.mapper.user.UserRFMMapper;
import com.kayn.pojo.idf.CatCnt;
import com.kayn.pojo.idf.CatIdf;
import com.kayn.pojo.idf.QueryCnt;
import com.kayn.pojo.idf.QueryIdf;
import com.kayn.pojo.slide.Slide;
import com.kayn.pojo.user.UserRFM;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/recommender")
public class RecommenderController {

    @Resource
    UserRFMMapper userRFMMapper;

    @Resource
    CatIdfMapper catIdfMapper;

    @Resource
    QueryIdfMapper queryIdfMapper;

    @Resource
    SlideMapper slideMapper;

    @Resource
    QueryCntMapper queryCntMapper;

    @Resource
    CatCntMapper catCntMapper;

    @GetMapping("/getRecommenderList")
    public R getRecommenderList(@RequestParam String username) {
        R r = new R();
        try {
            QueryWrapper<UserRFM> queryWrapper = new QueryWrapper<UserRFM>().eq("username", username);
            UserRFM userRFM = userRFMMapper.selectOne(queryWrapper);
            ArrayList<RecDto> recDtoList = new ArrayList<>();
            String un = userRFM.getUsername();
            QueryIdf queryIdf = queryIdfMapper.selectOne(new QueryWrapper<QueryIdf>().eq("username", un));
            CatIdf catIdf = catIdfMapper.selectOne(new QueryWrapper<CatIdf>().eq("username", un));
            RecommenderDto recommenderDto = new RecommenderDto();
            if (queryIdf != null && catIdf != null) {
                RecDto recDto = new RecDto()
                        .setUsername(un)
                        .setPreferQuery(queryIdf.getQuery())
                        .setQueryScore(queryIdf.getScore())
                        .setPreferCat(catIdf.getCat())
                        .setCatScore(catIdf.getScore())
                        .setRfm(userRFM.getRfm());
                ArrayList<RecDto> recDtos = new ArrayList<>();
                recDtos.add(recDto);
                recommenderDto.setRecDto(recDtos);
            }

            List<QueryCnt> queryCntList = queryCntMapper.selectList(new QueryWrapper<QueryCnt>().eq("username", username));
            List<CatCnt> catCntList = catCntMapper.selectList(new QueryWrapper<CatCnt>().eq("username", username));

            // query数据封装
            ArrayList<String> queryLabelList = new ArrayList<>();
            ArrayList<DataSetDto> queryDataSetList = new ArrayList<>();

            DataSetDto<Integer> queryDataSetCnt = new DataSetDto<Integer>().setLabel("频次");
            DataSetDto<Double> queryDataSetScore = new DataSetDto<Double>().setLabel("得分");

            ArrayList<Integer> queryDataCnt = new ArrayList<>();
            ArrayList<Double> queryDataScore = new ArrayList<>();
            for (QueryCnt queryCnt: queryCntList) {
                queryLabelList.add(queryCnt.getQuery());
                queryDataCnt.add(queryCnt.getCnt());
                queryDataScore.add(queryCnt.getScore());
            }
            queryDataSetCnt.setData(queryDataCnt);
            queryDataSetScore.setData(queryDataScore);
            queryDataSetList.add(queryDataSetCnt);
            queryDataSetList.add(queryDataSetScore);
            recommenderDto.setQueryLabelList(queryLabelList).setQueryDataSetList(queryDataSetList);

            // cat数据封装
            ArrayList<String> catLabelList = new ArrayList<>();
            ArrayList<DataSetDto> catDataSetList = new ArrayList<>();

            DataSetDto<Integer> CatDataSetCnt = new DataSetDto<Integer>().setLabel("频次");
            DataSetDto<Double> CatDataSetScore = new DataSetDto<Double>().setLabel("得分");

            ArrayList<Integer> catDataCnt = new ArrayList<>();
            ArrayList<Double> catDataScore = new ArrayList<>();
            for (CatCnt catCnt: catCntList) {
                catLabelList.add(catCnt.getCat());
                catDataCnt.add(catCnt.getCnt());
                catDataScore.add(catCnt.getScore());
            }
            CatDataSetCnt.setData(catDataCnt);
            CatDataSetScore.setData(catDataScore);
            catDataSetList.add(CatDataSetCnt);
            catDataSetList.add(CatDataSetScore);
            recommenderDto.setCatLabelList(catLabelList).setCatDataSetList(catDataSetList);

            r.setCode(200).setData(recommenderDto);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }

    @DeleteMapping("/deleteRecommender")
    public R deleteRecommender(@RequestParam String username) {
        R r = new R();
        try {
            userRFMMapper.delete(new QueryWrapper<UserRFM>().eq("username", username));
            catIdfMapper.delete(new QueryWrapper<CatIdf>().eq("username", username));
            queryIdfMapper.delete(new QueryWrapper<QueryIdf>().eq("username", username));
            r.setCode(200);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }

    @PostMapping("/addOrUpdateSlide")
    public R addOrUpdateSlide(Slide slide) {
        R r = new R();
        try {
            String rfm = slide.getRfm();
            Slide slide1 = slideMapper.selectOne(new QueryWrapper<Slide>().eq("rfm", rfm));
            if (slide1 != null) {
                slideMapper.update(slide, new UpdateWrapper<Slide>().eq("rfm", rfm));
            } else {
                slideMapper.insert(slide);
            }
            r.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }

}

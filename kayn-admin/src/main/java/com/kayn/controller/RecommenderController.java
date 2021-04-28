package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.R;
import com.kayn.dto.RecDto;
import com.kayn.dto.RecommenderDto;
import com.kayn.mapper.idf.CatIdfMapper;
import com.kayn.mapper.idf.QueryIdfMapper;
import com.kayn.mapper.slide.SlideMapper;
import com.kayn.mapper.user.UserRFMMapper;
import com.kayn.pojo.idf.CatIdf;
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

    @GetMapping("/getRecommenderList")
    public R getRecommenderList(@RequestParam(value = "username", required = false) String username,
                         @RequestParam Integer pageIndex,
                         @RequestParam Integer pageSize) {
        R r = new R();
        try {
            QueryWrapper<UserRFM> queryWrapper = new QueryWrapper<>();
            Page<UserRFM> page = new Page<>(pageIndex, pageSize);
            if (!username.equals("")) {
                queryWrapper.eq("username", username);
            }
            List<UserRFM> userRFMList = userRFMMapper.selectPage(page, queryWrapper).getRecords();
            ArrayList<RecDto> recDtoList = new ArrayList<>();
            for (UserRFM userRFM : userRFMList) {
                String un = userRFM.getUsername();
                QueryIdf queryIdf = queryIdfMapper.selectOne(new QueryWrapper<QueryIdf>().eq("username", un));
                CatIdf catIdf = catIdfMapper.selectOne(new QueryWrapper<CatIdf>().eq("username", un));
                if (queryIdf != null && catIdf != null) {
                    RecDto recDto = new RecDto()
                            .setUsername(un)
                            .setPreferQuery(queryIdf.getQuery())
                            .setQueryScore(queryIdf.getScore())
                            .setPreferCat(catIdf.getCat())
                            .setCatScore(catIdf.getScore())
                            .setRfm(userRFM.getRfm());
                    recDtoList.add(recDto);
                }
            }
            Integer count = userRFMMapper.selectCount(queryWrapper);
            r.setCode(200).setData(new RecommenderDto().setRecommenderList(recDtoList).setTotalCnt(count));
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

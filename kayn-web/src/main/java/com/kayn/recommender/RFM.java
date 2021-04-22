package com.kayn.recommender;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.mapper.rfm.UserRFMMapper;
import com.kayn.pojo.rfm.UserRFM;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RFM {

    private static Integer AVG_R = 10;
    private static Integer AVG_F = 20;
    private static Double AVG_M = 1000D;

    @Resource
    UserRFMMapper userRFMMapper;

    public Integer getDiffDay(Long time1, Long time2) {
        long res;
        if (time1 > time2) {
            res = (time1 - time2) / (1000 * 60 * 60 * 24);
        } else {
            res = (time2 - time1) / (1000 * 60 * 60 * 24);
        }
        return (int) res;
    }

    public void refreshAvgRFM() {
        List<UserRFM> userRFMList = userRFMMapper.selectList(new QueryWrapper<>());
        int size = userRFMList.size();
        if (size > 0) {
            Integer totalR = 0, totalF = 0;
            Double totalM = 0D;
            for (UserRFM userRFM : userRFMList) {
                Integer R = getDiffDay(userRFM.getRecency(), System.currentTimeMillis());
                Integer F = userRFM.getFrequency();
                Double M = userRFM.getMonetary();
                totalR += R;
                totalF += F;
                totalM += M;
            }
            AVG_R = totalR / size;
            AVG_F = totalF / size;
            AVG_M = totalM / size;
        }
    }

    public Integer getRFM(Long recency, Integer frequency, Double monetary) {
        refreshAvgRFM();

        int R = 0, F = 0, M = 0;

        // R 值统计
        Long currentTimeMillis = System.currentTimeMillis();
        Integer diffDay = getDiffDay(currentTimeMillis, recency);
        if (diffDay < AVG_R) {
            R = 1;
        }

        // F 值 统计
        if (frequency > AVG_F) {
            F = 1;
        }

        // M 值统计
        if (monetary > AVG_M) {
            M = 1;
        }

        return R*100 + F*10 + M;
    }
}

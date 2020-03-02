package com.andy.spring.util.encrypt;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * MurmurHash工具
 *
 * @author 庞先海 2019-12-23
 */
public class MurmurHashUtil {

    private static final int BUCKETS = 100;

    /**
     * 借助MurmurHash算法实现流量分配
     *
     * @param hashStr   hash字符串
     * @param ratioList 比例列表
     * @return 命中比例的偏移量
     */
    public static Integer assignmentRatio(String hashStr, List<Float> ratioList) {
        return assignmentRatio(hashStr, ratioList, BUCKETS);
    }

    /**
     * 借助MurmurHash算法实现流量分配
     *
     * @param hashStr   hash字符串
     * @param ratioList 比例列表
     * @return 命中比例的偏移量
     */
    public static Integer assignmentRatio(String hashStr, List<Float> ratioList, int buckets) {
        if (CollectionUtils.isEmpty(ratioList)) {
            return null;
        }
        ratioList = ratioList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ratioList)) {
            return null;
        }
        if (ratioList.size() == 1) {
            return 0;
        }
        //切分buckets份
        int bucketId = Hashing.consistentHash(Hashing.murmur3_128().hashString(hashStr, StandardCharsets.UTF_8),
            buckets);
        double totalRatio = ratioList.stream().mapToDouble(Float::doubleValue).sum();
        int offset = 0;
        for (int i = 0; i < ratioList.size(); ++ i) {
            Float ratio = ratioList.get(i);
            double realRatio = ratio / totalRatio;
            int offsetTo = offset + (int)Math.round(realRatio * buckets);
            if (i == ratioList.size() - 1) {
                offsetTo = buckets;
            }
            if (bucketId >= offset && bucketId < offsetTo) {
                return i;
            }
            offset = offsetTo;
        }
        return null;
    }
}

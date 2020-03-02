package com.andy.spring.util;

import com.andy.spring.exception.CommonCode;
import com.andy.spring.exception.CommonException;
import com.andy.spring.util.asserts.AssertUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.util.CollectionUtils;

/**
 * 集合工具类
 *
 * @author 庞先海 2019-11-14
 */
public class CollectionUtil {

    public static <T> List<T> subList(Collection<T> collection, int length) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        if (collection.size() <= length) {
            return new ArrayList<>(collection);
        }
        List<T> list = new ArrayList<>(collection);
        return new ArrayList<>(list.subList(0, length));
    }

    public static <T, E> T parseOne(List<E> list, Function<E, T> function) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        AssertUtil.assertTrue(list.size() == 1, () -> {
            throw new CommonException(CommonCode.TOO_MANY_RESULT.getCode());
        });
        return function.apply(list.get(0));
    }
}

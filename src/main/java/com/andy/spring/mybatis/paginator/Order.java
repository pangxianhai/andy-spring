package com.andy.spring.mybatis.paginator;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 分页功能:排序
 *
 * @author 庞先海 2019-11-17
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 8138022018100161833L;

    private Direction direction;

    private String property;

    private String orderExpr;

    public Order(String property, Direction direction, String orderExpr) {
        this.direction = direction;
        this.property = property;
        this.orderExpr = orderExpr;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getProperty() {
        return property;
    }

    public String getOrderExpr() {
        return orderExpr;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setOrderExpr(String orderExpr) {
        this.orderExpr = orderExpr;
    }

    private final static String INJECTION_REGEX = "[A-Za-z0-9\\_\\-\\+\\.]+";

    public static boolean isSqlInjection(String str) {
        return ! Pattern.matches(INJECTION_REGEX, str);
    }

    @Override
    public String toString() {
        if (isSqlInjection(property)) {
            throw new IllegalArgumentException("SQLInjection property: " + property);
        }
        if (orderExpr != null && orderExpr.contains("?")) {
            String[] exprs = orderExpr.split("\\?");
            if (exprs.length == 2) {
                return String.format(orderExpr.replaceAll("\\?", "%s"), property) + (direction == null ? ""
                    : " " + direction.name());
            }
            return String.format(orderExpr.replaceAll("\\?", "%s"), property,
                direction == null ? "" : " " + direction.name());
        }
        return property + (direction == null ? "" : " " + direction.name());
    }


    public static List<Order> formString(String orderSegment) {
        return formString(orderSegment, null);
    }

    public static List<Order> formString(String orderSegment, String orderExpr) {
        if (StringUtils.isEmpty(orderSegment)) {
            return new ArrayList<>(0);
        }

        List<Order> results = new ArrayList<>();
        String[] orderSegments = orderSegment.trim().split(",");
        for (String sortSegment : orderSegments) {
            Order order = formString0(sortSegment, orderExpr);
            if (order != null) {
                results.add(order);
            }
        }
        return results;
    }


    private static Order formString0(String orderSegment, String orderExpr) {

        if (StringUtils.isEmpty(orderSegment) || orderSegment.startsWith("null.") || orderSegment.startsWith(".")) {
            return null;
        }

        String[] array = orderSegment.trim().split("\\.");
        if (array.length != 1 && array.length != 2) {
            throw new IllegalArgumentException(
                "orderSegment pattern must be {property}.{direction}, input is: " + orderSegment);
        }

        return create(array[0], array.length == 2 ? array[1] : "asc", orderExpr);
    }

    public static Order create(String property, String direction) {
        return create(property, direction, null);
    }

    public static Order create(String property, String direction, String orderExpr) {
        return new Order(property, Direction.fromString(direction), orderExpr);
    }

    public enum Direction {
        /**
         * 升序
         */
        ASC,

        /**
         * 降序
         */
        DESC;

        public static Direction fromString(String value) {
            try {
                return Direction.valueOf(value.toUpperCase(Locale.US));
            } catch (Exception e) {
                return ASC;
            }
        }
    }
}

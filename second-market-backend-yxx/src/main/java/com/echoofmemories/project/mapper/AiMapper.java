package com.echoofmemories.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * AI 聚合数据访问层
 * 为 AI 智能决策/推荐/分析能力提供基础统计查询
 *
 * @author EchoOfMemories
 */
@Mapper
public interface AiMapper {

    /**
     * 统计某品类下同类在售商品的价格分布
     * AVG / MIN / MAX / COUNT
     */
    @Select("SELECT " +
            "  AVG(price) AS avgPrice, " +
            "  MIN(price) AS minPrice, " +
            "  MAX(price) AS maxPrice, " +
            "  COUNT(*)   AS productCount " +
            "FROM product " +
            "WHERE category = #{category} " +
            "  AND deleted = 0 " +
            "  AND status = 1")
    Map<String, Object> getCategoryPriceStats(@Param("category") String category);

    /**
     * 按日期聚合某品类近 N 天的成交均价
     * orders.status = 4 表示已完成，关联 product 表按 category 过滤
     */
    @Select("SELECT " +
            "  DATE(o.create_time) AS tradeDate, " +
            "  AVG(o.amount)       AS avgPrice, " +
            "  COUNT(*)            AS orderCount " +
            "FROM orders o " +
            "INNER JOIN product p ON o.product_id = p.id " +
            "WHERE o.status = 4 " +
            "  AND o.deleted = 0 " +
            "  AND p.category = #{category} " +
            "  AND o.create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(o.create_time) " +
            "ORDER BY tradeDate ASC")
    List<Map<String, Object>> getCategoryDailyPriceTrend(
            @Param("category") String category,
            @Param("days") Integer days);

    /**
     * 同类 TOP N 商品（按 views + favorites 降序）
     */
    @Select("SELECT " +
            "  id, " +
            "  title, " +
            "  description, " +
            "  category, " +
            "  price, " +
            "  condition_score AS conditionScore, " +
            "  school_id       AS schoolId, " +
            "  status, " +
            "  view_count      AS viewCount, " +
            "  favorite_count  AS favoriteCount, " +
            "  create_time     AS createTime, " +
            "  (view_count + favorite_count) AS hotScore " +
            "FROM product " +
            "WHERE category = #{category} " +
            "  AND deleted = 0 " +
            "  AND status = 1 " +
            "ORDER BY hotScore DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getSimilarProducts(
            @Param("category") String category,
            @Param("limit") Integer limit);

    /**
     * 同校买家画像匹配（按同校 + 过去30天有购买行为 + 购买过同品类，按次数降序）
     */
    @Select("SELECT " +
            "  u.id          AS userId, " +
            "  u.username    AS username, " +
            "  u.nickname    AS nickname, " +
            "  u.avatar      AS avatar, " +
            "  u.school_id   AS schoolId, " +
            "  u.credit_score AS creditScore, " +
            "  COUNT(o.id)   AS buyCount " +
            "FROM sys_user u " +
            "INNER JOIN orders o  ON u.id = o.buyer_id " +
            "INNER JOIN product p ON o.product_id = p.id " +
            "WHERE u.school_id = #{schoolId} " +
            "  AND u.deleted = 0 " +
            "  AND o.status = 4 " +
            "  AND o.deleted = 0 " +
            "  AND p.category = #{category} " +
            "  AND o.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY u.id, u.username, u.nickname, u.avatar, u.school_id, u.credit_score " +
            "ORDER BY buyCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCampusMatchedBuyers(
            @Param("schoolId") Long schoolId,
            @Param("category") String category,
            @Param("limit") Integer limit);

    /**
     * 同校卖家画像匹配（按同校 + 过去30天有出售行为 + 出售过同品类，按次数降序）
     */
    @Select("SELECT " +
            "  u.id           AS userId, " +
            "  u.username     AS username, " +
            "  u.nickname     AS nickname, " +
            "  u.avatar       AS avatar, " +
            "  u.school_id    AS schoolId, " +
            "  u.credit_score AS creditScore, " +
            "  COUNT(o.id)    AS sellCount " +
            "FROM sys_user u " +
            "INNER JOIN orders o  ON u.id = o.seller_id " +
            "INNER JOIN product p ON o.product_id = p.id " +
            "WHERE u.school_id = #{schoolId} " +
            "  AND u.deleted = 0 " +
            "  AND o.status = 4 " +
            "  AND o.deleted = 0 " +
            "  AND p.category = #{category} " +
            "  AND o.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY u.id, u.username, u.nickname, u.avatar, u.school_id, u.credit_score " +
            "ORDER BY sellCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getCampusMatchedSellers(
            @Param("schoolId") Long schoolId,
            @Param("category") String category,
            @Param("limit") Integer limit);

    /**
     * 模糊搜索商品（关键词命中 title / description，schoolId 可空过滤）
     */
    @Select("<script>" +
            "SELECT " +
            "  id, " +
            "  title, " +
            "  description, " +
            "  category, " +
            "  price, " +
            "  condition_score AS conditionScore, " +
            "  school_id       AS schoolId, " +
            "  status, " +
            "  view_count      AS viewCount, " +
            "  favorite_count  AS favoriteCount, " +
            "  create_time     AS createTime " +
            "FROM product " +
            "WHERE deleted = 0 " +
            "  AND status = 1 " +
            "  AND (title LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "  <if test='schoolId != null'>" +
            "    AND school_id = #{schoolId} " +
            "  </if>" +
            "ORDER BY (view_count + favorite_count) DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Map<String, Object>> searchProductsByKeyword(
            @Param("keyword") String keyword,
            @Param("schoolId") Long schoolId,
            @Param("limit") Integer limit);

    /**
     * 平台整体数据统计
     * 商品数 / 订单数 / 已完成订单总金额 / 用户数
     */
    @Select("SELECT " +
            "  (SELECT COUNT(*) FROM product WHERE deleted = 0)              AS productCount, " +
            "  (SELECT COUNT(*) FROM orders WHERE deleted = 0)               AS orderCount, " +
            "  (SELECT IFNULL(SUM(amount), 0) FROM orders WHERE status = 4 AND deleted = 0) AS totalTradeAmount, " +
            "  (SELECT COUNT(*) FROM sys_user WHERE deleted = 0)             AS userCount")
    Map<String, Object> getPlatformStats();
}

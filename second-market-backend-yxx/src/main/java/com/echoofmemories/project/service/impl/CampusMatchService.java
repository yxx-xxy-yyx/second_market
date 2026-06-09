package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.dto.AiCampusMatchRequest;
import com.echoofmemories.project.dto.AiCampusMatchResponse;
import com.echoofmemories.project.entity.Orders;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.School;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.mapper.OrdersMapper;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.SchoolMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusMatchService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final SchoolMapper schoolMapper;
    private final OrdersMapper ordersMapper;

    public AiCampusMatchResponse campusMatch(AiCampusMatchRequest request, Long userId) {
        log.info("校园匹配 - 用户ID: {}, 商品ID: {}", userId, request.getProductId());

        AiCampusMatchResponse response = new AiCampusMatchResponse();

        Long schoolId = null;
        String category = null;
        if (request.getProductId() != null) {
            Product product = productMapper.selectById(request.getProductId());
            if (product != null) {
                schoolId = product.getSchoolId();
                category = product.getCategory();
            }
        }
        if (schoolId == null && userId != null) {
            User u = userMapper.selectById(userId);
            if (u != null) schoolId = u.getSchoolId();
        }

        String schoolName = "";
        if (schoolId != null) {
            School s = schoolMapper.selectById(schoolId);
            if (s != null) {
                if (s.getNameZh() != null) schoolName = s.getNameZh();
                else if (s.getNameEn() != null) schoolName = s.getNameEn();
            }
        }

        List<Map<String, Object>> matchedBuyers = new ArrayList<>();
        List<Map<String, Object>> matchedSellers = new ArrayList<>();

        if (schoolId != null) {
            try {
                LocalDateTime since = LocalDateTime.now().minusDays(30);
                Map<Long, Integer> buyerMap = new HashMap<>();
                Map<Long, Integer> sellerMap = new HashMap<>();

                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.eq("deleted", 0);
                ow.ge("create_time", since);
                if (category != null && !category.isEmpty()) {
                    ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND school_id=" + schoolId
                            + " AND category='" + category.replace("'", "") + "'");
                } else {
                    ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND school_id=" + schoolId);
                }
                List<Orders> orders = ordersMapper.selectList(ow);
                for (Orders o : orders) {
                    if (o.getBuyerId() != null) {
                        buyerMap.put(o.getBuyerId(), buyerMap.getOrDefault(o.getBuyerId(), 0) + 1);
                    }
                    if (o.getSellerId() != null) {
                        sellerMap.put(o.getSellerId(), sellerMap.getOrDefault(o.getSellerId(), 0) + 1);
                    }
                }

                QueryWrapper<Product> pw = new QueryWrapper<>();
                pw.eq("deleted", 0).eq("school_id", schoolId);
                pw.isNotNull("user_id");
                if (category != null && !category.isEmpty()) {
                    pw.eq("category", category);
                }
                List<Product> prods = productMapper.selectList(pw);
                for (Product p : prods) {
                    if (p.getUserId() == null) continue;
                    sellerMap.put(p.getUserId(), sellerMap.getOrDefault(p.getUserId(), 0) + 1);
                }

                List<Map.Entry<Long, Integer>> buyerList = new ArrayList<>(buyerMap.entrySet());
                buyerList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                int buyerLimit = 0;
                for (Map.Entry<Long, Integer> e : buyerList) {
                    if (buyerLimit >= 5) break;
                    User u = userMapper.selectById(e.getKey());
                    if (u == null) continue;
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getNickname() == null ? "校园用户" : u.getNickname());
                    m.put("school", schoolName);
                    m.put("matchScore", Math.min(1.0, 0.5 + 0.1 * e.getValue()));
                    m.put("tradeCount", e.getValue());
                    matchedBuyers.add(m);
                    buyerLimit++;
                }

                List<Map.Entry<Long, Integer>> sellerList = new ArrayList<>(sellerMap.entrySet());
                sellerList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                int sellerLimit = 0;
                for (Map.Entry<Long, Integer> e : sellerList) {
                    if (sellerLimit >= 5) break;
                    User u = userMapper.selectById(e.getKey());
                    if (u == null) continue;
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getNickname() == null ? "校园用户" : u.getNickname());
                    m.put("school", schoolName);
                    m.put("matchScore", Math.min(1.0, 0.5 + 0.1 * e.getValue()));
                    m.put("tradeCount", e.getValue());
                    matchedSellers.add(m);
                    sellerLimit++;
                }
            } catch (Exception e) {
                log.warn("校园匹配计算失败: {}", e.getMessage());
            }
        }

        response.setMatchedBuyers(matchedBuyers);
        response.setMatchedSellers(matchedSellers);

        if (!schoolName.isEmpty()) {
            response.setMeetupSuggestions("建议在" + schoolName + "图书馆/食堂/快递点等公开场所当面交易");
            response.setSafetyTips("1. 选择" + schoolName + "校内公共场所见面 2. 当面检查商品后再付款 3. 建议使用平台担保交易 4. 保留聊天记录与支付凭证");
        } else {
            response.setMeetupSuggestions("建议在校园内或其他公共场所见面交易");
            response.setSafetyTips("1. 选择公共场所见面 2. 交易时检查商品 3. 建议使用平台担保交易");
        }

        log.info("校园匹配结果 - schoolId={}, schoolName={}, 买家数={}, 卖家数={}",
                schoolId, schoolName, matchedBuyers.size(), matchedSellers.size());

        return response;
    }
}

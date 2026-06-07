package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.ProductPageRequest;
import com.echoofmemories.project.dto.ProductRequest;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "商品管理", description = "商品相关接口")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "新增商品")
    @PostMapping("/add")
    public Result<String> addProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Product product = new Product();
            BeanUtils.copyProperties(request, product);
            product.setUserId(userId);

            boolean success = productService.addProduct(product);
            if (success) {
                return Result.success("发布成功");
            } else {
                return Result.error("发布失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public Result<String> updateProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            if (request.getId() == null) {
                return Result.error("商品ID不能为空");
            }

            Product existProduct = productService.getById(request.getId());
            if (existProduct == null) {
                return Result.error("商品不存在");
            }

            // 检查用户权限：只有商品所有者和管理员可以修改
            if (!SecurityUtils.canAccessUser(existProduct.getUserId())) {
                return Result.error("403", "无权限修改此商品");
            }

            Product product = new Product();
            BeanUtils.copyProperties(request, product);

            boolean success = productService.updateProduct(product);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Product product = productService.getById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }

            // 检查用户权限：只有商品所有者和管理员可以删除
            if (!SecurityUtils.canAccessUser(product.getUserId())) {
                return Result.error("403", "无权限删除此商品");
            }

            boolean success = productService.deleteProduct(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductWithUserById(id);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "分页查询商品列表")
    @PostMapping("/list")
    public Result<Page<Product>> getProductList(@RequestBody ProductPageRequest request) {
        try {
            Page<Product> page = productService.getProductPage(
                    request.getPageNum(),
                    request.getPageSize(),
                    request.getKeyword(),
                    request.getCategory(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getMinCondition(),
                    request.getMaxCondition(),
                    request.getSortBy(),
                    request.getStatus(),
                    request.getUserId(),
                    request.getSchoolId());
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取我的商品列表")
    @PostMapping("/my")
    public Result<Page<Product>> getMyProducts(@RequestBody ProductPageRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            request.setUserId(userId);
            Page<Product> page = productService.getProductPage(
                    request.getPageNum(),
                    request.getPageSize(),
                    request.getKeyword(),
                    request.getCategory(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getMinCondition(),
                    request.getMaxCondition(),
                    request.getSortBy(),
                    request.getStatus(),
                    userId,
                    null);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/status/{id}")
    public Result<String> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Product product = productService.getById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }

            // 检查用户权限：只有商品所有者和管理员可以修改状态
            if (!SecurityUtils.canAccessUser(product.getUserId())) {
                return Result.error("403", "无权限修改此商品");
            }

            product.setStatus(status);
            boolean success = productService.updateById(product);
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "浏览商品（增加浏览量）")
    @GetMapping("/view/{id}")
    public Result<Product> viewProduct(@PathVariable Long id,
            javax.servlet.http.HttpServletRequest request) {
        try {
            String userIdentifier = getUserIdentifier(request);
            Product product = productService.viewProduct(id, userIdentifier);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    private String getUserIdentifier(javax.servlet.http.HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            return "user_" + userId;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return "ip_" + ip;
    }
}

package com.echoofmemories.project.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultCode枚举测试
 *
 * @author Echo of Memories
 */
public class ResultCodeTest {

    @Test
    public void testAllCodes() {
        assertEquals("200", ResultCode.SUCCESS.getCode());
        assertEquals("操作成功", ResultCode.SUCCESS.getMessage());

        assertEquals("400", ResultCode.BAD_REQUEST.getCode());
        assertEquals("请求参数错误", ResultCode.BAD_REQUEST.getMessage());

        assertEquals("401", ResultCode.UNAUTHORIZED.getCode());
        assertEquals("未授权，请先登录", ResultCode.UNAUTHORIZED.getMessage());

        assertEquals("403", ResultCode.FORBIDDEN.getCode());
        assertEquals("无权限访问", ResultCode.FORBIDDEN.getMessage());

        assertEquals("404", ResultCode.NOT_FOUND.getCode());
        assertEquals("请求资源不存在", ResultCode.NOT_FOUND.getMessage());
    }

    @Test
    public void testAllValues() {
        ResultCode[] values = ResultCode.values();
        assertTrue(values.length > 0);
        assertNotNull(values[0]);
    }

    @Test
    public void testValueOf() {
        ResultCode code = ResultCode.valueOf("SUCCESS");
        assertEquals(ResultCode.SUCCESS, code);

        code = ResultCode.valueOf("INTERNAL_ERROR");
        assertEquals(ResultCode.INTERNAL_ERROR, code);
    }
}


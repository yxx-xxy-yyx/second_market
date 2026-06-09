package com.echoofmemories.project.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Result类单元测试
 *
 * @author Echo of Memories
 */
public class ResultTest {

    @Test
    public void testSuccess() {
        Result<Object> result = Result.success();
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertTrue(result.getSuccess());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSuccessWithData() {
        String data = "test data";
        Result<String> result = Result.success(data);
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(data, result.getData());
        assertTrue(result.getSuccess());
    }

    @Test
    public void testSuccessWithMessageAndData() {
        String message = "自定义成功消息";
        Integer data = 123;
        Result<Integer> result = Result.success(message, data);
        assertEquals("200", result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    public void testError() {
        Result<Object> result = Result.error();
        assertEquals("500", result.getCode());
        assertEquals("操作失败", result.getMessage());
        assertFalse(result.getSuccess());
        assertFalse(result.isSuccess());
    }

    @Test
    public void testErrorWithMessage() {
        String errorMsg = "测试错误";
        Result<Object> result = Result.error(errorMsg);
        assertEquals("500", result.getCode());
        assertEquals(errorMsg, result.getMessage());
    }

    @Test
    public void testErrorWithCodeAndMessage() {
        String code = "400";
        String errorMsg = "参数错误";
        Result<Object> result = Result.error(code, errorMsg);
        assertEquals(code, result.getCode());
        assertEquals(errorMsg, result.getMessage());
    }

    @Test
    public void testErrorWithResultCode() {
        Result<Object> result = Result.error(ResultCode.BAD_REQUEST);
        assertEquals(ResultCode.BAD_REQUEST.getCode(), result.getCode());
        assertEquals(ResultCode.BAD_REQUEST.getMessage(), result.getMessage());
    }

    @Test
    public void testConstructor() {
        String code = "201";
        String message = "创建成功";
        String data = "new item";
        Result<String> result = new Result<>(code, message, data);

        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    public void testDefaultConstructor() {
        Result<Object> result = new Result<>();
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getSuccess());
    }
}


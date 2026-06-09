package com.echoofmemories.project.exception;

import com.echoofmemories.project.common.ResultCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 自定义异常类测试
 *
 * @author Echo of Memories
 */
public class CustomExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "测试异常";
        CustomException exception = new CustomException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(ResultCode.INTERNAL_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void testConstructorWithCodeAndMessage() {
        String code = "400";
        String message = "参数错误";
        CustomException exception = new CustomException(code, message);

        assertEquals(code, exception.getCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithResultCode() {
        CustomException exception = new CustomException(ResultCode.USER_NOT_FOUND);

        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(ResultCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "测试异常";
        Throwable cause = new RuntimeException("根本原因");
        CustomException exception = new CustomException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(ResultCode.INTERNAL_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void testSetCode() {
        CustomException exception = new CustomException("test");
        exception.setCode("404");
        assertEquals("404", exception.getCode());
    }

    @Test
    public void testSetMessage() {
        CustomException exception = new CustomException("original");
        exception.setMessage("updated");
        assertEquals("updated", exception.getMessage());
    }
}


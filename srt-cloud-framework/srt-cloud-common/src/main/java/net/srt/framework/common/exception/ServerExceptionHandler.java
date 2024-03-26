package net.srt.framework.common.exception;

import lombok.extern.slf4j.Slf4j;
import net.srt.framework.common.utils.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理器
 *
 * @author 阿沐 babamu@126.com
 */
@Slf4j
@RestControllerAdvice
public class ServerExceptionHandler {
	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(ServerException.class)
	public Result<String> handleException(ServerException ex) {

		return Result.error(ex.getCode(), ex.getMsg());
	}

	/**
	 * SpringMVC参数绑定，Validator校验不正确
	 */
	@ExceptionHandler(BindException.class)
	public Result<String> bindException(BindException ex) {
		FieldError fieldError = ex.getFieldError();
		assert fieldError != null;
		return Result.error(fieldError.getDefaultMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public Result<String> handleAccessDeniedException(Exception ex) {

		return Result.error(ErrorCode.FORBIDDEN);
	}

	@ExceptionHandler(Exception.class)
	public Result<String> handleException(Exception ex) {
		log.error(ex.getMessage(), ex);
		return Result.error("出错了！异常信息：" + ex.getMessage());
	}

}

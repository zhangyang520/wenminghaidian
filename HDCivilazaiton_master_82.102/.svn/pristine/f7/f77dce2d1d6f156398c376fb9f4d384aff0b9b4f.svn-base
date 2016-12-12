package com.zhjy.hdcivilization.exception;

/**
 * 
 * @项目名：AroundYou 
 * @类名称：NetCodeException   
 * @类描述：   服务端异常
 * @创建人：HXF   
 * @修改人：    
 * @创建时间：2015-12-17 上午11:28:10  
 * @version    
 *
 */
@SuppressWarnings("serial")
public class ContentException extends Exception{

	// 错误的代码的叙述
	private int errorCode;
    private String errorContent;//异常描述

	public ContentException() {
		super();
	}

	public ContentException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ContentException(String errorContent) {
		super(errorContent);
		this.errorContent = errorContent;
	}

	public ContentException(int errorCode, String errorContent) {
		this.errorCode = errorCode;
		this.errorContent = errorContent;
	}

	public ContentException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ContentException(Throwable throwable) {
		super(throwable);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorContent() {
		return errorContent;
	}

	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}
}

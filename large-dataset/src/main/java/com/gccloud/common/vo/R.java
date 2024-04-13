/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gccloud.common.constant.CommonConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 定义Json响应数据
 *
 * @param <T>
 */
@Data
@ApiModel(description = "接口返回的对象")
@Accessors(chain = true)
@NoArgsConstructor
public class R<T> implements Serializable {

    @ApiModelProperty(notes = "响应码 200: 成功，其他: 异常")
    private Integer code;

    @ApiModelProperty(notes = "异常描述信息")
    private String msg;

    @ApiModelProperty(notes = "返回的数据")
    private T data;

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功
     *
     * @return
     */
    public static <E> R<E> success(E data) {
        return new R<E>(CommonConst.Response.Code.SUCCESS, null, data);
    }

    public static <E> R<E> success() {
        return new R<E>(CommonConst.Response.Code.SUCCESS, null, null);
    }


    /**
     * 失败
     *
     * @param msg
     * @return
     */
    public static R error(String msg) {
        R result = new R();
        result.setCode(CommonConst.Response.Code.SERVER_ERROR);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败
     *
     * @param code
     * @param msg
     * @return
     */
    public static R error(Integer code, String msg) {
        R result = new R();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败
     *
     * @return
     */
    public static R error() {
        R result = new R();
        result.setCode(CommonConst.Response.Code.SERVER_ERROR);
        result.setMsg("未知异常，请联系管理员");
        return result;
    }

    /**
     * 添加返回的数据
     *
     * @param data
     * @return
     */
    public R<T> put(T data) {
        this.data = data;
        return this;
    }

    /**
     * 是否正常
     *
     * @return
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.code.intValue() == CommonConst.Response.Code.SUCCESS;
    }

    @JsonIgnore
    public boolean isError() {
        return this.code.intValue() != CommonConst.Response.Code.SUCCESS;
    }
}
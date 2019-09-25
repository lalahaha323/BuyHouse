package com.lala.service.result;

import com.lala.enums.ResultEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DataTables响应结构
 * @author lala
 */

@Data
@NoArgsConstructor
public class ResultDataTableResponse<T> extends ServiceResult{
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;

    public ResultDataTableResponse(int code, String message, T data) {
        super(code, message, data);
    }

    public ResultDataTableResponse(ResultEnum resultEnum) {
        this(resultEnum.getCode(), resultEnum.getMessage(), null);
    }
}

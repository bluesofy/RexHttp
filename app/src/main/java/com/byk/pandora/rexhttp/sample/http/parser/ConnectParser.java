package com.byk.pandora.rexhttp.sample.http.parser;

import com.byk.pandora.rexhttp.model.DataResult;
import com.byk.pandora.rexhttp.sample.http.BaseParser;
import com.byk.pandora.rexhttp.sample.bean.ApiCenter;

/**
 * Created by Byk on 2017/12/28.
 *
 * @author Byk
 */
public class ConnectParser extends BaseParser<ApiCenter> {

    public ConnectParser() {
        super(new DataResult<>(ApiCenter.class, false));
    }
}

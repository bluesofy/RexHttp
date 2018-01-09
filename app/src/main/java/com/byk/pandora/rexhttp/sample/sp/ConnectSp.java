package com.byk.pandora.rexhttp.sample.sp;

import com.baoyz.treasure.Default;
import com.baoyz.treasure.Preferences;

/**
 * Created by Byk on 2017/12/26.
 *
 * @author Byk
 */
@Preferences
public interface ConnectSp {

    @Default("null")
    String getToken();

    void setToken(String token);

    String getPwdCryptKey();

    void setPwdCryptKey(String key);
}

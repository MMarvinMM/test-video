package com.chinatower.platform.auth.service;

import com.chinatower.platform.common.utils.security.DESUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DESPasswordEncoder implements PasswordEncoder {


    @Override
    public String encode(CharSequence charSequence) {
        return DESUtil.encrypt(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(encode(charSequence));
    }
}

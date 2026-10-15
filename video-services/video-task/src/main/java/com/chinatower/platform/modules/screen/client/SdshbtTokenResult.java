package com.chinatower.platform.modules.screen.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SdshbtTokenResult {

    private String token;

    private String refreshToken;
}

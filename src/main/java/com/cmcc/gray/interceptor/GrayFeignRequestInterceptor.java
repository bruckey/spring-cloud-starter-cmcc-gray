package com.cmcc.gray.interceptor;

import com.cmcc.gray.constant.GrayConstant;
import com.cmcc.gray.holder.GrayFlagRequestHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;

import java.util.Collections;

public class GrayFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 如果灰度标记存在，将灰度标记通过HttpHeader传递下去
        String grayTag = GrayFlagRequestHolder.getGrayTag();
        if (!StringUtils.isEmpty(grayTag)) {
            template.header(GrayConstant.GRAY_HEADER, Collections.singleton(grayTag));
        }
    }
}
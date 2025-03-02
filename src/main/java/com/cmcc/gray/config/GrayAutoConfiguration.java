package com.cmcc.gray.config;

import com.cmcc.gray.filter.GrayGatewayAfterFilter;
import com.cmcc.gray.filter.GrayGatewayBeginFilter;
import com.cmcc.gray.handler.GrayGatewayExceptionHandler;
import com.cmcc.gray.interceptor.GrayFeignRequestInterceptor;
import com.cmcc.gray.interceptor.GrayMvcHandlerInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
// 可以通过@ConditionalOnProperty设置是否开启灰度自动配置 默认是不加载的
//@ConditionalOnProperty(value = "cmcc.tool.gray.load",havingValue = "true")
//@EnableConfigurationProperties(GrayVersionProperties.class)
public class GrayAutoConfiguration {


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(value = GlobalFilter.class)
    //@EnableConfigurationProperties(GrayGatewayProperties.class)
    static class GrayGatewayFilterAutoConfiguration {
        @Bean
        public GrayGatewayBeginFilter grayGatewayBeginFilter() {
            return new GrayGatewayBeginFilter();
        }

        @Bean
        public GrayGatewayAfterFilter grayGatewayAfterFilter() {
            return new GrayGatewayAfterFilter();
        }

        @Bean
        public GrayGatewayExceptionHandler grayGatewayExceptionHandler() {
            return new GrayGatewayExceptionHandler();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(value = WebMvcConfigurer.class)
    static class GrayWebMvcAutoConfiguration {
        /**
         * Spring MVC 请求拦截器
         *
         * @return WebMvcConfigurer
         */
        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new GrayMvcHandlerInterceptor());
                }
            };
        }
    }

    @Configuration
    @ConditionalOnClass(value = RequestInterceptor.class)
    static class GrayFeignInterceptorAutoConfiguration {
        /**
         * Feign拦截器
         *
         * @return GrayFeignRequestInterceptor
         */
        @Bean
        public GrayFeignRequestInterceptor grayFeignRequestInterceptor() {
            return new GrayFeignRequestInterceptor();
        }
    }
}

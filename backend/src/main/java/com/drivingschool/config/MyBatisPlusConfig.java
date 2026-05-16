package com.drivingschool.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * <p>
 * 配置分页插件和 Mapper 扫描路径。
 * 分页插件使 MyBatis-Plus 的 Page 对象能自动生成 COUNT 和 LIMIT SQL。
 * </p>
 */
@Configuration
// 扫描 Mapper 接口所在包，后续创建 Mapper 时统一放在此包下
@MapperScan("com.drivingschool.**.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器 — 添加分页支持
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页拦截器，指定数据库类型为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

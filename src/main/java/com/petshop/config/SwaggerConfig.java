package com.petshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("宠物服务平台 API")
                        .version("1.0.0")
                        .description("宠物服务平台后端API接口文档\n\n" +
                                "## 接口分组\n" +
                                "- **平台端API**: 管理员使用的接口，包括用户管理、商家管理、审核等\n" +
                                "- **商家端API**: 商家使用的接口，包括服务管理、商品管理、订单处理等\n" +
                                "- **用户端API**: 用户使用的接口，包括预约、购物、个人中心等\n" +
                                "- **公共API**: 无需认证的公开接口\n\n" +
                                "## 认证方式\n" +
                                "使用 Session 认证，登录后 Session 会被创建")
                        .contact(new Contact()
                                .name("宠物家园")
                                .email("support@petshop.com")
                                .url("https://petshop.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("开发服务器")
                ))
                .components(new Components()
                        .addSecuritySchemes("sessionAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Session认证，登录后自动获取")));
    }
}

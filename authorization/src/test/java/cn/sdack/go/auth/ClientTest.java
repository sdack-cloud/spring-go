package cn.sdack.go.auth;

import cn.sdack.go.auth.query.ClientQuery;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sdack
 * @date 2024/1/7
 */

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
public class ClientTest {


    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void test() throws Exception {

        this.mockMvc.perform(get("/test?q=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("index", queryParameters(
                        parameterWithName("q").description("查询")
                )));

    }


    @Test
    void save() throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        ClientQuery query = new ClientQuery();
        query.setClientName(uuid.substring(0, 10));
        ArrayList<String> methods = new ArrayList<>();
        methods.add("client_secret_post");
        methods.add("client_secret_jwt");
        query.setMethods(methods);
        ArrayList<String> grantTypes = new ArrayList<>();
        grantTypes.add("sms_code");
        grantTypes.add("refresh_token");
        grantTypes.add("device_code");
        grantTypes.add("client_credentials");
        query.setGrantTypes(grantTypes);
        ArrayList<String> redirectUris = new ArrayList<>();
        redirectUris.add("https://www.baidu.com");
        query.setRedirectUris(redirectUris);
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("read");
        scopes.add("user");
        query.setScopes(scopes);
        query.setIssConsent(0);
        query.setIssRefresh(1);
        query.setExpRefresh(1440);
        query.setExpAccess(15);

        this.mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(query))
        )
                .andExpect(status().isOk())
                .andDo(
                        document("client",
                                relaxedRequestFields(
                                        fieldWithPath("clientName").description("客户端名称"),
                                        fieldWithPath("methods").description("客户端支持方式 选项client_secret_post，client_secret_post，client_secret_post"),
                                        fieldWithPath("grantTypes").description("授权类型 选项refresh_token，client_credentials，device_code，sms_code,jwt_bearer"),
                                        fieldWithPath("redirectUris").description("重定向地址"),
                                        fieldWithPath("scopes").description("作用域"),
                                        fieldWithPath("issConsent").description("是否需要同意授权"),
                                        fieldWithPath("issRefresh").description("是否需要刷新令牌"),
                                        fieldWithPath("expRefresh").description("刷新令牌过期时间/分钟"),
                                        fieldWithPath("expAccess").description("令牌过期时间/分钟")
                                ) ,
                                relaxedResponseFields(
                                        fieldWithPath("succeed").description("成功"),
                                        fieldWithPath("message").description("提示"),
                                        fieldWithPath("data").description("数据")
                                )
                        )
                )
        ;
    }

    @Test
    void updateSecret() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/client/resetSecret?clientId=10044828").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("client/updateSecret", queryParameters(
                        parameterWithName("clientId").description("客户端id")),
                        relaxedResponseFields(
                                fieldWithPath("succeed").description("成功"),
                                fieldWithPath("message").description("提示"),
                                fieldWithPath("data").description("Secret")
                        )
                )).andReturn();
    }
}

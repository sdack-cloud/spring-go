package cn.sdack.go.auth.jackson2;

import cn.sdack.go.auth.entity.AccountEntity;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author sdack
 * @date 2023/12/23
 */
public class AccountDeserializer extends JsonDeserializer<AccountEntity> {

    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TypeReference<HashSet<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET =  new TypeReference<>() {};

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        if (jsonNode.has(field)) {
            return jsonNode.get(field);
        } else {
            return MissingNode.getInstance();
        }
    }

    @Override
    public AccountEntity deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();

        JsonNode jsonNode = mapper.readTree(jp);

        ArrayNode jsonNode3 = (ArrayNode) jsonNode.get("authority");
//        HashSet<SimpleGrantedAuthority> authorities = mapper.convertValue(jsonNode3, SIMPLE_GRANTED_AUTHORITY_SET);

        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        String password = passwordNode.asText("");

        Long id = readJsonNode(jsonNode, "id").asLong(0);
        String account = readJsonNode(jsonNode, "account").asText("");
        Long mobile = readJsonNode(jsonNode, "phone").asLong(0);
        String phone = readJsonNode(jsonNode, "phone").asText("");
        String nickname = readJsonNode(jsonNode, "nickname").asText("");
        String avatar = readJsonNode(jsonNode, "avatar").asText("");
        String name = readJsonNode(jsonNode, "name").asText("");
        String email = readJsonNode(jsonNode, "email").asText("");
        Boolean issLock = readJsonNode(jsonNode, "issLock").asBoolean(false);
        Boolean issActive = readJsonNode(jsonNode, "issActive").asBoolean(false);

        JsonNode jsonNode1 = jsonNode.get("expTime");

        LocalDateTime expTime = null;
        if (!jsonNode1.isNull()){
            expTime = LocalDateTime.parse(jsonNode.get("expTime").asText(""), sdf);
        }

        LocalDateTime del = null;
        JsonNode jsonNode2 = jsonNode.get("del");
        if (!jsonNode2.isNull()) {
            del = LocalDateTime.parse(jsonNode.get("del").asText(""), sdf);
        }

        AccountEntity result = new AccountEntity();
        result.id = id;
        result.account = account;
        result.nickname = nickname;
        result.email = email;
        result.name = name;
        result.phone = phone;
        result.mobile = mobile;
        result.pwd = password;
        result.issLock = issLock;
        result.issActive = issActive;
        result.avatar = avatar;
//        if (authorities != null) {
//            result.authority = (HashSet<SimpleGrantedAuthority>) authorities;
//        }
        result.expTime = expTime;
        result.del = del;


        if (passwordNode.asText(null) == null) {
            result.pwd = null;
        }
        return result;
    }
}

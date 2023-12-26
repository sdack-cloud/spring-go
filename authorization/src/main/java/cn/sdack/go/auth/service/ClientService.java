package cn.sdack.go.auth.service;

import cn.sdack.go.auth.entity.ClientEntity;
import cn.sdack.go.auth.query.ClientQuery;

/**
 * @author sdack
 * @date 2023/12/24
 */
public interface ClientService {



    ClientEntity getByClientId(String client) throws IllegalAccessException;

    void save(ClientQuery client) throws IllegalAccessException;

    String updateSecret(String clientId) throws IllegalAccessException;

}

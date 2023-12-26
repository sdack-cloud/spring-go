package cn.sdack.go.auth.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;

/**
 * @author sdack
 * @date 2023/12/23
 */
public class ClientQuery {

    private String id;

    private String clientId;

    @NotNull(message = "客户端必填", groups = {Add.class})
    private String clientName;

    private ArrayList<String> methods;

    private ArrayList<String> grantTypes;

    @NotNull(message = "重定向连接 必填", groups = {Add.class})
    @Size(min = 1,message = "重定向连接 必填", groups = {Add.class})
    private ArrayList<String> redirectUris;

    @NotNull(message = "作用域 必填", groups = {Add.class})
    @Size(min = 1,message = "作用域 必填", groups = {Add.class})
    private ArrayList<String> scopes;

    private int issConsent = 1;

    private int issRefresh = 1;

    private long expRefresh = 1440;// 分钟

    private long expAccess = 15;// 分钟

    private ArrayList<String> ids = new ArrayList<>();

    private ArrayList<String> clientIds = new ArrayList<>();

    private ArrayList<String> clientNames = new ArrayList<>();

    public interface Add {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public ArrayList<String> getClientIds() {
        return clientIds;
    }

    public void setClientIds(ArrayList<String> clientIds) {
        this.clientIds = clientIds;
    }

    public ArrayList<String> getClientNames() {
        return clientNames;
    }

    public void setClientNames(ArrayList<String> clientNames) {
        this.clientNames = clientNames;
    }

    public ArrayList<String> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<String> methods) {
        this.methods = methods;
    }

    public ArrayList<String> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(ArrayList<String> grantTypes) {
        this.grantTypes = grantTypes;
    }

    public ArrayList<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(ArrayList<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public ArrayList<String> getScopes() {
        return scopes;
    }

    public void setScopes(ArrayList<String> scopes) {
        this.scopes = scopes;
    }

    public int getIssConsent() {
        return issConsent;
    }

    public void setIssConsent(int issConsent) {
        this.issConsent = issConsent;
    }

    public int getIssRefresh() {
        return issRefresh;
    }

    public void setIssRefresh(int issRefresh) {
        this.issRefresh = issRefresh;
    }

    public long getExpRefresh() {
        return expRefresh;
    }

    public void setExpRefresh(long expRefresh) {
        this.expRefresh = expRefresh;
    }

    public long getExpAccess() {
        return expAccess;
    }

    public void setExpAccess(long expAccess) {
        this.expAccess = expAccess;
    }
}

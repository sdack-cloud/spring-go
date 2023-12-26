package cn.sdack.go.auth.entity;

import java.io.Serializable;

public class JsonResult<T> implements Serializable {
    public boolean succeed = false;
    public int code = 0;
    public String message = "成功";
    public T data;

    public static JsonResult getInstance() {
        //单例模式
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final JsonResult INSTANCE = new JsonResult();
    }

    public JsonResult() {
    }


    public static JsonResult toJson(boolean succeed, String message, Object data, int code) {
        JsonResult instance = getInstance();
        instance.succeed = succeed;
        instance.message = message;
        instance.data = data;
        instance.code = code;
        return instance;
    }

    public static JsonResult toJson(boolean succeed, String message, int code) {
        JsonResult instance = getInstance();
        instance.succeed = succeed;
        instance.message = message;
        instance.data = null;
        instance.code = code;
        return instance;
    }
    public static JsonResult toJson(boolean succeed, String message) {
        JsonResult instance = getInstance();
        instance.succeed = succeed;
        instance.message = message;
        instance.data = null;
        instance.code = 0;
        return instance;
    }

    public static JsonResult toJson(boolean succeed, int code) {
        JsonResult instance = getInstance();
        instance.succeed = succeed;
        instance.message = "";
        instance.data = null;
        instance.code = code;
        return instance;
    }

    public static JsonResult toJson(boolean succeed) {
        JsonResult instance = getInstance();
        instance.succeed = succeed;
        instance.message = "";
        instance.data = null;
        instance.code = 0;
        return instance;
    }


    public static JsonResult toJson(Object data, int code) {
        JsonResult instance = getInstance();
        instance.succeed = true;
        instance.message = "成功";
        instance.data = data;
        instance.code = code;
        return instance;
    }

    public static JsonResult toJson(Object data) {
        JsonResult instance = getInstance();
        instance.succeed = true;
        instance.message = "成功";
        instance.data = data;
        instance.code = 0;
        return instance;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JsonResult(boolean succeed, String msg, T data) {
        this.succeed = succeed;
        this.message = msg;
        this.data = data;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }


    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

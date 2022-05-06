package cc.seckill.springcloud.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * description: Result <br>
 * date: 2022/4/8 19:04 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public class Result extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public Result() {
        put("code", 200);
    }

    public static Result error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static Result error(Object msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }

    public static Result ok(Object msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }


    public static Result ok(Map<String, Object> map) {
        Result r = new Result();
        r.putAll(map);
        return r;
    }

    public static Result ok() {
        return new Result();
    }


    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public void code(int code) {
        put("code", code);
    }

    public void msg(String msg) {
        put("msg", msg);
    }
}

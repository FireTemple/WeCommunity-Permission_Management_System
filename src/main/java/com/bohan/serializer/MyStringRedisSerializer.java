package com.bohan.serializer;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MyStringRedisSerializer implements RedisSerializer<Object> {

    /**
     *
     * 这里是为了让数值可读性更强 所以自己创建一种序列化方式 给template
     *
     */
    private final Charset charset;

    public MyStringRedisSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public MyStringRedisSerializer(Charset charset){
        Assert.notNull(charset,"charset must not be null!");
        this.charset = charset;

    }
    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        if(object instanceof String){
            return object.toString().getBytes(charset);
        }else {
            String string = JSON.toJSONString(object); return string.getBytes(charset);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return (bytes == null ? null : new String(bytes, charset));    }
}

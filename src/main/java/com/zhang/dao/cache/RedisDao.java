package com.zhang.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.zhang.domain.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {

    private final JedisPool jedisPool;

    public RedisDao(String ip,int port) {
        jedisPool = new JedisPool(ip,port);
    }

    //创建scheam
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


    /**
     *  通过id查找缓存
     * @param seckillId  商品id
     * @return 缓存对象
     */
    public Seckill getSeckill(long seckillId){
        //redis 逻辑操作
        //从数据库读取id对应的缓存
        try {
            Jedis jedis = jedisPool.getResource();


            try {
                String key = "seckill:" + seckillId;
                //因为需要实现局部序列化操作
                //进行对应的序列化   protostuff :pojo
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes!=null){
                    Seckill seckill = schema.newMessage();//创建新的空对象
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema); //返序列化操作

                    return seckill;
                }
            } finally {
                jedis.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * 将数据库查找结果放入缓存
     * @param seckill 需要插入的对象
     * @return
     */
    public String putSeckill(Seckill seckill){

        Jedis jedis = jedisPool.getResource();

        try {
            String key = "seckill:"+ seckill.getSeckillId();
            //序列化操作
            try {
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //设置超时缓存
                int timeout = 60 *60 ;//一小时
                String result= jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;


    }
}

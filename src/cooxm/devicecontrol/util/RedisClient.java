package cooxm.devicecontrol.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cooxm.devicecontrol.control.LogicControl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;




public class RedisClient {

    private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    private static ShardedJedis shardedJedis;//切片额客户端连接
    private ShardedJedisPool shardedJedisPool;//切片连接池

    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource();         
    } 
 
    /**
     * 初始化非切片池
     */
    private void initialPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        
        jedisPool = new JedisPool(config,"172.16.35.170",6379);
    }
    
    /** 
     * 初始化切片池 
     */ 
    private void initialShardedPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("172.16.35.170", 6379, "master")); 

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 
    
    public void show() {     
        jedisPool.returnResource(jedis);
        shardedJedisPool.returnResource(shardedJedis);
    } 
    
    public void redisClientTest(){
        RedisClient redisClient= new RedisClient();
    	redisClient.show();
       //System.out.println(redisClient.jedis.flushDB()); 
        
        System.out.println("=============增=============");
        System.out.println("hashs中添加key001和value001键值对："+shardedJedis.hset("hashs", "key001", "value001")); 
        System.out.println("hashs中添加key002和value002键值对："+shardedJedis.hset("hashs", "key002", "value002")); 
        System.out.println("hashs中添加key003和value003键值对："+shardedJedis.hset("hashs", "key003", "value003"));
        System.out.println("新增key004和4的整型键值对："+shardedJedis.hincrBy("hashs", "key004", 4l));
        System.out.println("hashs中的所有值："+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============删=============");
        System.out.println("hashs中删除key002键值对："+shardedJedis.hdel("hashs", "key002"));
        System.out.println("hashs中的所有值："+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============改=============");
        System.out.println("key004整型键值的值增加100："+shardedJedis.hincrBy("hashs", "key004", 100l));
        System.out.println("hashs中的所有值："+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============查=============");
        System.out.println("判断key003是否存在："+shardedJedis.hexists("hashs", "key003"));
        System.out.println("获取key004对应的值："+shardedJedis.hget("hashs", "key004"));
        System.out.println("批量获取key001和key003对应的值："+shardedJedis.hmget("hashs", "key001", "key003")); 
        System.out.println("获取hashs中所有的key："+shardedJedis.hkeys("hashs"));
        System.out.println("获取hashs中所有的value："+shardedJedis.hvals("hashs"));
        System.out.println();
    }
    
    public void hashmapTest(){
    	jedis.set("richard", "good boy");
    	jedis.get("richard");        
        jedis.hset("hashs", "key001", "value001");
    }
    
    /** 删除 key*/
    public static void reConstructRedis(){
    	Jedis jedis= new Jedis("120.24.81.226", 6379,5000);
    	Set<String> keys = jedis.keys("*");
    	for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String[] keyArr=key.split("_");
			if(keyArr.length==2 && isNumeric(keyArr[0] )){
				System.out.println(key);
				Map<String, String> map = jedis.hgetAll(key);
				String newKey=keyArr[1]+":"+keyArr[0];
				/*for (Iterator iterator2 = keys.iterator(); iterator2.hasNext();) {
					String string = (String) iterator2.next();
					
				}*/
				String ok=jedis.hmset(newKey, map);
				if(ok.equals("OK")){
					jedis.del(key);
				}
			}			
		}
    }
    
    /** 
     *  */
    public static void reConstructRedis2(){
    	Jedis jedis= new Jedis("120.24.81.226", 6379,5000);
    	jedis.select(9);
    	Set<String> keys = jedis.keys("*");
    	for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String key2=new String(key);
			String key3=key2;
			jedis.select(9);
			if( key.contains(":4000")  ){
				jedis.del(key);
			/*Map<String, String> map = jedis.hgetAll(key);
				for (Entry<String, String> entry : map.entrySet()) {
					key3=key2.replace("40006", "10002");
					entry.getValue().replace("40006", "10002");
					key3=key2.replace("40004", "10005");
					entry.getValue().replace("40004", "10005");
					key3=key2.replace("40008", "10000");
					entry.getValue().replace("40008", "10000");	
					System.out.println(key+"  "+entry.getKey());
				}
				jedis.select(9);
				String ok=jedis.hmset(key3, map);
				
				if(ok.equals("OK") ){
					jedis.select(9);
					jedis.del(key);
				}*/
			}
		}
    }
    
    public static boolean isNumeric(String str){
    	  for (int i = str.length();--i>=0;){   
    	   if (!Character.isDigit(str.charAt(i))){
    	    return false;
    	   }
    	  }
    	  return true;
    	 }

    public static void main(String[] args) {  
    	
   	Jedis jedis= new Jedis("172.16.35.170", 6379,5000);
   	jedis.select(9);
//   	String x=jedis.hget(LogicControl.currentProfile, 15662+"");
//   	System.out.println(x);

   	reConstructRedis2();
        /*JedisPubSub jedisPubSub=new JedisPubSub() {
			
			@Override
			public void onUnsubscribe(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSubscribe(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPUnsubscribe(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPSubscribe(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPMessage(String arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMessage(String arg0, String arg1) {
				// TODO Auto-generated method stub
				
				System.out.println(arg0+ "_"+ arg1);
				
			}
		};
        //jedis.publish("msg", "201451");
        jedis.subscribe(jedisPubSub, "msg");*/

    	    	
    }



}
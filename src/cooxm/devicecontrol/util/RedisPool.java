package cooxm.devicecontrol.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;




public class RedisPool {

    private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    
/*    private ShardedJedisPool shardedJedisPool;//切片连接池
    private static ShardedJedis shardedJedis;//切片额客户端连接
*/
    
    

    
    public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

/*	public static ShardedJedis getShardedJedis() {
		return shardedJedis;
	}

	public static void setShardedJedis(ShardedJedis shardedJedis) {
		RedisPool.shardedJedis = shardedJedis;
	}

	public ShardedJedisPool getShardedJedisPool() {
		return shardedJedisPool;
	}

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}*/

	public RedisPool() 
    { 
        initialPool(); 
        jedis = jedisPool.getResource();         
    } 
	
/*	public RedisPool(int a){
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
	}*/
 
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
    

    
    public static void main(String[] args) {  
    	
    }

}
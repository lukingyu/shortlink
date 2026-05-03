package github.lukingyu.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RBloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> rBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        rBloomFilter.tryInit(1000000L, 0.01);
        return rBloomFilter;
    }

}

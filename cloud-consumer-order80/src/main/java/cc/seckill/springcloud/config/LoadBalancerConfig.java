package cc.seckill.springcloud.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: LoadBalancerConfig <br>
 * date: 2022/4/12 20:12 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Configuration
public class LoadBalancerConfig {
    @Bean
    public ReactorServiceInstanceLoadBalancer customLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        return new CustomRandomLoadBalancerClient(serviceInstanceListSupplierProvider);
    }
}

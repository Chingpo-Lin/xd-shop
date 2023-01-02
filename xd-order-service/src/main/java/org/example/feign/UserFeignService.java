package org.example.feign;

import org.example.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "xd-user-service")
public interface UserFeignService {

    /**
     * query user address
     * @param addressId
     * @return
     */
    @GetMapping("/api/address/v1/find/{address_id}")
    JsonData detail(@PathVariable("address_id") long addressId);
}

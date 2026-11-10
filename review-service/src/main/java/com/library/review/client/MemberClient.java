package com.library.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/api/members/{id}")
    void getMember(@PathVariable("id") Long id);
}

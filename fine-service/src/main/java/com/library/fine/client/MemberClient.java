package com.library.fine.client;

import com.library.fine.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface MemberClient {
    @GetMapping("/api/members/{id}")
    MemberDto getMember(@PathVariable("id") Long id);
}
package com.library.borrowing.client;

import com.library.borrowing.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface MemberClient {

    /** GET /api/members/{id} */
    @GetMapping("/api/members/{id}")
    MemberDto getMember(@PathVariable("id") Long id);
}
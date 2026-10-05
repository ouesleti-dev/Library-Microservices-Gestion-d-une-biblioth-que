package com.library.member.controller;

import com.library.member.model.Member;
import com.library.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Members", description = "Gestion des membres de la bibliotheque")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les membres")
    public List<Member> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un membre par son id")
    public Member getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un membre")
    public Member create(@Valid @RequestBody Member member) {
        return service.create(member);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un membre")
    public Member update(@PathVariable Long id, @Valid @RequestBody Member member) {
        return service.update(id, member);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un membre")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

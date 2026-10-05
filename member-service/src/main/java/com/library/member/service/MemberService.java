package com.library.member.service;

import com.library.member.exception.ResourceNotFoundException;
import com.library.member.model.Member;
import com.library.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    public Member findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membre introuvable (id=" + id + ")"));
    }

    public Member create(Member member) {
        member.setId(null);
        return repository.save(member);
    }

    public Member update(Long id, Member data) {
        Member existing = findById(id);
        existing.setFirstName(data.getFirstName());
        existing.setLastName(data.getLastName());
        existing.setEmail(data.getEmail());
        existing.setPhone(data.getPhone());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Member existing = findById(id);
        repository.delete(existing);
    }
}

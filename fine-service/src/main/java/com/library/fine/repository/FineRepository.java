package com.library.fine.repository;
import com.library.fine.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FineRepository extends JpaRepository<Fine, Long> { List<Fine> findByMemberId(Long memberId); }

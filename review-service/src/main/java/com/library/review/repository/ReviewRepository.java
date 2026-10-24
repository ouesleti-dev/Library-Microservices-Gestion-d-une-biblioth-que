package com.library.review.repository;
import com.library.review.model.Review; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ReviewRepository extends JpaRepository<Review,Long>{List<Review> findByBookId(Long bookId);}

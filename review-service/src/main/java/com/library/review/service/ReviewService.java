package com.library.review.service;
import com.library.review.client.LibraryClient; import com.library.review.dto.ReviewRequest; import com.library.review.exception.ResourceNotFoundException; import com.library.review.model.Review; import com.library.review.repository.ReviewRepository; import org.springframework.stereotype.Service; import java.util.List;
@Service public class ReviewService {
    private final ReviewRepository repository; private final LibraryClient client;
    public ReviewService(ReviewRepository r,LibraryClient c){repository=r;client=c;}
    public List<Review> findAll(){return repository.findAll();} public List<Review> findByBook(Long id){return repository.findByBookId(id);}
    public Review findById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Avis introuvable (id="+id+")"));}
    public Review create(ReviewRequest r){client.verifyBook(r.bookId());client.verifyMember(r.memberId());Review v=new Review();v.setBookId(r.bookId());v.setMemberId(r.memberId());v.setRating(r.rating());v.setComment(r.comment());return repository.save(v);}
    public Review update(Long id,ReviewRequest r){Review v=findById(id);client.verifyBook(r.bookId());client.verifyMember(r.memberId());v.setRating(r.rating());v.setComment(r.comment());return repository.save(v);}
    public void delete(Long id){repository.delete(findById(id));}
}

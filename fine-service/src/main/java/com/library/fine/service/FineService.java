package com.library.fine.service;
import com.library.fine.client.LibraryClient;
import com.library.fine.dto.FineRequest;
import com.library.fine.exception.ResourceNotFoundException;
import com.library.fine.model.Fine;
import com.library.fine.repository.FineRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
public class FineService {
    private final FineRepository repository; private final LibraryClient client;
    public FineService(FineRepository r,LibraryClient c){repository=r;client=c;}
    public List<Fine> findAll(){return repository.findAll();}
    public List<Fine> findByMember(Long id){return repository.findByMemberId(id);}
    public Fine findById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Amende introuvable (id="+id+")"));}
    public Fine create(FineRequest request){client.verifyMember(request.memberId());client.verifyBorrowing(request.borrowingId());Fine f=new Fine();f.setMemberId(request.memberId());f.setBorrowingId(request.borrowingId());f.setAmount(request.amount());f.setReason(request.reason());f.setCreatedDate(LocalDate.now());return repository.save(f);}
    public Fine pay(Long id){Fine f=findById(id);f.setStatus(Fine.PAID);return repository.save(f);}
    public void delete(Long id){repository.delete(findById(id));}
}

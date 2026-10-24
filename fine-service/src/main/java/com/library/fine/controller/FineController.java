package com.library.fine.controller;
import com.library.fine.dto.FineRequest; import com.library.fine.model.Fine; import com.library.fine.service.FineService;
import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/fines") @Tag(name="Fines",description="Gestion des amendes")
public class FineController {
    private final FineService service; public FineController(FineService s){service=s;}
    @GetMapping public List<Fine> getAll(){return service.findAll();}
    @GetMapping("/{id}") public Fine getById(@PathVariable Long id){return service.findById(id);}
    @GetMapping("/member/{memberId}") public List<Fine> getByMember(@PathVariable Long memberId){return service.findByMember(memberId);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Fine create(@Valid @RequestBody FineRequest r){return service.create(r);}
    @PutMapping("/{id}/pay") public Fine pay(@PathVariable Long id){return service.pay(id);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}

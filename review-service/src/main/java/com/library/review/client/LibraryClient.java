package com.library.review.client;
import com.library.review.exception.ResourceNotFoundException; import org.springframework.beans.factory.annotation.Value; import org.springframework.cloud.client.loadbalancer.LoadBalanced; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient; import org.springframework.web.client.RestClientResponseException;
@Component public class LibraryClient {
    private final RestClient books,members;
    public LibraryClient(@LoadBalanced RestClient.Builder builder,@Value("${services.book.url}")String b,@Value("${services.member.url}")String m){books=builder.baseUrl(b).build();members=builder.baseUrl(m).build();}
    public void verifyBook(Long id){verify(books,"/api/books/"+id,"Livre",id);} public void verifyMember(Long id){verify(members,"/api/members/"+id,"Membre",id);}
    private void verify(RestClient c,String u,String l,Long id){try{c.get().uri(u).retrieve().toBodilessEntity();}catch(RestClientResponseException e){if(e.getStatusCode().value()==404)throw new ResourceNotFoundException(l+" introuvable (id="+id+")");throw e;}}
}

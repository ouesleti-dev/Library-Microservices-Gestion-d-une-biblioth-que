package com.library.fine.client;
import com.library.fine.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
@Component
public class LibraryClient {
    private final RestClient members; private final RestClient borrowings;
    public LibraryClient(@LoadBalanced RestClient.Builder builder,
                         @Value("${services.member.url}") String memberUrl, @Value("${services.borrowing.url}") String borrowingUrl) {
        members=builder.baseUrl(memberUrl).build(); borrowings=builder.baseUrl(borrowingUrl).build();
    }
    public void verifyMember(Long id){verify(members,"/api/members/"+id,"Membre",id);}
    public void verifyBorrowing(Long id){verify(borrowings,"/api/borrowings/"+id,"Emprunt",id);}
    private void verify(RestClient c,String uri,String label,Long id){try{c.get().uri(uri).retrieve().toBodilessEntity();}catch(RestClientResponseException e){if(e.getStatusCode().value()==404)throw new ResourceNotFoundException(label+" introuvable (id="+id+")");throw e;}}
}

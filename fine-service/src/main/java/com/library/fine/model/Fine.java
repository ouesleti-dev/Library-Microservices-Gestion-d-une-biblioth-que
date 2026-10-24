package com.library.fine.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class Fine {
    public static final String UNPAID = "UNPAID";
    public static final String PAID = "PAID";
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long memberId;
    private Long borrowingId;
    private double amount;
    private String reason;
    private LocalDate createdDate;
    private String status = UNPAID;
    public Fine() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getMemberId(){return memberId;} public void setMemberId(Long v){memberId=v;}
    public Long getBorrowingId(){return borrowingId;} public void setBorrowingId(Long v){borrowingId=v;}
    public double getAmount(){return amount;} public void setAmount(double v){amount=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;}
    public LocalDate getCreatedDate(){return createdDate;} public void setCreatedDate(LocalDate v){createdDate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}

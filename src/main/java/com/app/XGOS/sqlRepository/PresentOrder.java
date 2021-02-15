package com.app.XGOS.sqlRepository;

import com.app.XGOS.sqlRepository.Wish;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "presentOrder")
public class PresentOrder {

    @Id
    @Column(name = "wish_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "wish_id")
    private Wish wish;

    private LocalDate date;

    public PresentOrder(LocalDate date) {
        this.date = date;
    }

    public PresentOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wish getWish() {
        return wish;
    }

    public void setWish(Wish wish) {
        this.wish = wish;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

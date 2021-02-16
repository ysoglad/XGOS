package com.app.XGOS.sqlRepository;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordersPublicData")
public class OrderPublicData {

    @Id
    @Column(name = "orderPrivateData_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "orderPrivateData_id")
    private OrderPrivateData orderPrivateData;

    private LocalDate date;

    public OrderPublicData(LocalDate date) {
        this.date = date;
    }

    public OrderPublicData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderPrivateData getOrderPrivateData() {
        return orderPrivateData;
    }

    public void setOrderPrivateData(OrderPrivateData orderPrivateData) {
        this.orderPrivateData = orderPrivateData;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

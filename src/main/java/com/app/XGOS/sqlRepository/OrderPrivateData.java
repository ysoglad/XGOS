package com.app.XGOS.sqlRepository;

import javax.persistence.*;

@Entity
@Table(name = "ordersPrivateData")
public class OrderPrivateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "orderPrivateData", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private OrderPublicData orderPublicData;

    String childName;
    String childSurname;
    String text;

    public OrderPrivateData(String childName, String childSurname, String text) {
        this.childName = childName;
        this.childSurname = childSurname;
        this.text = text;
    }

    public OrderPrivateData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderPublicData getOrderPublicData() {
        return orderPublicData;
    }

    public void setOrderPublicData(OrderPublicData orderPublicData) {
        this.orderPublicData = orderPublicData;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildSurname() {
        return childSurname;
    }

    public void setChildSurname(String childSurname) {
        this.childSurname = childSurname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

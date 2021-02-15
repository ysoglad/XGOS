package com.app.XGOS.sqlRepository;

import javax.persistence.*;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "wish", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PresentOrder presentOrder;

    String childName;
    String childSurname;
    String text;

    public Wish(String childName, String childSurname, String text) {
        this.childName = childName;
        this.childSurname = childSurname;
        this.text = text;
    }

    public Wish() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PresentOrder getOrder() {
        return presentOrder;
    }

    public void setOrder(PresentOrder presentOrder) {
        this.presentOrder = presentOrder;
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

package com.example.propagation.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    public Member(String username) {
        this.username = username;
    }

    public Member() {
    }

    public Member(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}

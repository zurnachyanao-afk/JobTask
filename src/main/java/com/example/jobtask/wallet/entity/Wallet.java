package com.example.jobtask.wallet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "wallet")
@NoArgsConstructor
public class Wallet {

    @Getter
    @Id
    private UUID id;

    @Getter
    @Column(nullable = false)
    private Long balance;


    @Version
    @Column(nullable = false)
    private Long version;

    private Wallet(UUID id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    public static Wallet create(UUID id) {
        return new Wallet(id, 0L);
    }


    public void deposit(long amount) {
        this.balance += amount;
    }

    public void withdraw(long amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance -= amount;
    }
}

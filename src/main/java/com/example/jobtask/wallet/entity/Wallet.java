package com.example.jobtask.wallet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "wallet")

public class Wallet {

    @Getter
    @Id
    private UUID id;

    @Getter
    private Long balance;


    @Version
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

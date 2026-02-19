package com.example.jobtask.wallet.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WalletResponse {

    private UUID walletId;
    private long balance;

    public WalletResponse(UUID walletId, long balance) {
        this.walletId = walletId;
        this.balance = balance;
    }
}
package com.example.jobtask.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WalletRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @Min(0)
    private long amount;

    public enum OperationType {
        DEPOSIT, WITHDRAW
    }

}

package com.example.jobtask.wallet.controller;


import com.example.jobtask.wallet.dto.WalletRequest;
import com.example.jobtask.wallet.dto.WalletResponse;
import com.example.jobtask.wallet.entity.Wallet;
import com.example.jobtask.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> updateWallet(@RequestBody @Valid WalletRequest request) {
        Wallet wallet;

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        switch (request.getOperationType()) {
            case DEPOSIT:
                wallet = walletService.deposit(request.getWalletId(), request.getAmount());
                break;
            case WITHDRAW:
                wallet = walletService.withdraw(request.getWalletId(), request.getAmount());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new WalletResponse(wallet.getId(), wallet.getBalance()));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId) {
        Wallet wallet = walletService.get(walletId);
        return ResponseEntity.ok(new WalletResponse(wallet.getId(), wallet.getBalance()));
    }
}
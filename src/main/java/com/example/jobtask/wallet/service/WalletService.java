package com.example.jobtask.wallet.service;

import com.example.jobtask.exception.InsufficientFundsException;
import com.example.jobtask.exception.WalletNotFoundException;
import com.example.jobtask.wallet.entity.Wallet;
import com.example.jobtask.wallet.config.WalletProperties;
import com.example.jobtask.wallet.repository.WalletRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final int maxRetries;

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletProperties properties) {
        this.walletRepository = walletRepository;
        this.maxRetries = properties.getMaxRetries();
    }

    @Transactional
    public Wallet deposit(UUID walletId, long amount) {
        return executeWithRetry(() -> {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseGet(() -> Wallet.create(walletId));

            wallet.deposit(amount);
            return walletRepository.save(wallet);
        });
    }

    @Transactional
    public Wallet withdraw(UUID walletId, long amount) {
        return executeWithRetry(() -> {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new WalletNotFoundException(walletId));

            try {
                wallet.withdraw(amount);
            } catch (IllegalStateException e) {
                throw new InsufficientFundsException();
            }

            return walletRepository.save(wallet);
        });
    }

    @Transactional(readOnly = true)
    public Wallet get(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }



    private Wallet executeWithRetry(SupplierWithException<Wallet> action) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return action.execute();
            } catch (OptimisticLockException e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Unreachable");
    }


    @FunctionalInterface
    private interface SupplierWithException<T> {
        T execute();
    }

}

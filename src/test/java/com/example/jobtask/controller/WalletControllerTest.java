package com.example.jobtask.controller;

import com.example.jobtask.exception.GlobalExceptionHandler;
import com.example.jobtask.exception.InsufficientFundsException;
import com.example.jobtask.exception.WalletNotFoundException;
import com.example.jobtask.wallet.controller.WalletController;
import com.example.jobtask.wallet.entity.Wallet;
import com.example.jobtask.wallet.service.WalletService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    // Успешное пополнение кошелька
    @Test
    void depositSuccess() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.create(walletId);
        wallet.deposit(1000L);

        given(walletService.deposit(walletId, 1000L)).willReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    // Успешное снятие денег
    @Test
    void withdrawSuccess() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.create(walletId);
        wallet.deposit(500L);

        given(walletService.withdraw(walletId, 500L)).willReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500));
    }

    // Снятие с недостатком средств
    @Test
    void withdrawInsufficientFunds() throws Exception {
        UUID walletId = UUID.randomUUID();

        doThrow(new InsufficientFundsException("Not enough money"))
                .when(walletService).withdraw(walletId, 2000L);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":2000}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Not enough money"));
    }

    // Попытка обращения к несуществующему кошельку
    @Test
    void getWalletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        doThrow(new WalletNotFoundException(walletId))
                .when(walletService).get(walletId);

        mockMvc.perform(get("/api/v1/wallet/" + walletId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found: " + walletId));
    }

    // Некорректный JSON
    @Test
    void invalidJson() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());
    }

    // Неизвестный тип операции
    @Test
    void unknownOperationType() throws Exception {
        UUID walletId = UUID.randomUUID();
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"TRANSFER\",\"amount\":100}"))
                .andExpect(status().isBadRequest());
    }

    // Сумма депозита или снятия отрицательная или нулевая
    @Test
    void negativeOrZeroAmount() throws Exception {
        UUID walletId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":-100}"))
                .andExpect(status().isBadRequest());
    }
}
package com.stream.payment_gpay.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountHolder;
    private Double balance;

    public Account(){}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (! Objects.equals(id, account.id)) return false;
        if (! Objects.equals(accountHolder, account.accountHolder))
            return false;
        return Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountHolder != null ? accountHolder.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountHolder='" + accountHolder + '\'' +
                ", balance=" + balance +
                '}';
    }
}

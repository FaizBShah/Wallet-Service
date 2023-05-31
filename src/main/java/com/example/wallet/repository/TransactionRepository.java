package com.example.wallet.repository;

import com.example.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM wallet_transactions WHERE from_wallet_id = :walletId OR to_wallet_id = :walletId", nativeQuery = true)
    public List<Transaction> getTransactionsByWalletId(@Param("walletId") Long walletId);

}

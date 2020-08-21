package com.pasta.mensadd.domain.balanceentry;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_balance_entries")
public class BalanceEntry {

    @PrimaryKey
    private long timestamp;
    private float cardBalance;
    private float lastTransaction;

    public BalanceEntry(long timestamp, float cardBalance, float lastTransaction) {
        this.timestamp = timestamp;
        this.cardBalance = cardBalance;
        this.lastTransaction = lastTransaction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getCardBalance() {
        return cardBalance;
    }

    public float getLastTransaction() {
        return lastTransaction;
    }
}

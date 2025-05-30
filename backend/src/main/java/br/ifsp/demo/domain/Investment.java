package br.ifsp.demo.domain;

import br.ifsp.demo.util.EffectiveWithdrawDateResolver;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Investment {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @Column(name = "initial_value")
    private double initialValue;
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    @Setter
    @Column(name = "withdraw_date")
    private LocalDate withdrawDate;
    @Setter
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    @Transient
    private EffectiveWithdrawDateResolver dateResolver;

    public Investment(double initialValue, Asset asset) {
        this.id = UUID.randomUUID();
        this.purchaseDate = LocalDate.now();
        verifyInvestment(initialValue, asset, purchaseDate);
        this.initialValue = initialValue;
        this.asset = asset;
        this.dateResolver = new EffectiveWithdrawDateResolver();
    }

    Investment(double initialValue, Asset asset, LocalDate purchaseDate) {
        this.id = UUID.randomUUID();
        verifyInvestment(initialValue, asset, purchaseDate);
        this.initialValue = initialValue;
        this.asset = asset;
        this.purchaseDate = purchaseDate;
        this.dateResolver = new EffectiveWithdrawDateResolver();
    }

     Investment(double initialValue, Asset asset, LocalDate purchaseDate, EffectiveWithdrawDateResolver resolver) {
        this.id = UUID.randomUUID();
        verifyInvestment(initialValue, asset, purchaseDate);
        this.initialValue = initialValue;
        this.asset = asset;
        this.purchaseDate = purchaseDate;
        this.dateResolver = resolver;
    }

    public Investment() {
        this.id = UUID.randomUUID();
        this.dateResolver = new EffectiveWithdrawDateResolver();
    }

    private void verifyInvestment(double initialValue, Asset asset, LocalDate purchaseDate){
        if (initialValue <= 0) throw new IllegalArgumentException("Initial value must be greater than zero");
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
        if (purchaseDate == null) throw new IllegalArgumentException("Purchase date cannot be null");
        if (purchaseDate.isAfter(asset.getMaturityDate())) throw new IllegalArgumentException("Purchase date cannot be after maturity date");
    }

    public boolean isWithdrawn() {
        return withdrawDate != null;
    }

    private double calculateBalanceAt(LocalDate date) {
        BigDecimal balance = BigDecimal.ZERO;

        long days = ChronoUnit.DAYS.between(this.purchaseDate, date);
        BigDecimal time = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(30.0), 10, RoundingMode.HALF_UP);

        BigDecimal initialValue = BigDecimal.valueOf(this.initialValue);
        BigDecimal profitability = BigDecimal.valueOf(this.asset.getProfitability()).add(BigDecimal.ONE);
        BigDecimal compound = BigDecimal.valueOf(
                Math.pow(profitability.doubleValue(), time.doubleValue())
        );

        BigDecimal finalValue = initialValue.multiply(compound);
        balance = balance.add(finalValue);
        return balance.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateCurrentBalance() {
        return calculateBalanceAt(dateResolver.resolve(withdrawDate));
    }

    public double calculateFutureBalance() {
        return calculateBalanceAt(asset.getMaturityDate());
    }

    @Override
    public String toString() {
        return "Initial value = R$ " + String.format("%.2f", initialValue) + " | " + asset.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Investment that = (Investment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

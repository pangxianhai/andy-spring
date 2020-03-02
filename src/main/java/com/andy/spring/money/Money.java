package com.andy.spring.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * money类型
 *
 * @author 庞先海 2019-11-14
 */
public class Money implements Serializable {

    private static final long serialVersionUID = 1113029833580708266L;

    public final static String DEFAULT_CURRENCY_CODE = "CNY";

    private final static int[] CENT_FACTORS = new int[] {1, 10, 100, 1000};

    /**
     * 分
     */
    private long cent;

    /**
     * 币种
     */
    private Currency currency;

    public Money() {
        this(0L);
    }

    public Money(long cent) {
        this.cent = cent;
        this.currency = Currency.getInstance(DEFAULT_CURRENCY_CODE);
    }

    public Money(long yuan, int cent) {
        this(yuan, cent, Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    public Money(long yuan, int cent, Currency currency) {
        this.currency = currency;
        this.cent = yuan * this.getCentFactor() + cent % this.getCentFactor();
    }

    public Money(String amount) {
        this(amount, Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    public Money(String amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    public Money(double amount) {
        this(amount, Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    public Money(double amount, Currency currency) {
        this.currency = currency;
        this.cent = Math.round(amount * this.getCentFactor());
    }

    public Money(BigDecimal amount) {
        this(amount, Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    public Money(BigDecimal amount, int roundingMode) {
        this(amount, Currency.getInstance(DEFAULT_CURRENCY_CODE), roundingMode);
    }

    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, 6);
    }

    public Money(BigDecimal amount, Currency currency, int roundingMode) {
        this.currency = currency;
        this.cent = this.rounding(amount.movePointRight(currency.getDefaultFractionDigits()), roundingMode);
    }

    protected long rounding(BigDecimal value, int roundingMode) {
        return value.setScale(0, roundingMode).longValue();
    }


    public int getCentFactor() {
        return CENT_FACTORS[this.currency.getDefaultFractionDigits()];
    }


    public long getCent() {
        return this.cent;
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(this.cent, this.currency.getDefaultFractionDigits());
    }

    @Override
    public String toString() {
        return this.getAmount().toString();
    }
}

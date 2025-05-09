package org.ttrader.util;

public enum CandlePeriod {
    SECONDS_10(10, 60 * 60/*1 hour*/, false),
    MINUTE_1(60, 6 * 60 * 60/*6 hours*/, false),
    HOUR_1(60 * 60, 7 * 24 * 60 * 60/*7 days*/, false),
    DAY_1(24 * 60 * 60, 6 * 30 * 24 * 60 * 60/*6 months*/, false),
    MONTH_1(30 * 24 * 60 * 60, -1, true);
    private final long unixPeriod;
    private final long liveTime;
    private final boolean unlimitedLiveTime;

    public static final CandlePeriod[] all = new CandlePeriod[] {
        SECONDS_10, MINUTE_1, HOUR_1, DAY_1, MONTH_1
    };

    public static final CandlePeriod[] allButFirst = new CandlePeriod[] {
        MINUTE_1, HOUR_1, DAY_1, MONTH_1
    };

    public static final CandlePeriod[] allButLast = new CandlePeriod[] {
        SECONDS_10, MINUTE_1, HOUR_1, DAY_1, MONTH_1
    };

    public static final long second = 1;
    public static final long minute = 60 * second;
    public static final long hour = 60 * minute;
    public static final long day = 24 * hour;
    public static final long week = 7 * day;
    public static final long month = 30 * day;

    CandlePeriod(long unixPeriod, long liveTime, boolean unlimitedLiveTime) {
        this.unixPeriod = unixPeriod;
        this.liveTime = liveTime;
        this.unlimitedLiveTime = unlimitedLiveTime;
    }

    public long getUnixPeriod() { return unixPeriod; }
    public long getLiveTime() { return liveTime; }
    public boolean isUnlimitedLiveTime() { return unlimitedLiveTime; }

    public CandlePeriod getNext() {
        return this.ordinal() == allButLast.length ? null : all[this.ordinal() + 1];
    }

    public CandlePeriod getPrev() {
        return this.ordinal() == 0 ? null : all[this.ordinal() - 1];
    }

    public CandlePeriod getNextSafe() {
        return this.ordinal() == allButLast.length ? MONTH_1 : all[this.ordinal() + 1];
    }

    public CandlePeriod getPrevSafe() {
        return this.ordinal() == 0 ? SECONDS_10 : all[this.ordinal() - 1];
    }

    public long normalize(long unixTime) { return normalize(unixTime, this.unixPeriod); }
    public static long normalize(long unixTime, long unixPeriod) {
        return unixTime - unixTime % unixPeriod;
    }
}

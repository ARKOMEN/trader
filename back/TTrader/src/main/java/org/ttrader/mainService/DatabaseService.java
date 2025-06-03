package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.analisys.CompanyDescriptor;
import org.ttrader.mainService.entities.*;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.mainService.repositories.NewsRepository;
import org.ttrader.mainService.repositories.TickerRepository;
import org.ttrader.newsUtil.NewsDescriptor;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("main-service")
public class DatabaseService {
    private final CandleRepository candleRepository;
    private final TickerRepository tickerRepository;
    private final NewsRepository newsRepository;

    private final Map<String, TickerEntity> tickers = new HashMap<>();

    private final Map<String, Map<Long, CandleEntityFull>> cache = new HashMap<>();

    private static final Map<String, CompanyDescriptor> companyMap = Arrays.stream(new CompanyDescriptor[] {
        new CompanyDescriptor("AAPL", "Apple Inc.", "https://www.nasdaq.com/market-activity/stocks/aapl"),
        new CompanyDescriptor("GOOGL", "Google Inc,", "https://www.nasdaq.com/market-activity/stocks/googl"),
        new CompanyDescriptor("NVDA", "NVIDIA Corp.", "https://www.nasdaq.com/market-activity/stocks/nvda"),
        new CompanyDescriptor("MSFT", "Microsoft Corp.", "https://www.nasdaq.com/market-activity/stocks/msft"),
        new CompanyDescriptor("AMZN", "Amazon Inc.", "https://www.nasdaq.com/market-activity/stocks/amzn"),
        new CompanyDescriptor("META", "Meta Inc.", "https://www.nasdaq.com/market-activity/stocks/meta"),
        new CompanyDescriptor("TSLA", "Tesla Inc.", "https://www.nasdaq.com/market-activity/stocks/tsla"),
        new CompanyDescriptor("NFLX", "Netflix Inc.", "https://www.nasdaq.com/market-activity/stocks/nflx"),
        new CompanyDescriptor("DIS", "Walt Disney Comp.", "https://www.nasdaq.com/market-activity/stocks/dis"),
        new CompanyDescriptor("INTC", "Intel Corp.", "https://www.nasdaq.com/market-activity/stocks/intc"),

        new CompanyDescriptor("OGKB", "Оптовая генерирующая компания №2 ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=OGKB"),
        new CompanyDescriptor("TGKA", "Территор. генерирующая компания № 1 ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=TGKA"),
        new CompanyDescriptor("TGKB", "Территор. генерирующая компания № 2 ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=TGKB"),
        new CompanyDescriptor("MRSB", "Мордовская энергосбытовая компания ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=TGKBP"),
        new CompanyDescriptor("KUZB", "Банк Кузнецкий ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=KUZB"),
        new CompanyDescriptor("CBOM", "МОСКОВСКИЙ КРЕДИТНЫЙ БАНК ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=CBOM"),
        new CompanyDescriptor("BANE", "АНК Башнефть ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=BANE"),
        new CompanyDescriptor("NAUK", "НПО Наука ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=NAUK"),
        new CompanyDescriptor("NLMK", "Новолипецкий металлургический комбинат ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=NLMK"),
        new CompanyDescriptor("MGNT", "Магнит ПАО", "https://www.moex.com/ru/issue.aspx?board=TQBR&code=BSPBP"),
    }).collect(Collectors.toMap(CompanyDescriptor::ticker, c -> c, (a, b) -> {throw new IllegalStateException("dublicating tickers");}));
//        Map.of(
//        "AAPL", new CompanyDescriptor("AAPL", "Apple Inc.", "https://www.nasdaq.com/market-activity/stocks/aapl"),
//        "GOOGL", new CompanyDescriptor("GOOGL", "Google Inc,", "https://www.nasdaq.com/market-activity/stocks/googl"),
//        "NVDA", new CompanyDescriptor("NVDA", "NVIDIA Corp.", "https://www.nasdaq.com/market-activity/stocks/nvda"),
//        "MSFT", new CompanyDescriptor("MSFT", "Microsoft Corp.", "https://www.nasdaq.com/market-activity/stocks/msft"),
//        "AMZN", new CompanyDescriptor("AMZN", "Amazon Inc.", "https://www.nasdaq.com/market-activity/stocks/amzn"),
//        "META", new CompanyDescriptor("META", "Meta Inc.", "https://www.nasdaq.com/market-activity/stocks/meta"),
//        "TSLA", new CompanyDescriptor("TSLA", "Tesla Inc.", "https://www.nasdaq.com/market-activity/stocks/tsla"),
//        "NFLX", new CompanyDescriptor("NFLX", "Netflix Inc.", "https://www.nasdaq.com/market-activity/stocks/nflx"),
//        "DIS", new CompanyDescriptor("DIS", "Walt Disney Comp.", "https://www.nasdaq.com/market-activity/stocks/dis"),
//        "INTC", new CompanyDescriptor("INTC", "Intel Corp.", "https://www.nasdaq.com/market-activity/stocks/intc"),
//        "OGKB",  new CompanyDescriptor("", "", "")
//    );

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public DatabaseService(CandleRepository candleRepository, TickerRepository tickerRepository, NewsRepository newsRepository) {
        this.candleRepository = candleRepository;
        this.tickerRepository = tickerRepository;
        this.newsRepository = newsRepository;
    }

    public void saveCandle(CandleEntity candle) { candleRepository.save(candle); }

    public void saveAllEntities(List<CandleEntity> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntity candle : candles) {
            candleRepository.save(candle);
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public void saveAllFunnies(List<? extends CandleEntityFull> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntityFull candle : candles) {
            candleRepository.save(candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getTicker(), candle.getTimestamp(), candle.getPeriod());
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public List<CandleEntityShort> getHistory(String ticker, long unit, long amount) {
        return candleRepository.findByTickerAndPeriodAndTimestampGreaterThan(
            tickers.get(ticker),
            unit,
            getCurrentTime() - unit * (amount + 2));
    }

    public long clearByTimestampAndPeriod(long timestamp, long period) {
        return candleRepository.deleteByTimestampLessThanAndPeriodLessThanEqual(timestamp, period);
    }

    public void informAboutTickers(Collection<String> tickers) {
        Map<String, TickerEntity> newTickers = tickers.stream().filter(ticker -> !tickers.contains(ticker)).collect(
            Collectors.toMap(ticker -> ticker, TickerEntity::new, (ticker1, ticker2) -> ticker1)
        );
        this.tickers.putAll(newTickers);
        newTickers.forEach((t, ticker) -> {
            cache.put(t, new HashMap<>());
            tickerRepository.save(ticker);
        });
    }
    public Set<String> getTickers() { return tickers.keySet(); }
    public Optional<CandleEntityFull> getCurrent(String ticker, long unit) {
        return Optional.ofNullable(cache.get(ticker).get(unit));
    }

    public CompanyDescriptor getCompany(String ticker) {
        return companyMap.get(ticker);
    }

    public void saveNews(Collection<NewsDescriptor> newsDescriptors) {
        newsDescriptors.forEach(
            newsDescriptor -> newsRepository.save(
                newsDescriptor.ticker(), newsDescriptor.title(), newsDescriptor.description(), newsDescriptor.url()
            )
        );
    }

    public List<NewsShort> getLastNews() {
        return newsRepository.findAll(
            Pageable.ofSize(20)
        );
    }

}

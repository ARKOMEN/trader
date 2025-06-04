package org.ttrader.mainService;

import jakarta.persistence.EntityManager;
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

    private final Set<String> allTickers;

    private final Map<String, Map<Long, CandleEntityFull>> cache;

    private final EntityManager entityManager;

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

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public DatabaseService(CandleRepository candleRepository, TickerRepository tickerRepository, NewsRepository newsRepository, EntityManager entityManager) {
        this.candleRepository = candleRepository;
        this.tickerRepository = tickerRepository;
        this.newsRepository = newsRepository;
        this.entityManager = entityManager;

        this.allTickers = tickerRepository.findAll().stream().map(TickerShort::getTicker).collect(Collectors.toSet());
        this.cache = this.allTickers.stream().collect(Collectors.toMap(t -> t, t -> new HashMap<>(), (a, b) -> null));
    }

    public synchronized void saveAllFunnies(List<? extends CandleEntityFull> candles) {
        informAboutTickers(candles.stream().map(CandleEntityFull::getTicker).toList());
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntityFull candle : candles) {
            TickerEntity ticker = getTickerReference(candle.getTicker());
            candleRepository.save(new CandleEntity(
                ticker,
                candle.getOpen(),
                candle.getHigh(),
                candle.getLow(),
                candle.getClose(),
                candle.getTimestamp(),
                candle.getPeriod()
            ));
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public synchronized List<CandleEntityShort> getHistory(String ticker, long unit, long amount) {
        return candleRepository.findByTickerAndPeriodAndTimestampGreaterThan(
            getTickerReference(ticker),
            unit,
            getCurrentTime() - unit * (amount + 2));
    }

    public synchronized long clearByTimestampAndPeriod(long timestamp, long period) {
        return candleRepository.deleteByTimestampLessThanAndPeriodLessThanEqual(timestamp, period);
    }

    public synchronized void informAboutTickers(Collection<String> tickers) {
        Map<String, TickerEntity> newTickers = tickers.stream()
            .filter(ticker -> !allTickers.contains(ticker))
            .collect(
            Collectors.toMap(ticker -> ticker, TickerEntity::new, (ticker1, ticker2) -> ticker1)
        );
        this.allTickers.addAll(newTickers.keySet());
        newTickers.forEach((t, ticker) -> {
            cache.put(t, new HashMap<>());
            tickerRepository.save(ticker);
        });
    }
    public synchronized Set<String> getAllTickers() { return allTickers; }
    public synchronized Optional<CandleEntityFull> getCurrent(String ticker, long unit) {
        return Optional.ofNullable(cache.get(ticker).get(unit));
    }

    public synchronized CompanyDescriptor getCompany(String ticker) {
        return companyMap.get(ticker);
    }

    public synchronized void saveNews(Collection<NewsDescriptor> newsDescriptors) {
        informAboutTickers(newsDescriptors.stream().map(NewsDescriptor::ticker).toList());
        newsDescriptors.forEach(
            newsDescriptor -> newsRepository.save(new NewsEntity(
                newsDescriptor.id(),
                newsDescriptor.timestamp(),
                getTickerReference(newsDescriptor.ticker()),
                cut(newsDescriptor.title()),
                cut(newsDescriptor.description()),
                cut(newsDescriptor.url())
            ))
        );
    }

    private static String cut(String text) {
        return text.length() > 255 ? text.substring(0,255) : text;
    }

    public synchronized List<NewsShort> getLastNews() {
        return newsRepository.findAllByOrderByTimeDesc(
            Pageable.ofSize(20)
        );
    }

    private synchronized TickerEntity getTickerReference(String ticker) {
        return entityManager.getReference(TickerEntity.class, ticker);
        //return new TickerEntity(ticker);
    }

    private synchronized NewsEntity getNewsReference(String ticker) {
        return entityManager.getReference(NewsEntity.class, ticker);
    }

    private synchronized CandleEntity getCandleReference(String ticker) {
        return entityManager.getReference(CandleEntity.class, ticker);
    }

}

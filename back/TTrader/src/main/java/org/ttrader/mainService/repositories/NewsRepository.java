package org.ttrader.mainService.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.ttrader.mainService.entities.NewsEntity;
import org.ttrader.mainService.entities.NewsShort;

import java.util.List;

@Profile("main-service")
public interface NewsRepository extends Repository<NewsEntity, Long> {
    NewsEntity save(NewsEntity newsEntity);

//    @Modifying
//    @Query("""
//        insert into NewsEntity (id, time, ticker, title, description, url)
//        values (:id, :time, :ticker, :title, :description, :url)
//        """)
//    void save(@Param("id") long id, @Param("time") long time,
//              @Param("ticker") String ticker, @Param("title") String title,
//              @Param("description") String description, @Param("url") String url);

    List<NewsShort> findAllByOrderByTimeDesc(Pageable pageable);

}

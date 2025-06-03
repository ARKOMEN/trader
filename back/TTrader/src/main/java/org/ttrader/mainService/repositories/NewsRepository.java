package org.ttrader.mainService.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.ttrader.mainService.entities.NewsEntity;
import org.ttrader.mainService.entities.NewsShort;

import java.util.List;

@Profile("main-service")
public interface NewsRepository extends Repository<NewsEntity, Long> {
    NewsEntity save(NewsEntity newsEntity);

    @Query("insert into NewsEntity (ticker, title, description, url) values (:ticker, :title, :description, :url)")
    void save(@Param("ticker") String ticker, @Param("title") String title,
              @Param("description") String description, @Param("url") String url);

    List<NewsShort> findAll(Pageable pageable);

}

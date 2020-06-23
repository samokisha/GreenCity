package greencity.repository;

import greencity.entity.EcoNews;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRepo extends JpaRepository<EcoNews, Long> {
    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM eco_news ORDER BY creation_date DESC LIMIT 3")
    List<EcoNews> getThreeLastEcoNews();

    /**
     * Method for getting three recommended eco news.
     *
     * @return list of three recommended {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value =
            "SELECT en.* FROM eco_news AS en "
                    + "WHERE en.id <> :openedEcoNewsId "
                    + "ORDER BY en.creation_date DESC LIMIT 3")
    List<EcoNews> getThreeRecommendedEcoNews(Long openedEcoNewsId);

    /**
     * Method for getting three recommended eco news for specific tags.
     *
     * @param tags            list of tags to search.
     * @param openedEcoNewsId id of opened eco news.
     * @return list of three or less recommended {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT en.* FROM eco_news AS en "
                    + "INNER JOIN eco_news_tags AS entag "
                    + "ON en.id = entag.eco_news_id "
                    + "INNER JOIN tags AS t ON entag.tags_id = t.id "
                    + "WHERE t.name IN (:tags) AND en.id <> :openedEcoNewsId "
                    + "ORDER BY en.creation_date DESC limit 3")
    List<EcoNews> getThreeRecommendedEcoNewsByTags(List<String> tags, Long openedEcoNewsId);

    /**
     * Method for getting three recommended eco news for specific tags.
     *
     * @param excludedEcoNewsIds list of excluded eco news ids.
     * @param limit              max number of instances
     * @return list of recommended {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT * FROM eco_news "
                    + "WHERE id NOT IN (:excludedEcoNewsIds) "
                    + "ORDER BY creation_date DESC limit :limit ")
    List<EcoNews> getRecommendedEcoNews(List<Long> excludedEcoNewsIds, Integer limit);

    /**
     * Method returns {@link EcoNews} for specific tags.
     *
     * @param tags list of tags to search.
     * @return {@link EcoNews} for specific tags.
     */
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT en.* FROM eco_news AS en "
                    + "INNER JOIN eco_news_tags AS entag "
                    + "ON en.id = entag.eco_news_id "
                    + "INNER JOIN tags AS t ON entag.tags_id = t.id "
                    + "WHERE t.name IN (:tags) "
                    + "ORDER BY  en.creation_date DESC")
    Page<EcoNews> find(Pageable pageable, List<String> tags);


    /**
     * Method returns all {@link EcoNews} by page.
     *
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    Page<EcoNews> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns {@link EcoNews} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNews}
     */
    @Query("select en from EcoNews as en "
            + "where lower(en.title) like lower(CONCAT('%', :searchQuery, '%')) "
            + "or lower(en.text) like lower(CONCAT('%', :searchQuery, '%')) "
            + "or en.id in (select en.id from EcoNews en inner join en.tags entags "
            + "where lower(entags.name) like lower(CONCAT('%', :searchQuery, '%')))")
    Page<EcoNews> searchEcoNews(Pageable pageable, String searchQuery);
}

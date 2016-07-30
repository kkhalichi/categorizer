package com.khalichi.categorizer.persistence.dao;

import java.util.List;
import com.khalichi.categorizer.persistence.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

/**
 * The customized stereotype of the category {@link CrudRepository} and {@link PagingAndSortingRepository} stereotypes, with restful
 * interface.
 * @author Keivan Khalichi
 */
@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
@Transactional
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>, CrudRepository<Category, Long> {

    /**
     * Find one category by name.
     * @param theCategoryName the category name to find
     * @return a category matching name
     */
    Category findOneByName(@Param("name") String theCategoryName);

    /**
     * Find count of categories matching name.  This number will never be greater than 1.
     * @param theCategoryName the category for which count is to be found
     * @return an long value of count of categories
     */
    Long countByName(@Param("name") String theCategoryName);

    /**
     * Finds all category names.
     * @return list of category names
     */
    @Query("select name from Category")
    List<String> findAllNames();

    /**
     * Finds all category objects.
     * @return list of categories
     */
    @Query("select c from Category c join fetch c.subcategories")
    List<Category> findAllWithChildren();

    /**
     * Finds all category names and number of subcategories for each, sorted by descending counts.
     * @return Tuple of category names and subcategory counts
     */
    @Query("select c.name, count(sc) as scc from Category c join c.subcategories sc group by c.name order by scc desc")
    List<Object[]> findCategoryAndCounts();
}

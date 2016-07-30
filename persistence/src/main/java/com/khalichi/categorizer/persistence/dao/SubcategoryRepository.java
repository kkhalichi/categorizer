package com.khalichi.categorizer.persistence.dao;

import java.util.List;
import javax.validation.constraints.NotNull;
import com.khalichi.categorizer.persistence.entity.Subcategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

/**
 * The customized stereotype of the sub-category {@link CrudRepository} and {@link PagingAndSortingRepository} stereotypes, with restful
 * interface.
 * @author Keivan Khalichi
 */
@RepositoryRestResource(collectionResourceRel = "subcategories", path = "subcategories")
@Transactional
public interface SubcategoryRepository extends PagingAndSortingRepository<Subcategory, Long>, CrudRepository<Subcategory, Long> {

    @Query("select sc from Subcategory sc where sc.name = :subcatName and sc.category.name = :catName")
    Subcategory findOneByNameAndCategory(@NotNull @Param("catName") final String theCategoryName,
                                         @NotNull @Param("subcatName")final String theSubcategoryName);

    @Query("select category.name, name from Subcategory")
    List<Object[]> findAllNames();
}

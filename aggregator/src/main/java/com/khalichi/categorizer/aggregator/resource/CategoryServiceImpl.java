package com.khalichi.categorizer.aggregator.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.khalichi.categorizer.aggregator.model.CategorizerModel;
import com.khalichi.categorizer.persistence.dao.CategoryRepository;
import com.khalichi.categorizer.persistence.dao.SubcategoryRepository;
import com.khalichi.categorizer.persistence.entity.Category;
import com.khalichi.categorizer.persistence.entity.Subcategory;
import com.khalichi.framework.rs.RESTUtils;
import com.khalichi.swagger.CustomSwagger2Feature;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the service, which is proxied and serviced by JAX-RS implementation.
 * @author Keivan Khalichi
 */
@Service("categoryService")
@Features(classes = CustomSwagger2Feature.class)
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private RESTUtils restUtils;

    @Value("${categorizer.default-categories}")
    private String defaultCategories;

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response addCategory(@NotNull final String theCategoryName) {
        Category aCategory;
        if (Strings.isNullOrEmpty(theCategoryName)) {
            return this.restUtils.warnNotModified("Null or empty category name.");
        }
        final Long aCategoryCount = this.categoryRepository.countByName(theCategoryName);
        if (aCategoryCount > 0) {
            return this.restUtils.warnNotModified("Category %s already exists.  Ignoring add.", theCategoryName);
        }
        aCategory = new Category(theCategoryName);
        this.categoryRepository.save(aCategory);
        return Response.ok().entity(aCategory).build();
    }

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response addSubcategory(@NotNull final String theCategoryName, @NotNull final String theSubcategoryName) {
        Subcategory aSubcategory;
        if (Strings.isNullOrEmpty(theSubcategoryName)) {
            return this.restUtils.warnNotModified("Null or empty subcategory name.");
        }
        final Category aCategory = this.categoryRepository.findOneByName(theCategoryName);
        if (aCategory == null) {
            return this.restUtils.warnNotModified("Category %s is not a valid category.", theCategoryName);
        }
        aSubcategory = this.subcategoryRepository.findOneByNameAndCategory(theCategoryName, theSubcategoryName);
        if (aSubcategory == null) {
            aSubcategory = new Subcategory(theSubcategoryName, aCategory);
            this.subcategoryRepository.save(aSubcategory);
        }
        else {
            return this.restUtils.warnNotModified("The category|subcategory of %s|%s is not unique.  Ignoring.", theCategoryName, theSubcategoryName);
        }
        return Response.ok().entity(aSubcategory).build();
    }

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response addSubcategories(@NotNull final List<CategorizerModel> theCatSubcatList) {
        if (CollectionUtils.isEmpty(theCatSubcatList)) {
            return this.restUtils.warnNotModified("List of categories|subcategories is empty or null.  Ignoring.");
        }
        // Inserting one pair of items at a time is not ideal, but ought to be sufficient for this exercise
        // Ideally, this method ought to accumulate list of subcategories, and bulk insert, but doing so requires getting the full list of
        // subcategories from the database, and checking each entry for duplication before assembling the bulk insert list
        final List<CategorizerModel> aModelList = new ArrayList<>(theCatSubcatList.size());
        for (final CategorizerModel aModel : theCatSubcatList) {
            final Response anAddResponse = this.addSubcategory(aModel.getCategory(), aModel.getSubcategory());
            if (anAddResponse.getStatus() == Status.OK.getStatusCode()) {
                final Subcategory aSubcategory = (Subcategory) anAddResponse.getEntity();
                aModelList.add(new CategorizerModel(aSubcategory.getCategory().getName(), aSubcategory.getName()));
            }
        }
        return Response.ok().entity(aModelList).build();
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findAllCategoryNames() {
        return this.categoryRepository.findAllNames();
    }

    /** {@inheritDoc} */
    @Override
    public List<Category> findAllCategories() {
        return this.categoryRepository.findAllWithChildren();
    }

    /** {@inheritDoc} */
    @Override
    public List<Subcategory> findAllSubcategories() {
        final Iterable<Subcategory> aSubcategories = this.subcategoryRepository.findAll();
        return Lists.newArrayList(aSubcategories);
    }

    @Override
    public List<Object[]> findAllCategorySubcategoryNames() {
        return this.subcategoryRepository.findAllNames();
    }

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response deleteCategory(@NotNull final String theCategoryName) {
        final Category aCategory = this.categoryRepository.findOneByName(theCategoryName);
        if (aCategory == null) {
            return this.restUtils.warnNotModified("Category %s not found.");
        }
        this.categoryRepository.delete(aCategory);
        return Response.noContent().build();
    }

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response deleteSubcategory(@NotNull final String theCategoryName, @NotNull final String theSubcategoryName) {
        Subcategory aSubcategory = this.subcategoryRepository.findOneByNameAndCategory(theCategoryName, theSubcategoryName);
        if (aSubcategory == null) {
            return this.restUtils.warnNotModified("The category|subcategory of %s|%s not found.  Ignoring.", theCategoryName, theSubcategoryName);
        }
        this.subcategoryRepository.delete(aSubcategory);
        return Response.noContent().build();
    }

    /** {@inheritDoc} */
    @Override
    @Modifying
    public Response deleteAllSubcategories() {
        this.subcategoryRepository.deleteAll();
        return Response.noContent().build();
    }

    /** {@inheritDoc} */
    @Override
    public Response dump() {
        final List<Subcategory> aSubcategories = this.findAllSubcategories();
        final List<CategorizerModel> aModelList = aSubcategories.stream().map(CategorizerModel::new).collect(Collectors.toList());
        final List<Object[]> aCategoryAndCountList = this.categoryRepository.findCategoryAndCounts();
        return Response.ok().entity(Arrays.asList(aModelList, aCategoryAndCountList)).build();
    }

    /**
     * Initializes list of categories based on configuration from application.properties.
     */
    @PostConstruct
    void initialize() {
        if (!Strings.isNullOrEmpty(this.defaultCategories)) {
            Iterable<String> aParsedCategories = Splitter.on(',').trimResults().omitEmptyStrings().split(this.defaultCategories);
            StreamSupport.stream(aParsedCategories.spliterator(), true).forEach(this::addCategory);
        }
    }
}
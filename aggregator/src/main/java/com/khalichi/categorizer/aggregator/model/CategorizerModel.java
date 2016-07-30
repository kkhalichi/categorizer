package com.khalichi.categorizer.aggregator.model;

import java.io.Serializable;
import com.khalichi.categorizer.persistence.entity.Subcategory;

/**
 * Model to hold a category/subcategories comination.
 * @author Keivan Khalichi
 */
public class CategorizerModel implements Serializable {

    private String category;
    private String subcategory;

    CategorizerModel() {
    }

    public CategorizerModel(final Subcategory theSubcategory) {
        category = theSubcategory.getCategory().getName();
        subcategory = theSubcategory.getName();
    }

    public CategorizerModel(final String theCategory, final String theSubcategory) {
        category = theCategory;
        subcategory = theSubcategory;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }
}

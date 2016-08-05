package com.khalichi.categorizer.aggregator.resource;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.khalichi.categorizer.aggregator.model.CategorizerModel;
import com.khalichi.categorizer.persistence.entity.Category;
import com.khalichi.categorizer.persistence.entity.Subcategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Categorizer REST service API interface.
 * @author Keivan Khalichi
 */
@Path("/categorizer")
@Produces(MediaType.APPLICATION_JSON)
@Api(value="/", description="APIs for categories and subcategories management")
public interface CategoryService {

    /**
     * Adds a category.  Duplicates are disallowed.
     * @param theCategoryName the category name
     * @return REST response
     */
    @POST
    @Path("/category")
    @ApiOperation("Adds a category.  Duplicates are disallowed.")
    Response addCategory(@NotNull @ApiParam(name = "cat", value = "Category") @QueryParam("cat") String theCategoryName);

    /**
     * Adds a subcategory.  Uniqueness of category/subcategory is enforced, and thus such duplicates are disallowed.
     * @param theCategoryName the category name
     * @param theSubcategoryName the subcategory name
     * @return REST response
     */
    @POST
    @Path("/subcategory")
    @ApiOperation("Adds a subcategory.  Uniqueness of category/subcategory is enforced, and thus such duplicates are disallowed.")
    Response addSubcategory(@NotNull @ApiParam(name = "cat", value = "Category") @QueryParam("cat") String theCategoryName,
                            @NotNull @ApiParam(name = "sub", value = "Subcategory") @QueryParam("sub") String theSubcategoryName);

    /**
     * Adds subcategories in bulk.
     * @param theCatSubcatList list of {@link CategorizerModel}
     * @return added list
     * @see #addSubcategories(List)
     */
    @POST
    @Path("/subcategory/bulk")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Adds subcategories in bulk.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list", value = "Category/subcategory list", dataType = "java.util.List", required = true, paramType = "body")
    })
    Response addSubcategories(@NotNull List<CategorizerModel> theCatSubcatList);

    /**
     * Finds all category names.
     * @return list of category names
     */
    @GET
    @Path("/category")
    @ApiOperation("Finds all category names.")
    List<String> findAllCategoryNames();

    /**
     * Finds all and returns {@link List}  of all categories.
     * @return list of {@link Category} entities
     */
    @GET
    @Path("/category/object")
    @ApiOperation("Finds all and returns list of all categories.")
    List<Category> findAllCategories();

    /**
     * Finds and returns {@link List} of all subcategories, with associated categories.
     * @return list of {@link Subcategory} entities.
     */
    @GET
    @Path("/subcategory/object")
    @ApiOperation("Finds and returns list of all subcategories, with associated categories.")
    List<Subcategory> findAllSubcategories();

    /**
     * Finds and returns list of cat/subcategory combinations.
     * @return list of {@link String}s
     */
    @GET
    @Path("/subcategory")
    @ApiOperation("Finds and returns list of cat/subcategory combinations.")
    List<Object[]> findAllCategorySubcategoryNames();

    /**
     * Deletes a category by name.
     * @param theCategoryName the category name
     * @return REST response
     */
    @DELETE
    @Path("/category/{cat}")
    @ApiOperation("Deletes a category by name.")
    Response deleteCategory(@NotNull  @ApiParam(name = "cat", value = "Category") @PathParam("cat") String theCategoryName);

    /**
     * Deletes a subcategory by name and category name.
     * @param theCategoryName the category name
     * @param theSubcategoryName the subcategory
     * @return REST response
     */
    @DELETE
    @Path("/subcategory/{cat}/{sub}")
    @ApiOperation("Deletes a subcategory by name and category name.")
    Response deleteSubcategory(@NotNull @ApiParam(name = "cat", value = "Category") @PathParam("cat") String theCategoryName,
                               @NotNull @ApiParam(name = "sub", value = "Subcategory") @PathParam("sub") String theSubcategoryName);

    /**
     * Deletes ALL subcategories, clearing the database.
     * @return REST response
     */
    @DELETE
    @Path("/subcategory")
    @ApiOperation("Deletes ALL subcategories, clearing the database.")
    Response deleteAllSubcategories();

    /**
     * Returns state of the system.  Return object has 2 rows; first is the list of subcategories, and second is the list of categories and count
     * of subcategories, sorted descending based on count.
     * @return
     */
    @GET
    @Path("/dump")
    @ApiOperation(  "Returns state of the system.  Return object has 2 rows; first is the list of subcategories, and second is the list of "
                  + "categories and count of subcategories, sorted descending based on count.")
    Response dump();
}

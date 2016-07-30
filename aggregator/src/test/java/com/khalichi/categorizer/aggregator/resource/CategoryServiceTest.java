package com.khalichi.categorizer.aggregator.resource;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khalichi.categorizer.aggregator.Aggregator;
import com.khalichi.categorizer.aggregator.model.CategorizerModel;
import org.apache.cxf.jaxrs.client.spring.EnableJaxRsProxyClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import static javax.ws.rs.core.Response.Status;
import static org.junit.Assert.*;

/**
 * Web integration test of REST services exposed by {@link CategoryService} implementation.
 * @author Keivan Khalichi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Aggregator.class)
@WebIntegrationTest
@EnableJaxRsProxyClient
public class CategoryServiceTest {

    /**
     * JAX-RS client proxy
     */
    @Autowired
    private CategoryService categoryService;

    @Value("${categorizer.test-file}")
    private Resource jsonData;

    @Before
    public void before() throws Exception {
        assertNotNull("Expected non-null data file resource, but is null.", this.jsonData);
        final File aDataFile;
        assertNotNull("Expected non-null data file, but is null.", aDataFile = this.jsonData.getFile());
        assertNotNull("Expected readable data file, but is not.", aDataFile.canRead());
        assertTrue("Expected file larger than 0 bytes, but is not.", aDataFile.length() > 0);
    }

    /**
     * Tests:
     *  <ul>
     *      <li>{@link CategoryService#addCategory}</li>
     *      <li>{@link CategoryService#findAllCategoryNames()}</li>
     *  </ul>
     * @throws Exception
     */
    @Test
    public void testAddCategory() {
        // Testing adding null category
        Response aResponse = this.categoryService.addCategory(null);
        assertEquals("Expected NOT_MODIFIED, received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());

        // Testing adding duplicate category
        aResponse = this.categoryService.addCategory("PERSON");
        assertEquals("Expected NOT_MODIFIED, received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());
        List<String> aCategoryNames = this.categoryService.findAllCategoryNames();
        assertEquals("Expected 1 category, but was other.", 1, aCategoryNames.stream().filter(c -> c.equals("PERSON")).count());

        // Testing adding a category and testing its existence in the list
        final String aCategoryName = "AUTOMOBILE";
        aResponse = this.categoryService.addCategory(aCategoryName);
        assertEquals("Expected OK, received other.", Status.OK.getStatusCode(), aResponse.getStatus());
        aCategoryNames = this.categoryService.findAllCategoryNames();
        assertTrue("Expected included category, but not there.", aCategoryNames.contains(aCategoryName));
    }

    /**
     * Tests:
     *  <ul>
     *      <li>{@link CategoryService#addSubcategory(String, String)}</li>
     *      <li>{@link CategoryService#findAllCategorySubcategoryNames()}</li>
     *  </ul>
     * @throws Exception
     */
    @Test
    public void testAddSubcategory() {
        // Testing adding a null subcategory
        this.addSubcategory("PERSON", null, Status.NOT_MODIFIED, 0);

        // Testing adding a subcategory
        final String aSubCategoryName = "Joe King";
        this.addSubcategory("PERSON", aSubCategoryName, Status.OK, 1);

        final List<Object[]> aCatSubcatNames = this.categoryService.findAllCategorySubcategoryNames();
        assertFalse(
                "Expected subcategory not in list, but it was.",
                aCatSubcatNames.stream().anyMatch(aRow -> Arrays.stream(aRow).anyMatch(aColumn -> aColumn.equals("Random Value")))
        );

        // Testing unique category/subcategory combination
        this.addSubcategory("PERSON", aSubCategoryName, Status.NOT_MODIFIED, 1);

        // Testing subcategory addition with invalid category
        this.addSubcategory("TREE", "Maple", Status.NOT_MODIFIED, 0);
    }

    /**
     * Tests {@link CategoryService#addSubcategories(List)}, which bulk loads list of category/subcategory combinations.
     */
    @Test
    public void testAddSubcategories() throws Exception {
        // Testing happy path
        final ObjectMapper anObjectMapper = new ObjectMapper();
        final CategorizerModel[] aCategorizerModels = anObjectMapper.readValue(jsonData.getFile(), CategorizerModel[].class);
        Response aResponse = this.categoryService.addSubcategories(Arrays.asList(aCategorizerModels));
        assertEquals("Expected OK, but received other.", Status.OK.getStatusCode(), aResponse.getStatus());

        // Testing with empty list
        aResponse = this.categoryService.addSubcategories(Collections.emptyList());
        assertEquals("Expected NOT_MODIFIED, but received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());
    }

    /**
     * Tests {@link CategoryService#deleteCategory(String)}.
     */
    @Test
    public void testDeleteCategory() {
        // Testing deleting a category and testing its existence in the list
        final String aCategoryName = "PERSON";
        Response aResponse = this.categoryService.deleteCategory(aCategoryName);
        assertEquals("Expected NO_CONTENT, but received other.", Status.NO_CONTENT.getStatusCode(), aResponse.getStatus());
        List<String> aCategoryNames = this.categoryService.findAllCategoryNames();
        assertFalse("Expected category not included, but it was.", aCategoryNames.contains(aCategoryName));

        aResponse = this.categoryService.deleteCategory(null);
        assertEquals("Expected NOT_MODIFIED, but received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());
    }

    /**
     * Tests {@link CategoryService#deleteSubcategory(String, String)}.
     */
    @Test
    public void testDeleteSubcategory() {
        final String aCategoryName = "PERSON";
        final String aSubategoryName = "Joe King";

        // Testing null parameter scenarios
        Response aResponse = this.categoryService.deleteSubcategory(null, aSubategoryName);
        assertEquals("Expected NOT_MODIFIED, but received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());

        aResponse = this.categoryService.deleteSubcategory(aCategoryName, null);
        assertEquals("Expected NOT_MODIFIED, but received other.", Status.NOT_MODIFIED.getStatusCode(), aResponse.getStatus());

        // Testing deleting a subcategory and testing its existence in the list
        // First add a category
        this.addSubcategory(aCategoryName, aSubategoryName, Status.OK, 1);
        // Now delete it
        aResponse = this.categoryService.deleteSubcategory(aCategoryName, aSubategoryName);
        assertEquals("Expected NO_CONTENT, but received other.", Status.NO_CONTENT.getStatusCode(), aResponse.getStatus());
        final List<Object[]> aCatSubcatNames = this.categoryService.findAllCategorySubcategoryNames();
        assertFalse(
                "Expected subcategory not included, but it was.",
                aCatSubcatNames.stream().anyMatch(aRow -> Arrays.stream(aRow).anyMatch(aColumn -> aColumn.equals(aSubategoryName)))
        );
    }

    /**
     * Tests {@link CategoryService#dump()}.
     */
    @Test
    public void testDump() {
        Response aResponse = this.categoryService.dump();
        assertEquals("Expected OK, but received other.", Status.OK.getStatusCode(), aResponse.getStatus());
        final List<Object> anEntitiesList = (List<Object>) aResponse.getEntity();
        assertFalse("Expected non-null/empty entity, but not so.", CollectionUtils.isEmpty(anEntitiesList));
        assertEquals("Expected 2 items in entity, but found other.", 2, anEntitiesList.size());
    }

    /**
     * Utility method that used in testing {@link CategoryService#addSubcategory(String, String)}.
     */
    private boolean addSubcategory(final String theCategoryName, final String theSubcategoryName,
                                   final Status theExpectedStatus, final int theExpectedCount) {
        final Response aResponse = this.categoryService.addSubcategory(theCategoryName, theSubcategoryName);
        assertEquals("Did not receive expected status code.", theExpectedStatus.getStatusCode(), aResponse.getStatus());
        final List<Object[]> aCatSubcatNames = this.categoryService.findAllCategorySubcategoryNames();
        if (theExpectedCount > 0) {
            assertNotNull("Expected a non-null, non-empty list, but was not.", CollectionUtils.isEmpty(aCatSubcatNames));
            assertTrue("Expected subcategory in list, but was not.",
                    aCatSubcatNames.stream().anyMatch(aRow -> Arrays.stream(aRow).anyMatch(aColumn -> aColumn.equals(theSubcategoryName)))
            );
        }
        assertEquals(
                String.format("Expected %d subcategory in list, but was other.", theExpectedCount),
                theExpectedCount,
                aCatSubcatNames.stream()
                               .filter(aRow -> Arrays.stream(aRow).filter(aColumn -> aColumn.equals(theSubcategoryName)).findFirst().isPresent())
                               .count()
        );
        return true;
    }
}
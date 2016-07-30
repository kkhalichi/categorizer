package com.khalichi.categorizer.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Category {@link Entity}.
 * @author Keivan Khalichi
 */
@Entity
@Table(name = "category")
public class Category implements Serializable {

    @Id
    @Column(name = "category_id")
    @GeneratedValue
    @JsonProperty
    private Long id;

    @Basic(optional = false)
    @Column(name = "category_name", unique = true, updatable = false, nullable = false)
    @JsonProperty
    private String name;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false, nullable = false)
    @JsonProperty
    private Date dateCreated;

    @Basic
    @Version
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", nullable = false)
    @GeneratedValue
    @JsonProperty
    private Date lastUpdated;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonProperty
    private List<Subcategory> subcategories;

    Category() {
    }

    public Category(final String theName) {
        this.name = theName;
        this.dateCreated = new Date();
    }

    public Long getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public void setName (final String theName) {
        name = theName;
    }

    public List<Subcategory> getSubcategories () {
        return subcategories;
    }

    public void setSubcategories (final List<Subcategory> theSubcategories) {
        subcategories = theSubcategories;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
}

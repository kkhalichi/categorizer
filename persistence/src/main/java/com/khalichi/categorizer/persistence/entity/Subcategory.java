package com.khalichi.categorizer.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Subcategory {@link Entity}.
 * @author Keivan Khalichi
 */
@Entity
@Table(name = "subcategory")
public class Subcategory implements Serializable {

    @GeneratedValue
    @Id
    @Column(name = "subcategory_id")
    @JsonProperty
    private Long id;

    @Basic(optional = false)
    @Column(name = "subcategory_name", updatable = false, nullable = false)
    @JsonProperty
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", updatable = false, nullable = false)
    @JsonIgnore
    private Category category;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false, nullable = false)
    @GeneratedValue
    @JsonProperty
    private Date dateCreated;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", nullable = false)
    @GeneratedValue
    @Version
    @JsonProperty
    private Date lastUpdated;

    Subcategory() {
    }

    public Subcategory(final String theName, final Category theCategory) {
        this.name = theName;
        this.category = theCategory;
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

    public String getCategoryName () {
        return category.getName();
    }

    @JsonIgnore
    public Category getCategory () {
        return category;
    }

    @JsonIgnore
    public void setCategory (final Category theCategory) {
        category = theCategory;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(final Object theObject) {
        if (this == theObject) {
            return true;
        }
        if (!(theObject instanceof Subcategory)) {
            return false;
        }
        final Subcategory anObject = (Subcategory) theObject;
        return Objects.equal(getName(), anObject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}

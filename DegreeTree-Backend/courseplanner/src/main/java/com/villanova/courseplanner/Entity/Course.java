package com.villanova.courseplanner.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Node("Course")
public class Course {
    @Id
    @GeneratedValue
    private Long id;

    private String courseCode;
    private String courseName;
    private int credits;
    private String courseDescription;

    @JsonIgnore // Prevent circular reference during JSON serialization
    @Relationship(type = "REFERS_TO", direction = Relationship.Direction.INCOMING)
    private CourseLeaf referredBy;

    public Course() {
    }

    public Course(Long id, String courseCode, String courseName, int credits, String courseDescription, CourseLeaf referredBy) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.courseDescription = courseDescription;
        this.referredBy = referredBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public CourseLeaf getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(CourseLeaf referredBy) {
        this.referredBy = referredBy;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", courseDescription='" + courseDescription + '\'' +
                '}';
    }
}

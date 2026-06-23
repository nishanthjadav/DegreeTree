package com.villanova.courseplanner.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Node("CourseLeaf")
public class CourseLeaf {
    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "REFERS_TO")
    private Course course;

    @JsonIgnore // Prevent circular reference in operator hierarchy
    @Relationship(type = "CHILD", direction = Relationship.Direction.INCOMING)
    private OperatorNode parentOperator;

    public CourseLeaf() {
    }

    public CourseLeaf(Long id, Course course, OperatorNode parentOperator) {
        this.id = id;
        this.course = course;
        this.parentOperator = parentOperator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public OperatorNode getParentOperator() {
        return parentOperator;
    }

    public void setParentOperator(OperatorNode parentOperator) {
        this.parentOperator = parentOperator;
    }

    @Override
    public String toString() {
        return "CourseLeaf{" +
                "id=" + id +
                ", course=" + (course != null ? course.getCourseCode() : "null") +
                '}';
    }
}

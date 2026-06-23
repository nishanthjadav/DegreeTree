package com.villanova.courseplanner.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Node("OperatorNode")
public class OperatorNode {
    @Id
    @GeneratedValue
    private Long id;

    private String type; // "AND" or "OR"

    @Relationship(type = "CHILD")
    private List<CourseLeaf> children;

    @JsonIgnore // Prevent circular reference in operator hierarchy
    @Relationship(type = "REQUIRES", direction = Relationship.Direction.INCOMING)
    private OperatorNode parentOperator;

    public OperatorNode() {
    }

    public OperatorNode(Long id, String type, List<CourseLeaf> children, OperatorNode parentOperator) {
        this.id = id;
        this.type = type;
        this.children = children;
        this.parentOperator = parentOperator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CourseLeaf> getChildren() {
        return children;
    }

    public void setChildren(List<CourseLeaf> children) {
        this.children = children;
    }

    public OperatorNode getParentOperator() {
        return parentOperator;
    }

    public void setParentOperator(OperatorNode parentOperator) {
        this.parentOperator = parentOperator;
    }

    @Override
    public String toString() {
        return "OperatorNode{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", children=" + (children != null ? children.size() : 0) + " items" +
                '}';
    }
}

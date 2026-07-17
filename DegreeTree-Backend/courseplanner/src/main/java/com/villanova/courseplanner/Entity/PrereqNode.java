package com.villanova.courseplanner.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * One node in a course's prerequisite tree. Modeled as a self-referencing
 * adjacency list: every node has at most one parent, so the parent link is the
 * only edge storage needed.
 *
 * node_type is one of:
 *   - "AND" / "OR": an operator node whose children must (all / at least one) be satisfied.
 *   - "LEAF": references a single prerequisite course via leafCourse.
 */
@Entity
@Table(name = "prereq_node")
public class PrereqNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The course whose prerequisite tree this node belongs to.
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    // Null for the root node of a course's tree.
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private PrereqNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrereqNode> children = new ArrayList<>();

    private String nodeType; // "AND" | "OR" | "LEAF"

    // Set only when nodeType = "LEAF".
    @ManyToOne
    @JoinColumn(name = "leaf_course_id")
    private Course leafCourse;

    public PrereqNode() {
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

    public PrereqNode getParent() {
        return parent;
    }

    public void setParent(PrereqNode parent) {
        this.parent = parent;
    }

    public List<PrereqNode> getChildren() {
        return children;
    }

    public void setChildren(List<PrereqNode> children) {
        this.children = children;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Course getLeafCourse() {
        return leafCourse;
    }

    public void setLeafCourse(Course leafCourse) {
        this.leafCourse = leafCourse;
    }

    public boolean isLeaf() {
        return "LEAF".equals(nodeType);
    }

    @Override
    public String toString() {
        return "PrereqNode{" +
                "id=" + id +
                ", nodeType='" + nodeType + '\'' +
                ", leafCourse=" + (leafCourse != null ? leafCourse.getCourseCode() : "null") +
                ", children=" + (children != null ? children.size() : 0) +
                '}';
    }
}

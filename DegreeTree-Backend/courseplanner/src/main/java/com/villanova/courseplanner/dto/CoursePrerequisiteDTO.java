package com.villanova.courseplanner.dto;

import com.villanova.courseplanner.Entity.Course;
import com.villanova.courseplanner.Entity.OperatorNode;

import java.util.List;

public class CoursePrerequisiteDTO
{
    private Course course;
    private OperatorNode root;
    private List<Course> prerequisites;

    public CoursePrerequisiteDTO() {
    }
    public CoursePrerequisiteDTO(Course course, OperatorNode root, List<Course> prerequisites) {
        this.course = course;
        this.root = root;
        this.prerequisites = prerequisites;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public OperatorNode getRoot() {
        return root;
    }

    public void setRoot(OperatorNode root) {
        this.root = root;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
}

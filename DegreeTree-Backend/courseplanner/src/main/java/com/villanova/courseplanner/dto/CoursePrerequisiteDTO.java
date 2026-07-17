package com.villanova.courseplanner.dto;

import com.villanova.courseplanner.Entity.Course;

import java.util.List;

public class CoursePrerequisiteDTO {
    private Course course;
    private List<Course> prerequisites;

    public CoursePrerequisiteDTO() {
    }

    public CoursePrerequisiteDTO(Course course, List<Course> prerequisites) {
        this.course = course;
        this.prerequisites = prerequisites;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
}

package com.villanova.courseplanner.Controller;

import com.villanova.courseplanner.Entity.Course;
import com.villanova.courseplanner.Service.CourseService;
import com.villanova.courseplanner.dto.CoursePrerequisiteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:4173"}) // Allow frontend ports
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourse(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{courseCode}")
    public ResponseEntity<Course> getCourseByCode(@PathVariable String courseCode) {
        Course course = courseService.getCourseByCode(courseCode.toUpperCase());
        return ResponseEntity.ok(course);
    }

    @GetMapping("/code/{courseCode}/prerequisites")
    public ResponseEntity<CoursePrerequisiteDTO> getCoursePrerequisites(@PathVariable String courseCode) {
        CoursePrerequisiteDTO results = courseService.getCoursePrerequisites(courseCode.toUpperCase());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/code/{courseCode}/prerequisite-tree")
    public ResponseEntity<Map<String, Object>> getPrerequisiteTree(@PathVariable String courseCode) {
        Map<String, Object> tree = courseService.getPrerequisiteTree(courseCode.toUpperCase());
        return ResponseEntity.ok(tree);
    }

}

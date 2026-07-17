package com.villanova.courseplanner.Service;

import com.villanova.courseplanner.Entity.Course;
import com.villanova.courseplanner.Entity.PrereqNode;
import com.villanova.courseplanner.Repository.CourseRepository;
import com.villanova.courseplanner.Repository.PrereqNodeRepository;
import com.villanova.courseplanner.dto.CoursePrerequisiteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private PrereqNodeRepository prereqRepo;

    public Course addCourse(Course course) {
        return courseRepo.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public Optional<Course> getCourse(Long id) {
        return courseRepo.findById(id);
    }

    public Course getCourseByCode(String courseCode) {
        return courseRepo.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseCode));
    }

    /**
     * Flat list of every prerequisite course referenced anywhere in the tree.
     */
    @Transactional(readOnly = true)
    public CoursePrerequisiteDTO getCoursePrerequisites(String courseCode) {
        Course course = getCourseByCode(courseCode);
        List<PrereqNode> nodes = prereqRepo.findByCourse_Id(course.getId());

        List<Course> prerequisites = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (PrereqNode node : nodes) {
            if (node.isLeaf() && node.getLeafCourse() != null) {
                Course leaf = node.getLeafCourse();
                if (seen.add(leaf.getCourseCode())) {
                    prerequisites.add(leaf);
                }
            }
        }
        return new CoursePrerequisiteDTO(course, prerequisites);
    }

    /**
     * Nested AND/OR tree in the shape the frontend consumes:
     *   operator node -> { type, children }
     *   leaf node     -> { courseCode, courseName }
     * Returns an empty map when the course has no prerequisites.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPrerequisiteTree(String courseCode) {
        Course course = getCourseByCode(courseCode);
        PrereqNode root = findRoot(course.getId());
        if (root == null) {
            return new HashMap<>();
        }
        return buildNode(root);
    }

    private PrereqNode findRoot(Long courseId) {
        List<PrereqNode> roots = prereqRepo.findByCourse_IdAndParentIsNull(courseId);
        return roots.isEmpty() ? null : roots.get(0);
    }

    private Map<String, Object> buildNode(PrereqNode node) {
        Map<String, Object> map = new HashMap<>();
        if (node.isLeaf()) {
            Course leaf = node.getLeafCourse();
            map.put("courseCode", leaf != null ? leaf.getCourseCode() : null);
            map.put("courseName", leaf != null ? leaf.getCourseName() : null);
        } else {
            map.put("type", node.getNodeType());
            List<Map<String, Object>> children = new ArrayList<>();
            for (PrereqNode child : node.getChildren()) {
                children.add(buildNode(child));
            }
            map.put("children", children);
        }
        return map;
    }

    /**
     * Given the courses a student has completed, return every course they are now
     * eligible to take: not already completed and with prerequisites satisfied.
     */
    @Transactional(readOnly = true)
    public List<Course> checkEligibility(List<String> completedCourses) {
        Set<String> completed = new HashSet<>(completedCourses == null ? List.of() : completedCourses);
        List<Course> eligible = new ArrayList<>();

        for (Course course : courseRepo.findAll()) {
            if (completed.contains(course.getCourseCode())) {
                continue;
            }
            PrereqNode root = findRoot(course.getId());
            if (isSatisfied(root, completed)) {
                eligible.add(course);
            }
        }
        return eligible;
    }

    private boolean isSatisfied(PrereqNode node, Set<String> completed) {
        if (node == null) {
            return true; // no prerequisites
        }
        if (node.isLeaf()) {
            Course leaf = node.getLeafCourse();
            return leaf != null && completed.contains(leaf.getCourseCode());
        }
        List<PrereqNode> children = node.getChildren();
        if ("OR".equals(node.getNodeType())) {
            return children.stream().anyMatch(child -> isSatisfied(child, completed));
        }
        // Default / "AND"
        return children.stream().allMatch(child -> isSatisfied(child, completed));
    }
}

package com.villanova.courseplanner.Service;

import com.villanova.courseplanner.Entity.*;
import com.villanova.courseplanner.Repository.CourseLeafRepository;
import com.villanova.courseplanner.Repository.CourseRepository;
import com.villanova.courseplanner.Repository.OperatorNodeRepository;
import com.villanova.courseplanner.dto.CoursePrerequisiteDTO;
import com.villanova.courseplanner.dto.PrerequisiteTreeNodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private CourseLeafRepository leafRepo;
    @Autowired
    private OperatorNodeRepository opRepo;

    public Course addCourse(Course course) {
        return courseRepo.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public Optional<Course> getCourse(Long code) {
        return courseRepo.findById(code);
    }

    public Course getCourseByCode(String courseCode) {
        return courseRepo.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseCode));
    }

    public CoursePrerequisiteDTO getCoursePrerequisites(String courseCode) {
        return courseRepo.findCoursePrerequisites(courseCode)
                .orElseThrow(() -> new RuntimeException("Course prerequisites not found for " + courseCode));
    }

    public Map<String, Object> getPrerequisiteTree(String courseCode) {
        try {
            List<PrerequisiteTreeNodeDTO> treeData = courseRepo.findPrerequisiteTreeStructure(courseCode);
            return buildPrerequisiteTree(treeData);
        } catch (Exception e) {
            // Fallback to strict structure
            CoursePrerequisiteDTO dto = getCoursePrerequisites(courseCode);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("type", "AND");
            List<Map<String, Object>> children = new ArrayList<>();
            for (Course prereq : dto.getPrerequisites()) {
                Map<String, Object> child = new HashMap<>();
                child.put("courseCode", prereq.getCourseCode());
                child.put("courseName", prereq.getCourseName());
                children.add(child);
            }
            fallback.put("children", children);
            return fallback;
        }
    }

    private Map<String, Object> buildPrerequisiteTree(List<PrerequisiteTreeNodeDTO> treeData) {
        Map<Long, Map<String, Object>> nodeMap = new HashMap<>();
        Set<Long> allNodeIds = new HashSet<>();
        Set<Long> childNodeIds = new HashSet<>();

        // First pass: build node map and track IDs
        for (PrerequisiteTreeNodeDTO nodeData : treeData) {
            Long nodeId = nodeData.getNodeId();
            allNodeIds.add(nodeId);

            Map<String, Object> node = new HashMap<>();
            String nodeLabel = nodeData.getNodeLabel();

            if ("OperatorNode".equals(nodeLabel)) {
                node.put("type", nodeData.getNodeType());
                node.put("children", new ArrayList<>());
            } else if ("CourseLeaf".equals(nodeLabel)) {
                node.put("courseCode", nodeData.getCourseCode());
            }

            nodeMap.put(nodeId, node);

            List<Long> childIds = nodeData.getChildIds();
            if (childIds != null) {
                childNodeIds.addAll(childIds);
            }
        }

        // Identify the root node (never appears as a child)
        allNodeIds.removeAll(childNodeIds);
        if (allNodeIds.size() != 1) {
            throw new IllegalStateException("Expected exactly one root node, found: " + allNodeIds.size());
        }

        Long rootId = allNodeIds.iterator().next();
        Map<String, Object> root = nodeMap.get(rootId);

        // Second pass: link parents to children
        for (PrerequisiteTreeNodeDTO nodeData : treeData) {
            Long nodeId = nodeData.getNodeId();
            List<Long> childIds = nodeData.getChildIds();

            Map<String, Object> node = nodeMap.get(nodeId);
            if (node.containsKey("children") && childIds != null) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
                for (Long childId : childIds) {
                    children.add(nodeMap.get(childId));
                }
            }
        }

        return root;
    }


}


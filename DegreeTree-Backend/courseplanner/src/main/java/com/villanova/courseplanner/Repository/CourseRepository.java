package com.villanova.courseplanner.Repository;

import com.villanova.courseplanner.Entity.Course;
import com.villanova.courseplanner.dto.CoursePrerequisiteDTO;
import com.villanova.courseplanner.dto.PrerequisiteTreeNodeDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends Neo4jRepository<Course, Long> {
    @Query("MATCH (c:Course {courseCode: $courseCode}) RETURN c")
    Optional<Course> findByCourseCode(String courseCode);

    @Query("""
    MATCH (c:Course {courseCode: $courseCode})-[:REQUIRES]->(root:OperatorNode)
    OPTIONAL MATCH path = (root)-[:CHILD*]->(leaf:CourseLeaf)-[:REFERS_TO]->(prereq:Course)
    RETURN c, root, collect(distinct prereq) as prerequisites
    """)
    Optional<CoursePrerequisiteDTO> findCoursePrerequisites(String courseCode);

    // @Query("""
    //         MATCH (c:Course {courseCode: $courseCode})-[:REQUIRES]->(root:OperatorNode)
    //         CALL {
    //             WITH root
    //             MATCH (root)-[:CHILD*0..]->(node)
    //             RETURN collect(node) as allNodes
    //         }
    //         UNWIND allNodes as node
    //         OPTIONAL MATCH (node)-[:CHILD]->(child)
    //         OPTIONAL MATCH (node)-[:REFERS_TO]->(course:Course) WHERE node:CourseLeaf
    //         RETURN\s
    //             id(node) as nodeId,
    //             CASE\s
    //                 WHEN node:OperatorNode THEN 'OperatorNode'\s
    //                 WHEN node:CourseLeaf THEN 'CourseLeaf'\s
    //                 ELSE null\s
    //             END as nodeLabel,
    //             CASE WHEN node:OperatorNode THEN node.type ELSE null END as nodeType,
    //             CASE WHEN node:CourseLeaf THEN course.courseCode ELSE null END as courseCode,
    //             collect(id(child)) as childIds

    //         """)
    @Query("""
MATCH (c:Course {courseCode: $courseCode})-[:REQUIRES]->(root:OperatorNode)
CALL (root) {
    WITH root
    MATCH (root)-[:CHILD*0..]->(node)
    RETURN collect(node) AS allNodes
}
UNWIND allNodes AS node
OPTIONAL MATCH (node)-[:CHILD]->(child)
OPTIONAL MATCH (node)-[:REFERS_TO]->(course:Course)
    WHERE node:CourseLeaf
RETURN
    id(node) AS nodeId,
    CASE
        WHEN node:OperatorNode THEN 'OperatorNode'
        WHEN node:CourseLeaf THEN 'CourseLeaf'
        ELSE null
    END AS nodeLabel,
    CASE
        WHEN node:OperatorNode THEN node.type
        ELSE null
    END AS nodeType,
    CASE
        WHEN node:CourseLeaf THEN course.courseCode
        ELSE null
    END AS courseCode,
    collect(id(child)) AS childIds


    """)
    List<PrerequisiteTreeNodeDTO> findPrerequisiteTreeStructure(String courseCode);
}

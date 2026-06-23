package com.villanova.courseplanner.Repository;

import com.villanova.courseplanner.Entity.CourseLeaf;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface CourseLeafRepository extends Neo4jRepository<CourseLeaf, Long> {
}

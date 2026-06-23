package com.villanova.courseplanner.Repository;

import com.villanova.courseplanner.Entity.OperatorNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface OperatorNodeRepository extends Neo4jRepository<OperatorNode, Long> {
}

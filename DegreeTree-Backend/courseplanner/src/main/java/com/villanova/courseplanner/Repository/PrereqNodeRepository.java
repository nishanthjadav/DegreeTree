package com.villanova.courseplanner.Repository;

import com.villanova.courseplanner.Entity.PrereqNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrereqNodeRepository extends JpaRepository<PrereqNode, Long> {
    // Loads every node in a course's prerequisite tree in one query.
    List<PrereqNode> findByCourse_Id(Long courseId);

    // The root node of a course's tree (the one with no parent).
    List<PrereqNode> findByCourse_IdAndParentIsNull(Long courseId);
}

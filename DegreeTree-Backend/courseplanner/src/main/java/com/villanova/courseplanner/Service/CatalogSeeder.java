package com.villanova.courseplanner.Service;

import com.villanova.courseplanner.Entity.Course;
import com.villanova.courseplanner.Entity.PrereqNode;
import com.villanova.courseplanner.Repository.CourseRepository;
import com.villanova.courseplanner.Repository.PrereqNodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Seeds the Villanova CS-major course catalog and prerequisite trees on first
 * startup (when the course table is empty). Course/prereq data was transcribed
 * from the individual course-detail pages at
 * https://live-villanova-catalog.cleancatalog.io/computing-sciences/
 *
 * Prerequisite trees are built with the small {@link Prereq} DSL below and
 * persisted as {@link PrereqNode} rows. Grade qualifiers (e.g. ":C", ":D-",
 * ":Y") from the catalog are dropped; only the course logic is modeled.
 */
@Component
public class CatalogSeeder implements CommandLineRunner {

    private final CourseRepository courseRepo;
    private final PrereqNodeRepository prereqRepo;

    public CatalogSeeder(CourseRepository courseRepo, PrereqNodeRepository prereqRepo) {
        this.courseRepo = courseRepo;
        this.prereqRepo = prereqRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepo.count() > 0) {
            return; // already seeded
        }

        Map<String, Course> courses = new LinkedHashMap<>();

        // --- Courses (code, name, credits) ---
        // Primary CSC major courses.
        add(courses, "CSC 1051", "Algorithms & Data Structures I", 4);
        add(courses, "CSC 1052", "Algorithms & Data Structures II", 4);
        add(courses, "CSC 1300", "Discrete Structures", 3);
        add(courses, "CSC 1700", "Analysis of Algorithms", 3);
        add(courses, "CSC 1800", "Organization of Programming Languages", 3);
        add(courses, "CSC 1990", "Enrichment Seminar in Computing", 1);
        add(courses, "CSC 2053", "Platform Based Computing", 3);
        add(courses, "CSC 2300", "Statistics for Computing", 3);
        add(courses, "CSC 2400", "Computer Systems", 3);
        add(courses, "CSC 2405", "Distributed Systems", 3);
        add(courses, "CSC 4170", "Theory of Computation", 3);
        add(courses, "CSC 4480", "Principles of Database Systems", 3);
        add(courses, "CSC 4500", "Artificial Intelligence", 3);
        add(courses, "CSC 4505", "Applied Machine Learning", 3);
        add(courses, "CSC 4550", "Computing for Data Science", 3);
        add(courses, "CSC 4700", "Software Engineering", 3);
        add(courses, "CSC 4790", "Senior Projects", 3);
        // Related required courses referenced by the major.
        add(courses, "MAT 1500", "Calculus I", 4);
        add(courses, "MAT 2400", "Linear Algebra for Computing", 4);
        add(courses, "PHI 2180", "Computer Ethics", 3);
        add(courses, "STAT 4310", "Statistical Methods", 3);
        // Courses referenced only as alternative prerequisites (kept so trees resolve).
        add(courses, "CSC 2014", "Fundamentals of Computer Science", 4);
        add(courses, "MAT 2600", "Discrete Mathematics", 3);
        add(courses, "MAT 4310", "Statistical Methods", 3);
        add(courses, "ECE 1260", "Digital Systems", 3);
        add(courses, "ECE 1620", "Programming for Engineers", 3);
        add(courses, "ECE 2160", "Embedded Systems", 3);
        add(courses, "ECE 2161", "Embedded Systems Lab", 1);
        add(courses, "ECE 2620", "Data Structures for Engineers", 3);

        courseRepo.saveAll(courses.values());

        // --- Prerequisite trees (exactly as scraped from the detail pages) ---
        seed(courses, "CSC 1052", or(leaf("CSC 1051"), leaf("CSC 2014")));

        seed(courses, "CSC 1700", and(
                or(leaf("CSC 1300"), leaf("MAT 2600")),
                or(leaf("CSC 1052"), leaf("ECE 2620"), leaf("ECE 2160"))));

        seed(courses, "CSC 1800", and(
                or(leaf("CSC 1300"), leaf("MAT 2600")),
                or(leaf("CSC 1052"), leaf("ECE 2620"), leaf("ECE 2160"))));

        seed(courses, "CSC 2053", or(
                leaf("CSC 1052"),
                leaf("ECE 2620"),
                and(leaf("ECE 2160"), leaf("ECE 2161"))));

        seed(courses, "CSC 2300", and(leaf("CSC 1051"), leaf("CSC 1300")));

        seed(courses, "CSC 2400", and(
                leaf("CSC 1052"),
                or(leaf("CSC 1300"), leaf("MAT 2600"))));

        seed(courses, "CSC 2405", and(
                leaf("CSC 1052"),
                or(leaf("CSC 1300"), leaf("MAT 2600"))));

        seed(courses, "CSC 4170", leaf("CSC 1700"));

        seed(courses, "CSC 4480", and(
                or(leaf("CSC 1051"), leaf("ECE 1620"), leaf("ECE 1260")),
                or(leaf("CSC 1300"), leaf("MAT 2600"))));

        // "(CSC 1300 or MAT 2600) and (CSC 1052 or ECE 2620) or (ECE 2160 and ECE 2161)"
        // AND binds tighter than OR, so the first two groups are ANDed together,
        // then OR'd with the (ECE 2160 and ECE 2161) group.
        seed(courses, "CSC 4500", or(
                and(
                        or(leaf("CSC 1300"), leaf("MAT 2600")),
                        or(leaf("CSC 1052"), leaf("ECE 2620"))),
                and(leaf("ECE 2160"), leaf("ECE 2161"))));

        seed(courses, "CSC 4505", and(
                or(leaf("CSC 1052"), leaf("ECE 2620")),
                or(leaf("CSC 1300"), leaf("MAT 2600")),
                or(leaf("CSC 2300"), leaf("MAT 4310"), leaf("STAT 4310")),
                leaf("MAT 2400")));

        seed(courses, "CSC 4550", and(
                or(leaf("CSC 1300"), leaf("MAT 2600")),
                or(leaf("CSC 1052"), leaf("ECE 2620"), leaf("ECE 2160"))));

        seed(courses, "CSC 4700", leaf("CSC 2053"));

        seed(courses, "CSC 4790", leaf("CSC 4700"));
    }

    private void add(Map<String, Course> courses, String code, String name, int credits) {
        Course c = new Course();
        c.setCourseCode(code);
        c.setCourseName(name);
        c.setCredits(credits);
        courses.put(code, c);
    }

    /**
     * Persist a prerequisite tree for the given course, resolving leaf course
     * codes to the saved Course entities.
     */
    private void seed(Map<String, Course> courses, String courseCode, Prereq spec) {
        Course owner = courses.get(courseCode);
        PrereqNode root = toNode(spec, owner, courses);
        prereqRepo.save(root); // cascades to children
    }

    private PrereqNode toNode(Prereq spec, Course owner, Map<String, Course> courses) {
        PrereqNode node = new PrereqNode();
        node.setCourse(owner);
        if (spec.leafCode != null) {
            node.setNodeType("LEAF");
            Course leaf = courses.get(spec.leafCode);
            if (leaf == null) {
                throw new IllegalStateException("Unknown prerequisite course: " + spec.leafCode);
            }
            node.setLeafCourse(leaf);
        } else {
            node.setNodeType(spec.type);
            List<PrereqNode> children = new ArrayList<>();
            for (Prereq child : spec.children) {
                PrereqNode childNode = toNode(child, owner, courses);
                childNode.setParent(node);
                children.add(childNode);
            }
            node.setChildren(children);
        }
        return node;
    }

    // --- Tiny DSL for describing prerequisite trees ---
    private static final class Prereq {
        String type;         // "AND" | "OR" for operators
        String leafCode;     // set for leaves
        List<Prereq> children;
    }

    private static Prereq leaf(String code) {
        Prereq p = new Prereq();
        p.leafCode = code;
        return p;
    }

    private static Prereq and(Prereq... children) {
        return op("AND", children);
    }

    private static Prereq or(Prereq... children) {
        return op("OR", children);
    }

    private static Prereq op(String type, Prereq... children) {
        Prereq p = new Prereq();
        p.type = type;
        p.children = List.of(children);
        return p;
    }
}

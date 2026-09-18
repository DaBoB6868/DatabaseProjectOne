/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.util.List;

import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.
        
       /* EXAMPLE FOR ME
        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("Col01_Name", "Col02_Name", "Col03_Name"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("/path/to/exported/csv_file");
        rel1.print();
*/
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData("data/instructor_export.csv");
        System.out.print("MY MYID = slh56040");
        // instructor.print();
        Relation student = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "tot_cred"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        student.loadData("data/student_export.csv");
        System.out.print("MY MYID = slh56040");
        // student.print();




        Relation course = new RelationBuilder()
                .attributeNames(List.of("course_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        course.loadData("data/course_export.csv");

        uga.csx370.mydb.RA ra = new RAImpl();
        System.out.println("Query: Departments that have students, instructors, and courses.");
        Relation result = ra.intersect(
                ra.intersect(
                        ra.project(student, List.of("dept_name")),
                        ra.project(instructor, List.of("dept_name"))),
                ra.project(course, List.of("dept_name")));
        result.print();

        // ---- Query by Jackson Cross (jrc69565) ----
        System.out.println();
        System.out.println("Query: Departments that have students and courses, but no instructors.");
        Relation studentsAndCourses = ra.intersect(
                ra.project(student, List.of("dept_name")),
                ra.project(course, List.of("dept_name")));
        Relation noInstructor = ra.diff(studentsAndCourses,
                ra.project(instructor, List.of("dept_name")));
        Relation labeled = ra.rename(noInstructor,
                List.of("dept_name"), List.of("dept_without_instructors"));
        labeled.print();

        // ---- Query by Ethan Harding (eth63510) ----
        System.out.println();
        System.out.println("Query: Courses with more than 4 credit hours with an instructor earning more than $100,000.");
        Relation instructorCourses = ra.join(instructor, course);
        Relation highEarnersOnBigCourses = ra.select(instructorCourses, row -> {
            double salary = row.get(instructorCourses.getAttrIndex("salary")).getAsDouble();
            int credits = row.get(instructorCourses.getAttrIndex("credits")).getAsInt();
            return salary > 100000 && credits == 4;
        });
        Relation report = ra.project(highEarnersOnBigCourses,
                List.of("name", "dept_name", "title", "credits", "salary"));
        report.print();
        // ---- Query by Cameron Carson (clc60551) ----
        System.out.println();
        System.out.println("Query: Instructors in departments that offer 4-credit courses and have students.");

        Relation fourCreditCourses = ra.select(course, row -> {
        int credits = row.get(course.getAttrIndex("credits")).getAsInt();
        return credits == 4;
    });

        Relation fourCreditDepts = ra.project(
        fourCreditCourses,
        List.of("dept_name")
    );

        Relation studentDepts = ra.project(
        student,
        List.of("dept_name")
    );

        Relation qualifyingDepts = ra.intersect(
        fourCreditDepts,
        studentDepts
    );

        Relation matchingInstructors = ra.join(
        instructor,
        qualifyingDepts
    );

        Relation instructorReport = ra.project(
        matchingInstructors,
        List.of("ID", "name", "dept_name", "salary")
    );

    instructorReport.print();

    // ---- Query by Spencer Hicks (slh56040) ----
        System.out.println();
        System.out.println("Query: Students whose names start with M in departments that offer courses and have an instructor earning over $110,000.");
        Relation highPaidInstructors = ra.select(instructor, row -> {
            double salary = row.get(instructor.getAttrIndex("salary")).getAsDouble();
            return salary > 110000;
        });
        Relation courseDeptsWithHighPaidInstructors = ra.intersect(
                ra.project(course, List.of("dept_name")),
                ra.project(highPaidInstructors, List.of("dept_name")));
        Relation matchingStudents = ra.join(student, courseDeptsWithHighPaidInstructors);
        Relation studentsStartingWithM = ra.select(matchingStudents, row -> {
            String name = row.get(matchingStudents.getAttrIndex("name")).getAsString();
            return name.startsWith("M");
        });
        Relation studentReport = ra.project(studentsStartingWithM, List.of("ID", "name", "dept_name"));
        studentReport.print();
    }
}

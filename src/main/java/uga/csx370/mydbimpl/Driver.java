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
        instructor.loadData("C:\\Users\\spenc\\OneDrive\\Documentos\\DataBaseManagement\\exportedDatabases\\mysql-files\\instructor_export.csv");
        System.out.print("MY MYID = slh56040");
        instructor.print();
        Relation student = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "tot_cred"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        student.loadData("C:\\Users\\spenc\\OneDrive\\Documentos\\DataBaseManagement\\exportedDatabases\\mysql-files\\student_export.csv");
        System.out.print("MY MYID = slh56040");
        student.print();




        Relation course = new RelationBuilder()
                .attributeNames(List.of("course_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        course.loadData("PATH\\course_export.csv");

        uga.csx370.mydb.RA ra = new RAImpl();
        System.out.println("Query: Departments that have students, instructors, and courses.");
        Relation result = ra.intersect(
                ra.intersect(
                        ra.project(student, List.of("dept_name")),
                        ra.project(instructor, List.of("dept_name"))),
                ra.project(course, List.of("dept_name")));
        result.print();
    }

}

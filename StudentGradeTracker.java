//**TASK 1: Student Grade Tracker** 
//Build a Java program to input and manage student grades. 
//Calculate average, highest, and lowest scores. 
//Use arrays or ArrayLists to store and manage data. 
//Display a summary report of all students. 
//Make the interface console-based or GUI-based as desired.

import java.util.ArrayList;
import java.util.Scanner;

// Class to represent an individual student
class Student {
    String name;
    double score;

    public Student(String name, double score) {
        this.name = name;
        this.score = score;
    }
}

public class StudentGradeTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();
        int choice;

        do {
            System.out.println("\n==============================");
            System.out.println("   STUDENT GRADE TRACKER      ");
            System.out.println("==============================");
            System.out.println("1. Add Student and Grade");
            System.out.println("2. Display Summary Report");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");

            // Input validation for menu choice
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Please enter a number (1-3): ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    System.out.print("\nEnter student name: ");
                    String name = scanner.nextLine();
                    
                    double score = -1;
                    while (true) {
                        System.out.print("Enter student score (0 - 100): ");
                        if (scanner.hasNextDouble()) {
                            score = scanner.nextDouble();
                            scanner.nextLine(); // Consume newline
                            if (score >= 0 && score <= 100) {
                                break;
                            }
                        } else {
                            scanner.next(); // Clear invalid input
                        }
                        System.out.println("Invalid score. Please enter a value between 0 and 100.");
                    }

                    students.add(new Student(name, score));
                    System.out.println("✔ Student added successfully!");
                    break;

                case 2:
                    if (students.isEmpty()) {
                        System.out.println("\nNo student records found. Please add students first.");
                        break;
                    }

                    double sum = 0;
                    double highest = students.get(0).score;
                    double lowest = students.get(0).score;
                    String highestStudent = students.get(0).name;
                    String lowestStudent = students.get(0).name;

                    System.out.println("\n----------------------------------");
                    System.out.println("          SUMMARY REPORT          ");
                    System.out.println("----------------------------------");
                    System.out.printf("%-20s | %s\n", "Student Name", "Score");
                    System.out.println("----------------------------------");

                    for (Student s : students) {
                        System.out.printf("%-20s | %.2f\n", s.name, s.score);
                        sum += s.score;

                        if (s.score > highest) {
                            highest = s.score;
                            highestStudent = s.name;
                        }
                        if (s.score < lowest) {
                            lowest = s.score;
                            lowestStudent = s.name;
                        }
                    }

                    double average = sum / students.size();

                    System.out.println("----------------------------------");
                    System.out.println("Total Students : " + students.size());
                    System.out.printf("Average Score  : %.2f\n", average);
                    System.out.printf("Highest Score  : %.2f (%s)\n", highest, highestStudent);
                    System.out.printf("Lowest Score   : %.2f (%s)\n", lowest, lowestStudent);
                    System.out.println("----------------------------------");
                    break;

                case 3:
                    System.out.println("\nExiting Student Grade Tracker. Goodbye!");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please select an option between 1 and 3.");
            }
        } while (choice != 3);

        scanner.close();
    }
}
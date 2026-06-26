package com.xxw.studentproject;
import com.xxw.studentproject.entity.Student;
import com.xxw.studentproject.service.StudentService;
import java.util.List;
import java.util.Scanner;


public class StudentProjectApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();

    public static void main(String[] args) {
        while(true){
            showMenu();
            int chioce = readInt();

            switch (chioce){
                case 1 -> addStudent();
                case 2 -> inputScore();
                case 3 -> listStudent();
                case 4 -> showRanking();
                case 5 -> deleteStudent();
                case 6 -> searchStudents();
                case 7 -> exitSystem();
                default -> System.out.println("输入无效，请输入正确数字");
            }
        }

    }

    private static void showMenu(){
        System.out.println("=====学生成绩管理系统=====");
        System.out.println("1.添加学生");
        System.out.println("2. 录入成绩");
        System.out.println("3. 查看所有学生");
        System.out.println("4. 按总分排名");
        System.out.println("5. 删除学生");
        System.out.println("6. 搜索学生");
        System.out.println("7. 退出");
        System.out.println("============================");
    }


    //================1.添加学生================
    private static void addStudent(){
        System.out.println("请输入学号：");
        String sno = scanner.nextLine().strip();
        if(sno.isEmpty()){
            System.out.println("输入无效");
            return;
        }

        System.out.println("请输入姓名：");
        String sname = scanner.nextLine().strip();
        if(sname.isEmpty()){
            System.out.println("输入无效");
            return;
        }

        Boolean success = studentService.addStudent(sno, sname);

        if(success){
            System.out.println("添加成功");
        }else{
            System.out.println("该学号已存在");
        }
    }

    //================2.录入成绩================
    private static void inputScore(){
        System.out.println("请输入学生学号：");
        String sno = scanner.nextLine().strip();
        Boolean existStudent = studentService.findStudentSuccess(sno);

        if(!existStudent){
            System.out.println("未找到该学生");
            return;
        }

        System.out.println("请输入语文成绩：");
        Double chineseScored = readDouble();
        System.out.println("请输入数学成绩：");
        Double mathScored = readDouble();
        System.out.println("请输入英语成绩：");
        Double englishScored = readDouble();

        studentService.inputScore(sno,chineseScored, mathScored, englishScored);
    }

    //================3.查看所有学生成绩================
    private static void listStudent(){
        studentService.listStudents();
    }

    //================4.按总分排名================
    private static void showRanking(){
        List<Student> rankStudent = studentService.rankByTotalScore();

        System.out.println("====== 总分排名 ======");
        int rank = 1;
        for (Student s : rankStudent){
            System.out.println(rank + "." + s.toString());
            rank++;
        }
    }

    //================5.删除学生================
    private static void deleteStudent(){
        System.out.println("请输入学号：");
        String sno = scanner.nextLine().strip();
        studentService.deleteStudent(sno);
    }

    //================6.搜索学生================
    private static void searchStudents(){
        System.out.println("请输入学号或姓名关键字");
        String keyWord = scanner.nextLine().strip();

        List<Student> students = studentService.searchStudents(keyWord);

        if(students.isEmpty()){
            System.out.println("未找到合适的学生");
            return;
        }

        System.out.println("学号       姓名       语文   数学   英语   总分   平均分");
        System.out.println("----------------------------------------------------------");
        for (Student stu : students) {
            System.out.println(stu.toString());
        }
    }


    //退出系统
    private static void exitSystem(){
        System.out.println("数据已保存，再见！");
        scanner.close();
        System.exit(0);
    }


    //读取输入整形的数字
    private static Integer readInt(){
        while (true){
            if(scanner.hasNextInt()){
                int n = scanner.nextInt();
                scanner.nextLine();
                return n;
            }
            scanner.nextLine();
            System.out.println("请输入数字：");
        }
    }

    //读取输入Double形的数字
    private static Double readDouble(){
        while (true){
            if(scanner.hasNextDouble()){
                Double score = scanner.nextDouble();
                scanner.nextLine();

                if(score >= 0 && score <= 100)
                    return score;
            }else{
                scanner.nextLine();
            }
            System.out.println("请输入0~100的有效数字：");
        }
    }
}

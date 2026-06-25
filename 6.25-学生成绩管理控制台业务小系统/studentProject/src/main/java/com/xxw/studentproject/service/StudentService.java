package com.xxw.studentproject.service;

import com.xxw.studentproject.entity.Student;
import com.xxw.studentproject.utils.FileIOUtils;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

    //添加学生，返回是否添加成功
    public Boolean addStudent(String sno, String sname) {
        Boolean studentSnoDuplicate = findStudentSuccess(sno);
        if(studentSnoDuplicate){
            return false;
        }

        Student newStudent = new Student(sno, sname);
        FileIOUtils.writeFile(newStudent);
        return true;
    }

    //查找学生学号是否存在
    public static Boolean findStudentSuccess(String sno){
        List<Student> students = FileIOUtils.readFile();
        for(Student s : students){
            if(s.getSno().equals(sno))
                return true;
        }
        return false;
    }

    //录入学生三科成绩并传入到文件
    public void inputScore(String sno, Double chineseScored, Double mathScored, Double englishScored) {
        List<Student> students = FileIOUtils.readFile();

        for(Student s : students) {
            if (s.getSno().equals(sno)){
                s.setChineseScord(chineseScored);
                s.setMathScord(mathScored);
                s.setEnglishScord(englishScored);
                FileIOUtils.writeAll(students);
            }
        }

    }

    //返回所有学生信息，没有就null(直接打印出来)
    public void listStudents() {
        List<Student> allStudent = FileIOUtils.readFile();
        if(allStudent == null || allStudent.size() == 0){
            System.out.println("暂无数据");
            return;
        }

        System.out.println("学号       姓名       语文   数学   英语   总分   平均分");
        System.out.println("----------------------------------------------------------");

        for (Student s : allStudent){
            String studentprint = s.toString();
            System.out.println(studentprint);
        }
    }

    //按总分进行排名
    public List<Student> rankByTotalScore(){
        List<Student> rankStudent = FileIOUtils.readFile();

        rankStudent.sort((s1,s2)->
                Double.compare(s1.getSumScord(),s2.getSumScord())
                );

        return rankStudent;
    }

    //删除学生
    public void deleteStudent(String sno){
        List<Student> students = FileIOUtils.readFile();
        Boolean studentExists = students.removeIf(s -> s.getSno().equals(sno));

        if(!studentExists){
            System.out.println("未找到");
            return;
        }

        FileIOUtils.writeAll(students);
        System.out.println("已删除");
    }



    //根据sno查找学生
    public Student searchStudentBySno(String sno){
        List<Student> students = FileIOUtils.readFile();
        for(Student s : students){
            if(s.getSno().equals(sno))
                return s;
        }
        return null;
    }

    //根据sno或姓名关键字查找List
    public List<Student> searchStudents(String keyWord) {
        //判断keyWord是否为空
        if(keyWord == null || keyWord.strip().isEmpty()){
            return new ArrayList<>();
        }

        List<Student> allStudent = FileIOUtils.readFile();
        List<Student> students = new ArrayList<>();

        for (Student s : allStudent){
            if(s.getSno().equals(keyWord) || s.getSname().contains(keyWord)){
                students.add(s);
            }
        }

        return students;
    }

}

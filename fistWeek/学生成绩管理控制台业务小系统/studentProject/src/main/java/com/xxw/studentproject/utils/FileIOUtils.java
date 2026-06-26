package com.xxw.studentproject.utils;
import com.xxw.studentproject.entity.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileIOUtils {



    //读取文件
    public static List<Student> readFile(){
        List<Student> result = new ArrayList<>();


        //创建一个字符输入流
        try(
                FileReader fl = new FileReader("student.data");
                BufferedReader bfr = new BufferedReader(fl);
        ) {
            //读取信息并转化为实体存入List
            String line;
            while ((line = bfr.readLine()) != null){
                Student student = Student.parse(line);
                result.add(student);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    //写入文件
    public static void writeFile(Student student){
        String studentString = student.toString();

        //创建字符输出流写入数据
        try(
                FileWriter fw = new FileWriter("student.data",true);
                BufferedWriter bfw = new BufferedWriter(fw);
                ) {

            //将Student的String写入文件，一次一行
            bfw.write(studentString);
            bfw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //覆盖写入文件
    public static void writeAll(List<Student> students){

        //创建字符输出流写入数据
        try(
                FileWriter fw = new FileWriter("student.data",false);
                BufferedWriter bfw = new BufferedWriter(fw);
        ) {
            for (Student s : students){
                bfw.write(s.toString());
                bfw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

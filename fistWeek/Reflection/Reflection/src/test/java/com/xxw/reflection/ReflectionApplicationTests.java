package com.xxw.reflection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpringBootTest

class ReflectionApplicationTests {

    @Test
    void reflectionTest1() {
        Class c = Student.class;
        System.out.println(c.getName());
        System.out.println(c.getSimpleName());

        Student s = new Student();
        Class c2 = s.getClass();
        System.out.println(c == c2);
    }


    //获取构造器
    @Test
    void reflectionTest2() throws Exception {
        Class s = Student.class;


        //发现两次获取的对象实例不一样
        Constructor[] cs = s.getDeclaredConstructors();
        for (Constructor cc : cs){
            System.out.println(cc.getName() + "--> " + cc.getParameterAnnotations());
        }

        //1.获取无参构造器
        Constructor c1 = s.getDeclaredConstructor();
        System.out.println(c1.getName() + "--> " + c1.getParameterAnnotations());
        c1.setAccessible(true);
        Student s1 = (Student) c1.newInstance();
        System.out.println(s1.toString());

        //2.获取有参构造器
        Constructor c2 = s.getDeclaredConstructor(Integer.class, String.class);
        System.out.println(c2.getName() + "--> " + c2.getParameterAnnotations());
        c2.setAccessible(true);
        Student s2 = (Student) c2.newInstance(18,"张三");
        System.out.println(s2.toString());
    }


    //获取类的成员变量
    @Test
    void reflectionTest3() throws NoSuchFieldException, IllegalAccessException {
        Class s = Student.class;

        Field[] fs = s.getDeclaredFields();
        for (Field f : fs){
            System.out.println(f.getName() + "-->" + f.getType());
        }

        //获取id并打印
        Field fId = s.getDeclaredField("id");
        System.out.println(fId.getName() + "-->" + fId.getType());

        //获取name并打印
        Field fName = s.getDeclaredField("name");
        System.out.println(fName.getName() + "-->" + fName.getType());

        //赋值
        Student student = new Student();
        fId.setAccessible(true);
        fId.set(student,22);
        fName.setAccessible(true);
        fName.set(student,"李四");
        System.out.println(student.toString());

        //取值
        String name = (String) fName.get(student);
        System.out.println(name);
    }


    //获取类的成员方法
    @Test
    void reflectionTest4() throws Exception {
        Class s = Student.class;

        Method[] ms = s.getDeclaredMethods();
        for (Method m : ms){
            System.out.println(m.getName() + "-->" + m.getParameterAnnotations()
                +"-->" + m.getReturnType());
        }

        Student student = new Student();

        //获取无参
        Method run = s.getDeclaredMethod("run");
        run.setAccessible(true);
        Object rs = run.invoke(student);
        System.out.println(rs);

        //获取有参
        Method eat = s.getDeclaredMethod("eat",String.class);
        eat.setAccessible(true);
        Object rs2 = eat.invoke(student, "apple");
        System.out.println(rs2);
    }


}

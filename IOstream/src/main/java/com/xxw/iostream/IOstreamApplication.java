package com.xxw.iostream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class IOstreamApplication {

/*    一次读取多个字节
    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/a.txt");
        InputStream inputStream = new FileInputStream(file);
        int len;
        byte[] buffer = new byte[3];
        while ((len = inputStream.read(buffer)) != -1){
            String rs = new String(buffer, 0, len);
            System.out.println(rs);
        }
    }*/

/*    运用realAllBytes一次性读取所有字符
    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/a.txt");
        InputStream inputStream = new FileInputStream(file);

        byte[] bytes = inputStream.readAllBytes();
        String rs = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(rs);

        inputStream.close();
    }*/

//      输出字节流与输入字节流的简单运用
//    public static void main(String[] args) throws Exception {
//        File file = new File("src/main/resources/a.txt");
//        OutputStream os = new FileOutputStream(file,true);
//
//        os.write(97);
//        os.write('b');
//        byte[] bytes = "\n你好Java".getBytes(StandardCharsets.UTF_8);
//        os.write(bytes);
//
//        InputStream is = new FileInputStream(file);
//        byte[] bytes1 = is.readAllBytes();
//        String rs = new String(bytes1, StandardCharsets.UTF_8);
//        System.out.println(rs);
//
//        os.close();
//        is.close();
//    }


    //运用输入输出流复制
//    public static void main(String[] args) throws Exception {
//        File file = new File("src/main/resources/a.txt");
//        InputStream is = new FileInputStream(file);
//
//        byte[] bytes = is.readAllBytes();
//
//        File file2 = new File("src/main/resources/b.txt");
//
//        OutputStream os = new FileOutputStream(file2);
//        os.write(bytes);
//
//        is.close();
//        os.close();
//    }


    //   字符输入流读取
//    public static void main(String[] args) throws Exception {
//        FileReader fr = new FileReader("src/main/resources/a.txt");
//
////        int a;
////        while ( (a = fr.read()) != -1){
////            System.out.println((char)a);
////        }
//
//        char[] buffer = new char[3];
//        int len;
//        while ((len = fr.read(buffer)) != -1){
//            String sr = new String(buffer);
//            System.out.println(sr);
//        }
//
//        fr.close();
//    }


//       字符流写入并读取
//    public static void main(String[] args) throws Exception {
//        FileWriter fileWriter = new FileWriter("src/main/resources/b.txt",true);
//
//        String rs= new String("你好Java");
//        fileWriter.write(rs);
//        fileWriter.close();
//
//        FileReader fl = new FileReader("src/main/resources/b.txt");
//        char[] chars = new char[3];
//        int len;
//        while((len = fl.read(chars)) != -1){
//            String s = new String(chars);
//            System.out.println(s);
//        }
//
//        fl.close();
//    }



    /*字节缓冲流读取*/
//public static void main(String[] args) throws Exception {
//    try(
//            InputStream is = new FileInputStream("src/main/resources/a.txt");
//            InputStream bis = new BufferedInputStream(is);
//            ){
//        byte[] buffer = new byte[1024];
//        int len;
//        while((len = bis.read(buffer)) != -1){
//            System.out.println(new String(buffer,0,len,StandardCharsets.UTF_8));
//        }
//        System.out.println("复制完成");
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    }
//}


        /*字符缓冲流读取整行*/
//    public static void main(String[] args) throws Exception {
//        Reader fr = new FileReader("src/main/resources/a.txt");
//        BufferedReader br = new BufferedReader(fr);
//        String line = new String();
//        while ((line = br.readLine()) != null){
//            System.out.println(line);
//        }
//    }

    public static void main(String[] args) throws Exception {


        Size size = Size.LARGE;
        String size1 = size.getSize();
        System.out.println(size.compareTo(Size.EXTRA_LARGE));

        String input = "EXTRA_LARGE";
        Size s = Size.valueOf(input);
        System.out.println(s.toString());
        System.out.println(s.getSize());
    }
}






















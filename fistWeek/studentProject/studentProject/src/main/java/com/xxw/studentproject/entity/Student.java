package com.xxw.studentproject.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {
    private String sno;
    private String sname;
    private Double chineseScord;
    private Double mathScord;
    private Double englishScord;

    public Student(String sno, String sname){
        this.sno = sno;
        this.sname = sname;
        this.chineseScord = 0.0;
        this.mathScord = 0.0;
        this.englishScord = 0.0;
    }


    public double getSumScord() {
        return chineseScord + mathScord + englishScord;
    }

    public double getAvgScord() {
        return getSumScord() / 3;
    }

    //将实体类转换成String(存)
    public String toString() {
        return sno + " " + sname + " " + chineseScord + " " + mathScord + " " + englishScord + " " + getSumScord() + " " + getAvgScord();
    }

    //将文本读取的String转换成实体类(读)
    public static Student parse(String line) {
        String[] fields = line.split(" ");
        if(fields.length != 7){
            throw  new IllegalArgumentException("数据格式错误");
        }
        return new Student(
                fields[0].strip(),
                fields[1].strip(),
                Double.parseDouble(fields[2].strip()),
                Double.parseDouble(fields[3].strip()),
                Double.parseDouble(fields[4].strip())
        );
    }
}

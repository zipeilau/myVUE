package com.alan.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("t_course")
public class Course implements Serializable{
    private String id;
    private String name;         //课程名
    @TableField("course_type")
    private Integer courseType;  //课程类型
    private Double credit;       //学分
    @TableField("sign_stime")
    private String signStime;    //报名开始时间
    @TableField("sign_etime")
    private String signEtime;    //报名结束时间
    @TableField("exam_stime")
    private String examStime;    //考试开始时间
    @TableField("exam_etime")
    private String examEtime;   //考试结束时间
    @TableField(exist = false)
    private String courseTypeName; //课程类型名称
    @TableField(exist = false)
    private String teachersName;   //任课老师，可能有多个
    @TableField(exist = false)
    private Integer studentNum;    //选课人数
    @TableField(exist = false)
    private String[] teacherIds;   //任课老师id
    @TableField(exist = false)
    private String[] ids;          //id集合，可以是课程的id，也可能是课程-老师中间表的id
    @TableField(exist = false)
    private Double score;          //得分
    @TableField(exist = false)
    private Integer status;      //老师是否录入了成绩：1是，0否
    @TableField(exist = false)
    private String isCanDelete;  //是否可以退订已选课程（还在选课期间就可以退订，暂时未做）
}

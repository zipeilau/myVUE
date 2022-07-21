package com.alan.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.alan.pojo.Grade;
import com.alan.service.GradeService;
import com.alan.utils.EnumCode;
import com.alan.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/grade")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    //老师：查看某门课程的学生，参数id为课程-老师中间表的id
    @GetMapping("/viewStudent")
    public Object viewStudent(Integer startPage, Integer pageSize,String id){
        Page<Grade> page = new Page<>(startPage,pageSize);
        List<Grade> list = gradeService.viewStudent(page,id);
        return ResultUtil.result(EnumCode.OK.getValue(),"请求成功",list,page.getTotal());
    }

    //老师：给某个学生的某门课程打分
    @PostMapping("/update")
    public Object updateScore(Grade grade){
       return gradeService.updateScore(grade);
    }
}

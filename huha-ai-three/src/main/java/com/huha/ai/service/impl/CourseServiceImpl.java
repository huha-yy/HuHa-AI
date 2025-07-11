package com.huha.ai.service.impl;

import com.huha.ai.service.ICourseService;
import com.huha.ai.entity.po.Course;
import com.huha.ai.mapper.CourseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 学科表 服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}

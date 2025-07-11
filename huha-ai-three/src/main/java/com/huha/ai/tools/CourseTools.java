package com.huha.ai.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.huha.ai.entity.po.Course;
import com.huha.ai.entity.po.CourseReservation;
import com.huha.ai.entity.po.School;
import com.huha.ai.entity.query.CourseQuery;
import com.huha.ai.service.ICourseReservationService;
import com.huha.ai.service.ICourseService;
import com.huha.ai.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CourseTools {

    private final ICourseService courseService;
    private final ISchoolService schoolService;
    private final ICourseReservationService reservationService;

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourse(@ToolParam(description = "查询的条件", required = false) CourseQuery query) {
        if (query == null){
            return List.of();
//            return courseService.list();
        }
        QueryChainWrapper<Course> wrapper = courseService.query()
                .eq(query.getType() != null, "type", query.getType())//type = 编程
                .le(query.getEdu() != null, "edu", query.getEdu());//edu <= 2
        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true,sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }


    @Tool(description = "查询所有学校校区")
    public List<School> querySchools() {
        return schoolService.list();
    }

    @Tool(description = "生成预约单，创建课程预约")
    public Integer createCourseReservation(@ToolParam(description = "预约课程") String course,
                                           @ToolParam(description = "预约校区") String school,
                                           @ToolParam(description = "学生姓名") String studentName,
                                           @ToolParam(description = "联系电话") String contactInfo,
                                           @ToolParam(description = "备注") String remark) {
        CourseReservation reservation = new CourseReservation();
        reservation.setCourse(course);
        reservation.setSchool(school);
        reservation.setStudentName(studentName);
        reservation.setContactInfo(contactInfo);
        reservation.setRemark(remark);
        reservationService.save(reservation);
        return reservation.getId();
    }
}
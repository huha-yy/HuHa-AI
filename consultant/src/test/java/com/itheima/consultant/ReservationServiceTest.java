package com.itheima.consultant;

import com.itheima.consultant.pojo.Reservation;
import com.itheima.consultant.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;
    //测试添加
    @Test
    void testInsert(){
        Reservation reservation = new Reservation(null, "小王", "男", "13800000001", LocalDateTime.now(), "上海", 580);
        reservationService.insert(reservation);
    }
    //测试查询
    @Test
    void testFindByPhone(){
        String phone = "13800000001";
        Reservation reservation = reservationService.findByPhone(phone);
        System.out.println(reservation);
    }
}
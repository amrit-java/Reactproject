package com.exam.repository;


import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exam.model.Captcha;



public interface CaptchaRepository extends JpaRepository<Captcha, String> {

    @Query(value = "SELECT COUNT(*) FROM captcha_text WHERE id = :id AND captcha = :captcha", nativeQuery = true)
    BigInteger getcaptcha(@Param("captcha") String captcha, @Param("id") String id);
}

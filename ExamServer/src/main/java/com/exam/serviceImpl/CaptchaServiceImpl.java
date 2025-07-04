package com.exam.serviceImpl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.repository.CaptchaRepository;
import com.exam.service.CaptchaService;

@Service
public class CaptchaServiceImpl implements CaptchaService {

	@Autowired
	private CaptchaRepository captchaRepository;

	@Override
	public boolean getcaptcha(String captchaInput, String id) {
		BigInteger count = captchaRepository.getcaptcha(captchaInput, id);
		return count.intValue() == 1;
	}
}

package com.ssafy.api.service;

import com.ssafy.api.request.UserIdEmailReq;
import com.ssafy.api.request.UserRegisterPostReq;
import com.ssafy.db.entity.User;
import org.springframework.mail.MailSender;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface UserService {
	User createUser(UserRegisterPostReq userRegisterInfo);
	User getUserByUserId(Long userId);
	User getUserById(String Id);

	User modifyUser(UserRegisterPostReq userRegisterInfo);

	boolean checkUserId(String id);

	boolean sendMail(UserIdEmailReq userIE);
}
package org.best.backspringboot.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[좋은변화] 비밀번호 재설정 안내");
        message.setText(
            "안녕하세요, 좋은변화입니다.\n\n" +
            "아래 링크를 클릭하여 비밀번호를 재설정해 주세요.\n" +
            "이 링크는 30분간만 유효합니다.\n\n" +
            resetLink + "\n\n" +
            "만약 본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다."
        );
        mailSender.send(message);
    }
}
package br.com.marcio.financecontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacao(String emailDestino, String tokenRecuperacao) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(emailDestino);

        mensagem.setSubject("Recuperação de Senha - Finance Control");

        mensagem.setText("Olá!\n\n" +
                "Você solicitou a recuperação da sua senha.\n" +
                "Utilize o código abaixo para cadastrar uma nova senha:\n\n" +
                tokenRecuperacao + "\n\n" +
                "Se não foi você, apenas ignore este e-mail.");

        mailSender.send(mensagem);
    }
}
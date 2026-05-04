package com.ewallet.auth_service.listner;

import com.ewallet.auth_service.config.RabbitMQConfig;
import com.ewallet.auth_service.event.UserPublishEvent;
import com.ewallet.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPublishListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.USER_TO_AUTH_QUEUE)
    public void onUserRegistered(UserPublishEvent event){
        log.info("Auth Service received event for userId: {}", event.getUserId());
        userRepository.deleteById(event.getUserId());
        log.info("User with id {} deleted from auth service", event.getUserId());
    }
}

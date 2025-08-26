package com.example.demo.common.infrastructure;

import com.example.demo.common.service.port.UUIDProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SystemUUIDProvider implements UUIDProvider {

    @Override
    public String random() {
        return UUID.randomUUID().toString();
    }

}

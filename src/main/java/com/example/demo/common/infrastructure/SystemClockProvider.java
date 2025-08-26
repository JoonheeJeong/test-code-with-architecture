package com.example.demo.common.infrastructure;

import com.example.demo.common.service.port.ClockProvider;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class SystemClockProvider implements ClockProvider {

    @Override
    public long nowMillis() {
        return Clock.systemUTC().millis();
    }

}

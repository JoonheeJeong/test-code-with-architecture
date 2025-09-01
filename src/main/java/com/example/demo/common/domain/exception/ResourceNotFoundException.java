package com.example.demo.common.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String datasource, String field, Object value) {
        super("%s 에서 %s %s 을(를) 찾을 수 없습니다.".formatted(datasource, field, value.toString()));
    }
}

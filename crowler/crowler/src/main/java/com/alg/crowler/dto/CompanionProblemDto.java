package com.alg.crowler.dto;
import java.util.List;

public record CompanionProblemDto(
        String name,
        String group,
        String url,
        int memoryLimit,   // 단일 숫자를 바로 받도록 수정
        float timeLimit,   // 단일 숫자를 바로 받도록 수정
        List<TestCase> tests
) {
    public record TestCase(String input, String output) {}
}
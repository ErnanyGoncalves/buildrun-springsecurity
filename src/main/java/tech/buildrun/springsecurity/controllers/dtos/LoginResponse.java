package tech.buildrun.springsecurity.controllers.dtos;

public record LoginResponse(String accessToken, Long expiresIn) {
}

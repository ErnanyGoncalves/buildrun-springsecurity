package tech.buildrun.springsecurity.controllers.dtos;

import java.util.List;

public record FeedDTO(List<FeedItemDTO> feedItems,Integer page, Integer pageSize,Integer totalPages, Long totalElements) {
}

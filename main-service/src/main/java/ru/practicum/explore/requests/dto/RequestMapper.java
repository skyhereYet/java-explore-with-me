package ru.practicum.explore.requests.dto;

import ru.practicum.explore.requests.model.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requsterList) {
        return requsterList.stream()
                .map(request -> mapToDto(request))
                .collect(Collectors.toList());
    }

    public static ParticipationRequestDto mapToDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setStatus(request.getStatus());
        return participationRequestDto;
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setStatus(request.getStatus());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated());
        dto.setId(request.getId());
        return dto;
    }
}

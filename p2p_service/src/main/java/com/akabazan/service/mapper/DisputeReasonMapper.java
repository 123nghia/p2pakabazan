package com.akabazan.service.mapper;

import com.akabazan.repository.entity.DisputeReason;
import com.akabazan.service.dto.response.DisputeReasonResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisputeReasonMapper {
    DisputeReasonResponse toDto(DisputeReason entity);

    List<DisputeReasonResponse> toDto(List<DisputeReason> entityList);
}

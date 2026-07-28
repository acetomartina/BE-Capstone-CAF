package com.martina.caf_fapi.common.mapper;

public interface Mapper<E, REQUEST, RESPONSE> {

    E toEntity(REQUEST request);

    RESPONSE toResponse(E entity);
}
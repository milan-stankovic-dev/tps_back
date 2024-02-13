package rs.ac.bg.fon.tps_backend.converter;

import java.util.List;

public interface DTOEntityConverter<DTO, ENTITY> {
    ENTITY toEntity(DTO dto);
    DTO toDto(ENTITY entity);

    default List<ENTITY> listTOEntity(List<DTO> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }

    default List<DTO> listToDTO(List<ENTITY> entities){
        return entities.stream().map(this::toDto).toList();
    }

}

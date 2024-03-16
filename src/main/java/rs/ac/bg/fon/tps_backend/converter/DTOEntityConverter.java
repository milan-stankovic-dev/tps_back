package rs.ac.bg.fon.tps_backend.converter;

import java.util.List;

public interface DTOEntityConverter<DTO, ENTITY> {
    ENTITY toEntity(DTO dto);
    DTO toDto(ENTITY entity);

    default List<ENTITY> listTOEntity(List<DTO> dtos) {
        if(dtos == null){
            throw new IllegalArgumentException("List of DTOs may not be null");
        }
        return dtos.stream().map(this::toEntity).toList();
    }

    default List<DTO> listToDTO(List<ENTITY> entities){
        if(entities == null){
            throw new IllegalArgumentException("List of entities may not be null");
        }
        return entities.stream().map(this::toDto).toList();
    }

}

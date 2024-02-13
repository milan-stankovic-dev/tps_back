package rs.ac.bg.fon.tps_backend.converter.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.dto.CityDTO;

@Component
public class CityConverter implements DTOEntityConverter<CityDTO, City> {
    @Override
    public City toEntity(CityDTO cityDTO) {
        return City.builder()
                .id(cityDTO.id())
                .name(cityDTO.name())
                .build();
    }

    @Override
    public CityDTO toDto(City city) {
        return new CityDTO(
                city.getId(),
                city.getName()
        );
    }
}

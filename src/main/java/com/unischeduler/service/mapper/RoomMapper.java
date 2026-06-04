package com.unischeduler.service.mapper;

import com.unischeduler.domain.Building;
import com.unischeduler.domain.Room;
import com.unischeduler.service.dto.BuildingDTO;
import com.unischeduler.service.dto.RoomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Room} and its DTO {@link RoomDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper extends EntityMapper<RoomDTO, Room> {
    @Mapping(target = "building", source = "building", qualifiedByName = "buildingName")
    RoomDTO toDto(Room s);

    @Named("buildingName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BuildingDTO toDtoBuildingName(Building building);
}

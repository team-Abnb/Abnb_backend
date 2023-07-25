package com.sparta.abnb.mapper;

import com.sparta.abnb.dto.responsedto.RoomResponseDto;
import com.sparta.abnb.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper( RoomMapper.class );

    RoomResponseDto roomToRoomResponseDto(Room room);


}
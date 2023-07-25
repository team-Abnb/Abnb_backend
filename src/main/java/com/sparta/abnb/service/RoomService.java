package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.RoomRequestDto;
import com.sparta.abnb.dto.responsedto.RoomResponseDto;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.RoomPicture;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.RoomPictureRepository;
import com.sparta.abnb.repository.RoomRepository;
import com.sparta.abnb.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomPictureRepository roomPictureRepository;
    private final S3Util s3Util;
    private final String ROOM_PICTURE_FOLDER = "profilePicture";
    // 등록과정
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto, List<MultipartFile> multipartFiles, User user) {
        // room 등록
        Room room = new Room(roomRequestDto, user);
        Room saveRoom = roomRepository.save(room);
        // picture s3에 올리는 과정 매번 올리고 그값을 list에 추가 이후 그 list 값을 for 문 돌려서 각각의 링크를 저장
        List<String> urlLinks = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String urlLink = s3Util.uploadImage(multipartFile, ROOM_PICTURE_FOLDER);
            RoomPicture roomPicture = new RoomPicture(urlLink, saveRoom);
            roomPictureRepository.save(roomPicture);
        }

        return new RoomResponseDto(saveRoom, user); //본인이 개설한 방에 본인이 wish 리스트에 등록하는게 뭔가 이상하지만...

    }
    // 특정 theme의 room 전체 조회
    public List<RoomResponseDto> findRoomsByTheme(String theme, User user) {
        return roomRepository.findAllByThemeOrderByCreatedAtDesc(theme)
                .stream()
                .map(room -> new RoomResponseDto(room, user))
                .toList();
    }

    // 특정 방에 대한 상세 조회
    public RoomResponseDto getSelectedRoom(Long roomId, User user) {
        Room room = findRoom(roomId);
        return new RoomResponseDto(room, user);
    }

    // 특정 방에 대한 수정 작업
    @Transactional
    public RoomResponseDto updateRoom(Long roomId, RoomRequestDto roomRequestDto, List<MultipartFile> multipartFiles, User user) throws AccessDeniedException {
        // 수정, 삭제 할 방이 존재하는지 확인
        Room room = findRoom(roomId);
        List<RoomPicture> roomPictures = room.getRoomPictures();
        // 수정, 삭제 할 방의 권한을 확인
        checkAuthority(room, user);
        // 수정
        // roomPicture도 수정 필요 ----------------------
        List<String> urlLinks = room.getRoomPictures().stream().map(RoomPicture::getUrlLink).toList();
        for (int i = 0; i < roomPictures.size(); i++) {
            RoomPicture roomPicture = roomPictures.get(i);
            String newUrlLink = s3Util.updateImage(urlLinks.get(i), multipartFiles.get(i), ROOM_PICTURE_FOLDER);
            roomPicture.update(newUrlLink, room);
        }
        room.update(roomRequestDto);

        return new RoomResponseDto(room, user);
    }

    public ResponseEntity<String> deleteRoom(Long roomId, User user) throws AccessDeniedException {
        // 수정, 삭제 할 방이 존재하는지 확인
        Room room = findRoom(roomId);
        // 수정, 삭제 할 방의 권한을 확인
        checkAuthority(room, user);
        // 삭제
        roomRepository.delete(room);

        return ResponseEntity.status(HttpStatus.OK).body("방이 삭제 되었습니다.");
    }

    // 수정, 삭제 할 방이 존재하는지 확인하는 메서드
    protected Room findRoom(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 방입니다."));
    }

    protected RoomPicture findRoomPicture(Long roomPictureId) {
        return roomPictureRepository.findById(roomPictureId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 사진입니다."));
    }

    // 수정, 삭제 할 방의 권한을 확인하는 메서드
    public void checkAuthority(Room room, User user) throws AccessDeniedException {
        // admin 확인
        if (!user.getRole().getAuthority().equals("ROLE_ADMIN")) {
            // userId 확인
            if (room.getUser().getUserId() != user.getUserId()) {
                throw new AccessDeniedException("작성자만 수정, 삭제가 가능합니다.");
            }
        }
    }
}

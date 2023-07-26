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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomPictureRepository roomPictureRepository;
    private final S3Util s3Util;
    private final String ROOM_PICTURE_FOLDER = "roomPicture";
    // 등록과정
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto, List<MultipartFile> multipartFiles, User user) {
        if(!isFile(multipartFiles)) {
            throw new IllegalArgumentException("사진없이는 방을 등록할 수 없습니다.");
        }
        // room 등록
        Room room = new Room(roomRequestDto, user);
        Room saveRoom = roomRepository.save(room);

        // url 생성, url값 담은 roompicture 생성
        List<RoomPicture> saveRoomPicture = createRoomPictureUrlLinks(saveRoom, multipartFiles);

        return new RoomResponseDto(saveRoom, user, saveRoomPicture); //본인이 개설한 방에 본인이 wish 리스트에 등록하는게 뭔가 이상하지만...
    }
    // 특정 theme의 room 전체 조회
    public List<RoomResponseDto> findRoomsByTheme(String theme, User user) {
        return roomRepository.findAllByThemeOrderByCreatedAtDesc(theme)
                .stream()
                .map(room -> new RoomResponseDto(room, user, findRoomPicture(room)))
                .toList();
    }

    // 특정 방에 대한 상세 조회
    public RoomResponseDto getSelectedRoom(Long roomId, User user) {
        Room room = findRoom(roomId);
        return new RoomResponseDto(room, user, findRoomPicture(room));
    }

    // 특정 방에 대한 수정 작업
    @Transactional
    public RoomResponseDto updateRoom(Long roomId, RoomRequestDto roomRequestDto, List<MultipartFile> multipartFiles, User user) throws AccessDeniedException {
        // 수정, 삭제 할 방이 존재하는지 확인
        Room room = findRoom(roomId);
        // 수정, 삭제 할 사진이 존재하는지 확인
        List<RoomPicture> roomPictures = findRoomPicture(room);
        // 수정, 삭제 할 방의 권한을 확인
        checkAuthority(room, user);
        if (isFile(multipartFiles)) {
            // 기존 url를 통해 url삭제후 roompicture 삭제
            deleteRoomPictureUrlLinks(room);
            // url 생성, url값 담은 roompicture 생성
            roomPictures = createRoomPictureUrlLinks(room, multipartFiles);
        }

        room.update(roomRequestDto);

        return new RoomResponseDto(room, user, roomPictures);
    }

    public ResponseEntity<String> deleteRoom(Long roomId, User user) throws AccessDeniedException {
        // 수정, 삭제 할 방이 존재하는지 확인
        Room room = findRoom(roomId);
        // 수정, 삭제 할 방의 권한을 확인
        checkAuthority(room, user);
        // 기존 url를 통해 url삭제후 roompicture 삭제
        deleteRoomPictureUrlLinks(room);
        // 삭제
        roomRepository.delete(room);

        return ResponseEntity.status(HttpStatus.OK).body("방이 삭제 되었습니다.");
    }

    // 수정, 삭제 할 방이 존재하는지 확인하는 메서드

    public Room findRoom(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 방입니다."));
    }

    public Boolean isFile(List<MultipartFile> multipartFiles) {
        try {
            return !multipartFiles.get(0).isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected List<RoomPicture> findRoomPicture(Room room) {
        List<RoomPicture> roomPictures= roomPictureRepository.findAllByRoom(room);
        if (roomPictures.isEmpty()) {
            new NullPointerException("존재하지 않는 사진입니다.");
        }
        return roomPictures;
    }

    public List<RoomPicture> createRoomPictureUrlLinks(Room room, List<MultipartFile> multipartFiles) {
        List<RoomPicture> roomPictures = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String newUrlLink = s3Util.uploadImage(multipartFile, ROOM_PICTURE_FOLDER);
            RoomPicture roomPicture = new RoomPicture(newUrlLink, room);
            RoomPicture newRoomPicture = roomPictureRepository.save(roomPicture);
            roomPictures.add(newRoomPicture);
        }
        return roomPictures;
    }

    public void deleteRoomPictureUrlLinks(Room room) {
        // 기존 url를 통해 url삭제후 roompicture 삭제
        List<RoomPicture> roomPictures = findRoomPicture(room);
        for (RoomPicture roomPicture : roomPictures) {
            String urlLink = roomPicture.getUrlLink();
            s3Util.deleteImage(urlLink);
            roomPictureRepository.deleteById(roomPicture.getRoomPictureId());
        }
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

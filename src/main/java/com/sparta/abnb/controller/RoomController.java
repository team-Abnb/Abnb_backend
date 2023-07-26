package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.RoomRequestDto;
import com.sparta.abnb.dto.responsedto.RoomResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.security.UserDetailsImpl;
import com.sparta.abnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 새로운 room 등록하기
    @PostMapping()
    public RoomResponseDto createRoom(@RequestPart(value = "room") RoomRequestDto roomRequestDto,
                                      @RequestPart(value = "images") List<MultipartFile> files,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return roomService.createRoom(roomRequestDto, files, user);
    }

    // 특정 주제의 room 전체 조회
    @GetMapping()
    public List<RoomResponseDto> getRoomsByTheme(@RequestParam String theme,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        return roomService.findRoomsByTheme(theme, user);
    }

    // room 상세 조회하기
    @GetMapping("{roomId}")
    public RoomResponseDto getRoom(@PathVariable Long roomId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = checkGuest(userDetails);
        return roomService.getSelectedRoom(roomId, user);
    }

    // room 수정하기
    // requestpart로 image를 받을때 여러 경우
    // 1. 아예 key 값도 없는 경우 : null로 취급
    // 2. key값은 있으나 입력값이 없는경우 : 정체불명의 무언가 들어가 있음 상세값은 empty인 값이 들어가 있다

    @PutMapping("{roomId}")
    public RoomResponseDto updateRoom(@PathVariable Long roomId,
                                      @RequestPart("room") RoomRequestDto roomRequestDto,
                                      @RequestPart(name = "images", required = false) List<MultipartFile> files,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        User user = userDetails.getUser();
        return roomService.updateRoom(roomId, roomRequestDto, files, user);

    }

    // room 삭제하기
    @DeleteMapping("{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        User user = userDetails.getUser();
        return roomService.deleteRoom(roomId, user);
    }

    // 로그인하지 않은 사용자도 접속가능하도록 guest 확인 기능
    private User checkGuest(UserDetailsImpl userDetails) {
        User user = null;
        try {
            user = userDetails.getUser();
        } catch (NullPointerException e) {
            log.info("게스트 사용자 입니다.");
        }
        return user;
    }

}

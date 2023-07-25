package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.RoomRequestDto;
import com.sparta.abnb.dto.responsedto.RoomResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.security.UserDetailsImpl;
import com.sparta.abnb.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private RoomService roomService;

    // 새로운 room 등록하기
    @PostMapping()
    public RoomResponseDto createRoom(@RequestBody @Valid RoomRequestDto roomRequestDto,
                                      @RequestPart("images") List<MultipartFile> files,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return roomService.createRoom(roomRequestDto, files, user);
    }

    // 특정 주제의 room 전체 조회
    @GetMapping()
    public List<RoomResponseDto> getRoomsByTheme(@RequestParam String theme,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return roomService.findRoomsByTheme(theme, user);
    }

    // room 상세 조회하기
    @GetMapping("{roomId}")
    public RoomResponseDto getRoom(@PathVariable Long roomId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return roomService.getSelectedRoom(roomId, user);
    }

    // room 수정하기
    @PutMapping("{roomId}")
    public RoomResponseDto updateRoom(@PathVariable Long roomId,
                                      @RequestBody @Valid RoomRequestDto roomRequestDto,
                                      @RequestPart("images") List<MultipartFile> files,
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

}

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
}

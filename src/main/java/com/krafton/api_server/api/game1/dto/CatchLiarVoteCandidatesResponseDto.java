package com.krafton.api_server.api.game1.dto;

import com.krafton.api_server.api.game1.domain.CatchLiarUser;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatchLiarVoteCandidatesResponseDto {
    private Long userId;
    private String userColor;
    private String imageKey;
    private String imagePath;


    public static CatchLiarVoteCandidatesResponseDto from(CatchLiarUser catchLiarUser) {
        if(catchLiarUser == null) return null;

        return CatchLiarVoteCandidatesResponseDto.builder()
                .userId(catchLiarUser.getUserId())
                .userColor(catchLiarUser.getUserColor())
                .imageKey(catchLiarUser.getImageKey())
                .imagePath(catchLiarUser.getImagePath())
                .build();
    }
}

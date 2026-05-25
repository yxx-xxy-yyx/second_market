package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class ForumInteractionStatus {
    private boolean liked;
    private long likeCount;
    private boolean favorited;
    private long favoriteCount;
}


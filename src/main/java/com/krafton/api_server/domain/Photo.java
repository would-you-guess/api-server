package com.krafton.api_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.File;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Photo {

    @Id @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    @Enumerated
    private Game game;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private File imageData;
}
drop table point_history;
drop table attached_photo;
drop table review;
drop table `user`;
drop table place;

-- user definition
CREATE TABLE `user` (
    `id` varchar(36) NOT NULL COMMENT '사용자 id',
    `user_name` varchar(32) NOT NULL COMMENT '사용자 이름',
    `current_point` int DEFAULT 0 COMMENT '현재 시점의 사용자의 point 합계',
    `create_time` datetime NOT NULL DEFAULT current_timestamp(),
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자';


-- place definition
CREATE TABLE `place` (
    `id` varchar(36) NOT NULL COMMENT '장소 id',
    `place_name` varchar(32) NOT NULL COMMENT '장소 이름',
    `create_time` datetime NOT NULL DEFAULT current_timestamp(),
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장소';


-- review definition
CREATE TABLE `review` (
    `id` varchar(36) NOT NULL COMMENT '리뷰 id',
    `user_id` varchar(36) NOT NULL COMMENT '사용자 id',
    `place_id` varchar(36) NOT NULL COMMENT '장소 id',
    `content` varchar(1000) NOT NULL COMMENT '리뷰 내용',
    `is_delete` tinyint(1) DEFAULT 0 NOT NULL COMMENT '삭제 여부',  -- TRUE는 1, FALSE 는 0
    `create_time` datetime NOT NULL DEFAULT current_timestamp(),
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY(`id`),
    CONSTRAINT `fk_user_review` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_place_review` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
    INDEX `idx_review_place_id` (`place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰';


-- attached_photo definition
CREATE TABLE `attached_photo` (
    `id` varchar(36) NOT NULL COMMENT '첨부사진 id',
    `review_id` varchar(36) COMMENT '리뷰 id',
    `file_extension` varchar(8) NOT NULL COMMENT '파일확장자',
    `file_path` varchar(256) NOT NULL COMMENT '첨부사진경로',
    `file_name` varchar(128) NOT NULL COMMENT '첨부사진이름',
    `stored_file` varchar(256) NOT NULL COMMENT '저장된 파일의 이름',
    `is_delete` tinyint(1) DEFAULT 0 NOT NULL COMMENT '삭제 여부', -- TRUE는 1, FALSE 는 0
    `is_approval` tinyint(1) DEFAULT 0 NOT NULL COMMENT '승인 여부', -- TRUE는 1, FALSE 는 0
    `create_time` datetime NOT NULL DEFAULT current_timestamp(),
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY(`id`),
    CONSTRAINT `fk_review_attached_photo` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`),
    INDEX `idx_attached_photo_review_id` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='첨부사진';


-- point_history definition
CREATE TABLE `point_history` (
    `id` varchar(36) NOT NULL COMMENT '포인트 히스토리 id',
    `review_id` varchar(36) NOT NULL COMMENT '리뷰 id',
    `user_id` varchar(36) NOT NULL COMMENT '사용자 id',
    `place_id` varchar(36) NOT NULL COMMENT '장소 id',
    `action` varchar(32) NOT NULL COMMENT '액션',
    `mod_type` varchar(32) COMMENT '수정 타입',
    `point` int NOT NULL COMMENT '포인트',
    `is_bonus_point` tinyint(1) DEFAULT 0 NOT NULL COMMENT '첫 리뷰 여부', -- TRUE는 1, FALSE 는 0
    `create_time` datetime NOT NULL DEFAULT current_timestamp(),
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY(`id`),
    CONSTRAINT `fk_review_point` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`),
    INDEX `idx_point_history_user_id` (`user_id`),
    INDEX `idx_point_history_user_id_place_id` (`user_id`, `place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='포인트 히스토리';

INSERT INTO triple.`user`(id, user_name, current_point, create_time, update_time)
VALUES('240a0658-dc5f-4878-9381-ebb7b2667772', 'user01', 0, current_timestamp(), current_timestamp());

INSERT INTO triple.`user`(id, user_name, current_point, create_time, update_time)
VALUES('3ede0ef2-92b7-4817-a5f3-0c575361f745', 'user02', 0, current_timestamp(), current_timestamp());

INSERT INTO triple.place(id, place_name, create_time, update_time)
VALUES('2e4baf1c-5acb-4efb-a1af-eddada31b00f', 'place01', current_timestamp(), current_timestamp());

INSERT INTO triple.place(id, place_name, create_time, update_time)
VALUES('afb0cef2-851d-4a50-bb07-9cc15cbdc332', 'place02', current_timestamp(), current_timestamp());

INSERT INTO triple.place(id, place_name, create_time, update_time)
VALUES('e4d1a64e-a531-46de-88d0-ff0ed70c0bb8', 'place03', current_timestamp(), current_timestamp());

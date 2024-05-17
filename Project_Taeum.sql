use taeumDB;

drop table if exists membertbl;
CREATE TABLE IF NOT EXISTS membertbl(
    M_ID BIGINT auto_increment primary key,
    username VARCHAR(50) NOT NULL unique,
    password VARCHAR(100) NOT NULL, -- 비번은 암호하하기 위해서
    M_NAME VARCHAR(10) NOT NULL,
    M_PHONE VARCHAR(13) NOT NULL,
    role varchar(20) default "USER"
    );
     
    select * from membertbl;
    
  DROP table if exists applytbl;
CREATE TABLE IF NOT EXISTS applytbl(
   M_NAME VARCHAR(10) NOT NULL, -- 신청자 이름
    M_PHONE VARCHAR(13) NOT NULL, -- 신청자 전화번호
    A_ID BIGINT auto_increment primary key,
    A_NAME VARCHAR(10) NOT NULL, -- 탑승자 이름
    A_PHONE VARCHAR(13) DEFAULT NULL,    -- 탑승자 전화번호
    A_STARTADRESSNUM VARCHAR(20) NOT NULL,-- 출발지 우편번호
    A_STARTADRESS VARCHAR(60) NOT NULL,-- 출발지 도로명 주소
    A_STARTDADRESS VARCHAR(60) DEFAULT NULL, -- 출발지 상세 주소
    A_STARTRADRESS varchar(60) NOT NULL, -- 출발지 참고 주소 
   A_ENDADRESSNUM VARCHAR(20) NOT NULL, -- 도착지 우편 번호
    A_ENDADRESS VARCHAR (60) NOT NULL, -- 도착지 도로명 주소
   A_ENDDADRESS VARCHAR(60) DEFAULT NULL, -- 도착지 상세 주소
    A_ENDRADRESS varchar(60) NOT NULL, -- 도착지 참고 주소
   A_DATE DATETIME DEFAULT CURRENT_TIMESTAMP, -- 픽업 시간 (폼에서 시간 설정 가능)
    A_LOCALDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    A_CONTENTS TEXT DEFAULT NULL, -- 특이사항
    M_ID BIGINT, -- 외래키
    STATUS int,
    CONSTRAINT FK_M_ID FOREIGN KEY (M_ID) REFERENCES membertbl(M_ID)
);
    select * from applytbl;
    
    alter table NodeTbl add  A_LOCALDATE DATETIME DEFAULT CURRENT_TIMESTAMP;
    -- -------------------------------------------------------------------------------------------------------
drop table if exists NodeTbl; -- xml 바꿔야함
Create table NodeTbl( 
   node_id bigint primary key not null auto_increment,
    address varchar(500), -- 노드 주소
    x double (9,6), -- 노드 x 좌표
    y double (9,6), -- 노드 y 좌표
    M_ID bigint, -- memberTbl M_ID
    DR_ID bigint, -- 기사 id (999일경우 대기중, 0일 경우 거절, 0이상일경우 운행중)
    kind int default 0, -- 출발, 도착 
    cycle int default 0, -- 배차 차수
    status varchar(10) default null,
    A_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    D_ID bigint default null,
    M_PHONE varchar(13),
    M_NAME varchar(5),
    A_NAME varchar(5),
    A_CONTENTS varchar(100),
    A_ID bigint,
     FOREIGN KEY (M_ID) REFERENCES membertbl(M_ID),
     FOREIGN KEY (DR_ID) REFERENCES drivertbl(DR_ID)
);
alter table nodetbl change ride ride int default 0;
select * from nodetbl WHERE A_DATE LIKE '%2024-04-26%';
  select * from nodetbl WHERE address LIKE '%부평구%'; -- AND A_DATE LIKE '%2024-04-29%';
alter table nodetbl add status varchar(10) default null;
update nodetbl set ride = 0;

select * FROM dispatchtbl;
select * from nodetbl;




DELETE FROM NodeTbl WHERE node_id =10;




INSERT INTO NodeTbl (node_id, address, x, y, M_ID, DR_ID, kind, cycle, A_DATE, D_ID) VALUES
(null, '인천 서구 가정로 112번길 10', 126.682168, 37.540242, 1, null, 1, null, now(), null);

INSERT INTO NodeTbl (node_id, address, x, y, M_ID, DR_ID, kind, cycle, A_DATE, D_ID) VALUES
(null, '인천 부평구 부개로 91', 126.723879, 37.501361, 1, null, 1, null, NOW(), null),
(null, '인천 부평구 굴포로 489', 126.715988, 37.507617, 1, null, 2, null, NOW(), null),
(null, '인천 부평구 부평대로 365', 126.720972, 37.503812, 2, null, 1, null, NOW(), null),
(null, '인천 부평구 굴포로 472', 126.717276, 37.510211, 2, null, 2, null, NOW(), null),
(null, '인천 부평구 부개로 77', 126.722592, 37.498736, 3, null, 1, null, NOW(), null),
(null, '인천 부평구 부평대로 373', 126.720007, 37.504910, 3, null, 2, null, NOW(), null),
(null, '인천 부평구 부개로 53', 126.722043, 37.499876, 4, null, 1, null, NOW(), null),
(null, '인천 부평구 부흥로 281', 126.721429, 37.514492, 4, null, 2, null, NOW(), null);





DROP TABLE IF EXISTS node_cost; 
CREATE TABLE `node_cost` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '노드비용ID',
 `start_node_id` bigint(20) NOT NULL COMMENT '시작노드ID',
 `end_node_id` bigint(20) NOT NULL COMMENT '종료노드ID',
 `distance_meter` bigint(20) DEFAULT NULL COMMENT '이동거리(미터)',
 `duration_second` bigint(20) DEFAULT NULL COMMENT '이동시간(초)',
 `toll_fare` int(11) DEFAULT NULL COMMENT '통행 요금(톨게이트)',
 `taxi_fare` int(11) DEFAULT NULL COMMENT '택시 요금(지자체별, 심야, 시경계, 복합, 콜비 감안)',
 `fuel_price` int(11) DEFAULT NULL COMMENT '해당 시점의 전국 평균 유류비와 연비를 감안한 유류비',
 `path_json` text CHARACTER SET utf8 DEFAULT NULL COMMENT '이동경로JSON [[x,y],[x,y]]',
 `reg_dt` datetime NOT NULL COMMENT '등록일시',
 `mod_dt` datetime DEFAULT NULL COMMENT '수정일시',
 PRIMARY KEY (`id`),
 KEY `idx_node_cost_start_node_id` (`start_node_id`),
 KEY `idx_node_cost_end_node_id` (`end_node_id`)
) ENGINE=InnoDB COMMENT='노드비용';
    
    DROP table if exists dispatchtbl;
CREATE TABLE IF NOT EXISTS dispatchtbl( 
   D_ID BIGINT auto_increment primary key,
    D_SELECT varchar(500), -- 선정시간
    D_STATUS varchar(10) , -- 0= 선정 중/  1 = 선정완료 / 2 = 거절 
    D_CONFIRM dateTime default null,
    D_CANCLE DATETIME DEFAULT NULL, -- 취소시간
    D_REASON varchar(30) DEFAULT NULL,
   DR_ID BIGINT,
   D_DATE varchar(50), -- 탑승시간.
	cycle int default 0,
    FOREIGN KEY (DR_ID) REFERENCES drivertbl (DR_ID)
 );


    
    DROP table if exists drivermanagement;
CREATE TABLE IF NOT EXISTS drivermanagement(
    DM_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    DR_ID bigint,
    DM_STWORK datetime default NULL,
    DM_ENDWORK datetime default NULL,
    CONSTRAINT FK_dm_DR_ID FOREIGN KEY (DR_ID) REFERENCES drivertbl(DR_ID)
);
    select * from drivermanagement;
    
    DROP table if exists boardtbl;
CREATE TABLE IF NOT EXISTS boardtbl(
    B_ID bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username varchar(20) not null,
    B_TITLE VARCHAR(50) BINARY NOT NULL,   -- 게시글 제목
    B_CONTENTS TEXT BINARY NOT NULL,  -- 게시글 내용
    B_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,    -- 작성시간
    M_ID bigint,
    role varchar(20) not null,
    FOREIGN KEY(M_ID)
   REFERENCES membertbl(M_ID)
);
select * from boardtbl;

drop table if exists commentTbl;
CREATE TABLE IF NOT EXISTS commentTbl(
    C_ID bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    B_ID bigint NOT NULL,
    C_CONTENTS TEXT BINARY NOT NULL,  -- 댓글 내용
    C_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,    -- 작성시간
    M_ID bigint,
    FOREIGN KEY(B_ID)
    REFERENCES boardtbl(B_ID),
    FOREIGN KEY(M_ID)
    REFERENCES membertbl(M_ID)
);

select * from commentTbl;
    
    -- ---------------------------------------------------------------------------------------------------------------
    DROP table if exists drivertbl;
CREATE TABLE IF NOT EXISTS drivertbl(
    DR_ID BIGINT auto_increment primary key,
    DR_CARNUM VARCHAR(10),
    DR_AREA VARCHAR(10),
    M_ID BIGINT,
    FOREIGN KEY (M_ID) REFERENCES membertbl(M_ID)
); -- 드라이버 계정(회원) 테이블
select * from drivertbl;


select dtb.*, mtb.M_NAME, mtb.M_PHONE from drivertbl dtb, membertbl mtb where dtb.M_ID = mtb.M_ID;
select dtb.*, mtb.M_NAME, mtb.M_PHONE from drivertbl dtb, membertbl mtb where dtb.M_ID = mtb.M_ID and dtb.DR_AREA LIKE '%미추홀구%';
DROP table if exists driverprofile;
CREATE TABLE IF NOT EXISTS driverprofile( 
    DP_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    DP_ORINAME VARCHAR(255) NOT NULL,
    DP_SYSNAME VARCHAR(255) NOT NULL,
    M_ID bigint,
    FOREIGN KEY (M_ID) REFERENCES membertbl(M_ID)
); -- 프로필 이미지 업로드용
select * from driverprofile;

drop table if exists notificationtbl;
CREATE TABLE IF NOT EXISTS notificationtbl (
    NOTIFICATION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ROLE VARCHAR(20) NOT NULL, -- 알림을 받을 역할
    MESSAGE VARCHAR(255) NOT NULL, -- 알림 메시지 내용
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 알림이 생성된 시간

);
    select * from notificationtbl;
    
-- 1. role 테이블
CREATE TABLE role (
                      role_id     BIGINT       NOT NULL AUTO_INCREMENT,
                      role_name   VARCHAR(20)  NOT NULL,
                      description VARCHAR(100) NULL,
                      created_at DATETIME NOT NULL DEFAULT NOW(),
                      PRIMARY KEY (role_id),
                      UNIQUE KEY uq_role_name (role_name)
);

INSERT INTO role (role_name, description, created_at) VALUES ('ADMIN', '운영자', NOW());
INSERT INTO role (role_name, description, created_at) VALUES ('USER', '수혜자', NOW());
INSERT INTO role (role_name, description, created_at) VALUES ('MERCHANT', '가맹점 관리자', NOW());

CREATE TABLE member_token (
    member_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (member_id)
);

CREATE TABLE banner (
    banner_id   BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '배너 PK',
    title       VARCHAR(100)    NOT NULL                  COMMENT '배너 제목 (관리용)',
    image_url   VARCHAR(500)    NOT NULL                  COMMENT '이미지 경로',
    link_url    VARCHAR(500)    NULL                      COMMENT '클릭 시 이동 URL',
    sort_no     INT             NOT NULL DEFAULT 1        COMMENT '노출 순서',
    use_yn      CHAR(1)         NOT NULL DEFAULT 'Y'      COMMENT '노출 여부 (Y/N)',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '등록일',
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP          COMMENT '수정일',

    PRIMARY KEY (banner_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='배너 관리';


CREATE TABLE point_grant_log (
    log_id          BIGINT          NOT NULL AUTO_INCREMENT      COMMENT '로그 PK',
    grant_id        VARCHAR(36)     NOT NULL                     COMMENT '배치 식별자 (UUID, 같은 일괄지급끼리 묶음)',
    title           VARCHAR(100)    NOT NULL                     COMMENT '지급명 (예: 2026년 6월 정기지급)',
    grant_amount    BIGINT          NOT NULL                     COMMENT '지급 포인트',
    member_id       BIGINT          NULL                         COMMENT '수혜자 FK',
    member_name     VARCHAR(50)     NOT NULL                     COMMENT '수혜자 이름 (스냅샷)',
    before_point    BIGINT          NOT NULL DEFAULT 0           COMMENT '지급 전 포인트',
    after_point     BIGINT          NOT NULL DEFAULT 0           COMMENT '지급 후 포인트',
    status          ENUM('SUCCESS', 'FAIL')
                                    NOT NULL DEFAULT 'SUCCESS'   COMMENT '처리 결과',
    fail_reason     VARCHAR(255)    NULL                         COMMENT '실패 사유',
    granted_by      BIGINT          NULL                         COMMENT '지급자 member_id (관리자)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '처리일시',

    PRIMARY KEY (log_id),
    KEY idx_pgl_grant_id  (grant_id),
    KEY idx_pgl_member_id (member_id),
    KEY idx_pgl_status    (status),
    KEY idx_pgl_created_at (created_at),
    CONSTRAINT fk_pgl_member
        FOREIGN KEY (member_id)  REFERENCES member (member_id) ON DELETE SET NULL,
    CONSTRAINT fk_pgl_granted_by
        FOREIGN KEY (granted_by) REFERENCES member (member_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='포인트 일괄지급 로그';

-- 2. member 테이블
CREATE TABLE member (
            member_id   BIGINT       NOT NULL AUTO_INCREMENT,
            role_id     BIGINT       NULL,
            login_id    VARCHAR(50)  NOT NULL,
            password    VARCHAR(255) NOT NULL,
            name        VARCHAR(50)  NOT NULL,
            birth_date  DATE         NOT NULL,
            gender      ENUM('MALE', 'FEMALE') NOT NULL,
            address     VARCHAR(255) NULL,
            detail_address VARCHAR(255) NULL,
            email       VARCHAR(255) NULL,
            point       BIGINT       NOT NULL DEFAULT 0,
            status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
            referrer_id BIGINT       NULL,
            memo TEXT DEFAULT NULL,
            apply_date    DATETIME NULL,
            approve_date  DATETIME NULL,
            created_at DATETIME NOT NULL DEFAULT NOW(),
            updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

            PRIMARY KEY (member_id),
            UNIQUE KEY uq_login_id (login_id),
            CONSTRAINT fk_member_role
                FOREIGN KEY (role_id) REFERENCES role (role_id) ON DELETE SET NULL,
            CONSTRAINT fk_member_referrer
                FOREIGN KEY (referrer_id) REFERENCES member (member_id) ON DELETE SET NULL
);

CREATE TABLE member_withdraw_log (
    log_id      BIGINT       NOT NULL AUTO_INCREMENT,
    member_id   BIGINT       NOT NULL,
    login_id    VARCHAR(50)  NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    reason      VARCHAR(255) NULL      COMMENT '탈퇴 사유',
    created_at  DATETIME     NOT NULL  DEFAULT NOW(),

    PRIMARY KEY (log_id),
    INDEX idx_withdraw_member_id (member_id),
    INDEX idx_withdraw_created_at (created_at)
) COMMENT '회원 탈퇴 로그';

CREATE TABLE bank (
    bank_id   BIGINT       NOT NULL AUTO_INCREMENT,
    bank_name VARCHAR(50)  NOT NULL COMMENT '은행명',
    bank_code VARCHAR(10)  NOT NULL COMMENT '은행코드',
    status    VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    created_at DATETIME    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (bank_id)
) COMMENT '은행 정보';

-- 3. card 테이블
CREATE TABLE card (
      card_id     BIGINT      NOT NULL AUTO_INCREMENT,
      member_id   BIGINT      NOT NULL,
      bank_id     BIGINT      NULL COMMENT '은행 FK',
      card_number CHAR(16)    NOT NULL,
      card_alias  VARCHAR(50) NULL,
      is_primary  TINYINT     NOT NULL DEFAULT 0,
      card_type   VARCHAR(255) NOT NULL,
      status      ENUM('ACTIVE', 'BLOCKED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
      created_at  DATETIME    NOT NULL DEFAULT NOW(),
      updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
      PRIMARY KEY (card_id),
      UNIQUE KEY uq_card_number (card_number),
      CONSTRAINT fk_card_member
          FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE RESTRICT,
      CONSTRAINT fk_card_bank
          FOREIGN KEY (bank_id) REFERENCES bank (bank_id) ON DELETE SET NULL,
      INDEX idx_card_member_id (member_id),
      INDEX idx_card_bank_id (bank_id)
);


-- 카드번호 풀(pool) 테이블
-- 우리가 발급한 카드번호 전체를 미리 저장. 회원 가입 시 이 목록과 대조해 유효성 검증.
CREATE TABLE `card_list` (
  `card_list_id` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '카드 목록 PK',
  `card_number`  CHAR(16)     NOT NULL                COMMENT '카드번호 16자리 (하이픈 제외)',
  PRIMARY KEY (`card_list_id`),
  UNIQUE KEY `uq_card_list_number` (`card_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 카드 재발급 사유테이블
CREATE TABLE `card_reissue_history` (
    `history_id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '이력 PK',
    `old_card_id`     BIGINT       NOT NULL                COMMENT '기존 카드 ID',
    `old_card_number` CHAR(16)     NOT NULL                COMMENT '기존 카드번호',
    `new_card_id`     BIGINT       NOT NULL                COMMENT '새 카드 ID',
    `new_card_number` CHAR(16)     NOT NULL                COMMENT '새 카드번호',
    `member_id`       BIGINT       NOT NULL                COMMENT '소유자 ID',
    `reason`          VARCHAR(20)  NOT NULL                COMMENT '사유 (LOST/DAMAGED/STOLEN/OTHER)',
    `created_at`      DATETIME     NOT NULL DEFAULT NOW()  COMMENT '재발급 일시',
    PRIMARY KEY (`history_id`),
    KEY `idx_reissue_member` (`member_id`),
    KEY `idx_reissue_old_card` (`old_card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE merchant_category (
       category_id   BIGINT       NOT NULL AUTO_INCREMENT,
       category_name VARCHAR(50)  NOT NULL,
       description   VARCHAR(100) NULL,
       created_at    DATETIME     NOT NULL DEFAULT NOW(),

       PRIMARY KEY (category_id),
       UNIQUE KEY uq_category_name (category_name)
);

-- 4. merchant 테이블
CREATE TABLE merchant (
    merchant_id     BIGINT       NOT NULL AUTO_INCREMENT,
    member_id       BIGINT       NOT NULL,
    category_id BIGINT NULL,
    merchant_name   VARCHAR(100) NOT NULL,
    representative  VARCHAR(50)  NOT NULL,
    business_number VARCHAR(20)  NOT NULL,
    contact         VARCHAR(20)  NOT NULL,
    contact2         VARCHAR(20)  NULL,
    address         VARCHAR(255) NOT NULL,
    email           VARCHAR(100) NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    referrer_id     BIGINT       NULL,
    terminal_id     VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    apply_date    DATETIME NULL,
    approve_date  DATETIME NULL,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (merchant_id),
    UNIQUE KEY uq_merchant_business_number (business_number),
    CONSTRAINT fk_merchant_member
        FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE RESTRICT,
    CONSTRAINT fk_merchant_referrer
        FOREIGN KEY (referrer_id) REFERENCES merchant (merchant_id) ON DELETE SET NULL,
    CONSTRAINT fk_merchant_category
        FOREIGN KEY (category_id) REFERENCES merchant_category (category_id) ON DELETE SET NULL
);

-- 5. payment 테이블
CREATE TABLE payment (
    payment_id               BIGINT       NOT NULL AUTO_INCREMENT,
    member_id                BIGINT       NULL,
    merchant_id              BIGINT       NULL,
    koces_cd                 VARCHAR(5)   NULL,
    message_number           VARCHAR(50)  NULL,
    institution_code         VARCHAR(20)  NULL,
    transmission_date        VARCHAR(8)   NOT NULL DEFAULT '',
    trace_number             VARCHAR(50)  NULL,
    terminal_id              VARCHAR(100) NULL,
    business_number          VARCHAR(20)  NULL,
    merchant_number          VARCHAR(50)  NULL,
    merchant_name            VARCHAR(100) NULL,
    representative           VARCHAR(50)  NULL,
    phone                    VARCHAR(20)  NULL,
    address                  VARCHAR(255) NULL,
    card_number              CHAR(16)     NULL,
    track_data               VARCHAR(37)  NULL,
    key_in                   CHAR(1)      NULL,
    input_method             VARCHAR(20)  NULL,
    amount                   BIGINT       NULL,
    transaction_type         VARCHAR(20)  NULL,
    approval_number          VARCHAR(50)  NULL,
    response_code            VARCHAR(10)  NULL,
    cancel_code              VARCHAR(20)  NULL,
    original_trade_date      VARCHAR(8)   NULL,
    original_approval_number VARCHAR(50)  NULL,
    original_amount          BIGINT       NULL,
    remaining_point          BIGINT       NULL,
    filter_value             VARCHAR(255) NULL,
    status                   ENUM('SUCCESS', 'FAILED', 'CANCELED', 'DELETED') NOT NULL DEFAULT 'SUCCESS',
    created_at               DATETIME     NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (payment_id, transmission_date),
    INDEX idx_payment_member_id           (member_id),
    INDEX idx_payment_merchant_id         (merchant_id),
    INDEX idx_payment_status              (status),
    INDEX idx_payment_merchant_status     (merchant_id, status),
    INDEX idx_approval_number             (approval_number),
    INDEX idx_payment_transmission_status (transmission_date, status)
)
PARTITION BY RANGE COLUMNS (transmission_date) (
    PARTITION p20260426 VALUES LESS THAN ('20260427'),
    PARTITION p20260427 VALUES LESS THAN ('20260428'),
    PARTITION p20260428 VALUES LESS THAN ('20260429'),
    PARTITION p20260429 VALUES LESS THAN ('20260430'),
    PARTITION p20260430 VALUES LESS THAN ('20260501'),
    PARTITION p_future  VALUES LESS THAN (MAXVALUE)
);

-- 6. settlement 테이블

CREATE TABLE settlement (
    settlement_id     BIGINT       NOT NULL AUTO_INCREMENT,
    merchant_id       BIGINT       NOT NULL,
    merchant_name     VARCHAR(100) NOT NULL,
    business_number   VARCHAR(20)  NOT NULL,
    settlement_month  VARCHAR(7)   NOT NULL,
    settlement_date   VARCHAR(8)   NOT NULL,
    settlement_amount BIGINT       NOT NULL,
    status            ENUM('PENDING', 'COMPLETED', 'FAILED', 'DELETED') NOT NULL DEFAULT 'PENDING',
    status_changed_at DATETIME     NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (settlement_id, settlement_date),
)
PARTITION BY RANGE COLUMNS (settlement_date) (
    PARTITION p202605 VALUES LESS THAN ('202605'),
    PARTITION p202606 VALUES LESS THAN ('202606'),
    PARTITION p202607 VALUES LESS THAN ('202607'),
    PARTITION p202608 VALUES LESS THAN ('202608'),
    PARTITION p202609 VALUES LESS THAN ('202609'),
    PARTITION p202610 VALUES LESS THAN ('202610'),
    PARTITION p202611 VALUES LESS THAN ('202611'),
    PARTITION p202612 VALUES LESS THAN ('202612'),
    PARTITION p_future  VALUES LESS THAN (MAXVALUE)
);

-- 7. allowed_ip 테이블
CREATE TABLE allowed_ip (
    ip_id       BIGINT       NOT NULL AUTO_INCREMENT,
    ip_address  VARCHAR(50)  NOT NULL,
    merchant_id BIGINT       NULL,
    description VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (ip_id),
    UNIQUE KEY uq_ip_address (ip_address),
    CONSTRAINT fk_allowed_ip_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchant (merchant_id)
        ON DELETE SET NULL    -- ✅ 가맹점 삭제 시 IP는 NULL 처리
);



CREATE TABLE password_reset_token (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expires_at  DATETIME NOT NULL,
    used        TINYINT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_member FOREIGN KEY (member_id) REFERENCES member(member_id)
);


-- ============================================================
-- 게시판 타입 마스터
-- ============================================================
CREATE TABLE board_type (
    board_type_id   BIGINT       NOT NULL AUTO_INCREMENT,
    type_code       VARCHAR(20)  NOT NULL COMMENT '타입코드 (NOTICE, RESOURCE, PRESS)',
    type_name       VARCHAR(50)  NOT NULL COMMENT '타입명 (공지사항, 서식/자료, 언론보도)',
    sort_order      INT          NOT NULL DEFAULT 0,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (board_type_id),
    UNIQUE KEY uq_board_type_code (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시판 타입';

-- 기본 데이터
INSERT INTO board_type (type_code, type_name, sort_order)
VALUES
    ('NOTICE',   '공지사항', 1),
    ('RESOURCE', '서식/자료', 2),
    ('PRESS',    '언론보도', 3);


-- ============================================================
-- 게시글 (일반화)
-- ============================================================
CREATE TABLE board (
    board_id        BIGINT          NOT NULL AUTO_INCREMENT,
    board_type_id   BIGINT          NOT NULL COMMENT '게시판 타입 FK',
    member_id       BIGINT          DEFAULT NULL COMMENT '작성자 FK',
    title           VARCHAR(255)    NOT NULL COMMENT '제목',
    content         LONGTEXT        DEFAULT NULL COMMENT '내용',
    thumbnail       VARCHAR(500)    DEFAULT NULL COMMENT '썸네일 이미지 경로',
    source          VARCHAR(255)    DEFAULT NULL COMMENT '출처 (언론보도용)',
    source_url      VARCHAR(500)    DEFAULT NULL COMMENT '원문 URL (언론보도용)',
    view_count      INT             NOT NULL DEFAULT 0 COMMENT '조회수',
    is_pinned       TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '상단고정',
    is_active       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '노출여부',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DELETED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (board_id),
    KEY idx_board_type   (board_type_id),
    KEY idx_board_member (member_id),
    KEY idx_board_status (status, is_active),
    CONSTRAINT fk_board_type   FOREIGN KEY (board_type_id) REFERENCES board_type (board_type_id),
    CONSTRAINT fk_board_member FOREIGN KEY (member_id)     REFERENCES member (member_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시글';

-- 회원가입 문의
CREATE TABLE member_inquiry (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유번호
    name          VARCHAR(50)   NOT NULL,             -- 이름
    phone         VARCHAR(20)   NOT NULL,             -- 휴대폰
    title         VARCHAR(200)  NOT NULL,             -- 문의제목
    content       TEXT          NOT NULL,             -- 문의내용
    status        VARCHAR(20)   NOT NULL DEFAULT 'WAIT', -- 상태 (WAIT/DONE)
    created_at    DATETIME      NOT NULL DEFAULT NOW(), -- 문의 등록일
    answered_at   DATETIME                           -- 답변 완료일
);

-- ============================================================
-- 공통 파일 (다른 테이블에서 참조 가능)
-- ============================================================
CREATE TABLE common_file (
    file_id         BIGINT          NOT NULL AUTO_INCREMENT,
    ref_type        VARCHAR(50)     NOT NULL COMMENT '참조 테이블명 (board, member 등)',
    ref_id          BIGINT          NOT NULL COMMENT '참조 ID',
    file_name       VARCHAR(255)    NOT NULL COMMENT '원본 파일명',
    stored_name     VARCHAR(255)    NOT NULL COMMENT '저장 파일명 (UUID)',
    file_path       VARCHAR(500)    NOT NULL COMMENT '저장 경로',
    file_ext        VARCHAR(20)     DEFAULT NULL COMMENT '확장자',
    file_size       BIGINT          DEFAULT NULL COMMENT '파일 크기 (bytes)',
    mime_type       VARCHAR(100)    DEFAULT NULL COMMENT 'MIME 타입',
    sort_order      INT             NOT NULL DEFAULT 0 COMMENT '정렬순서',
    is_active       TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (file_id),
    KEY idx_common_file_ref (ref_type, ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 파일';


CREATE TABLE site_config (
    config_no   INT          NOT NULL AUTO_INCREMENT,
    config_key  VARCHAR(100) NOT NULL,
    config_val  VARCHAR(500) NULL,
    sort_no     INT          NOT NULL DEFAULT 1,
    use_yn      CHAR(1)      NOT NULL DEFAULT 'Y',
    reg_dt      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mod_dt      TIMESTAMP    NULL     ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (config_no),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사이트 설정';

---------------------------------------------------------
1. 프로시저 생성 (CREATE PROCEDURE)
   ↓
2. 이벤트 생성 (CREATE EVENT)
   ↓
3. 이벤트가 스케줄에 맞게 프로시저 자동 호출 (CALL)



SET GLOBAL event_scheduler = ON;


-- payment 파티셔닝 프로시저 (일별)
DELIMITER $$

CREATE PROCEDURE sp_add_payment_partition()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE days_in_month INT;
    DECLARE partition_name VARCHAR(20);
    DECLARE less_than VARCHAR(8);
    DECLARE partition_exists INT;

    -- 다음 달 일수
    SET days_in_month = DAY(LAST_DAY(DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY)));

    WHILE i <= days_in_month DO
        -- 파티션명 (예: p20260501)
        SET partition_name = CONCAT('p',
            DATE_FORMAT(
                DATE_ADD(DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY), INTERVAL i-1 DAY),
                '%Y%m%d'
            )
        );

        -- LESS THAN 값 (다음날)
        SET less_than = DATE_FORMAT(
            DATE_ADD(DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY), INTERVAL i DAY),
            '%Y%m%d'
        );

        -- 파티션 존재 여부 확인
        SELECT COUNT(*) INTO partition_exists
        FROM INFORMATION_SCHEMA.PARTITIONS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'payment'
        AND PARTITION_NAME = partition_name;

        -- 없으면 추가
        IF partition_exists = 0 THEN
            SET @sql = CONCAT(
                'ALTER TABLE payment REORGANIZE PARTITION p_future INTO (',
                'PARTITION ', partition_name, ' VALUES LESS THAN (''', less_than, '''),',
                'PARTITION p_future VALUES LESS THAN (MAXVALUE)',
                ')'
            );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;


-- settlement 파티션 프로시저 (월별)
DELIMITER $$

CREATE PROCEDURE sp_add_settlement_partition()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE next_year INT;
    DECLARE partition_name VARCHAR(20);
    DECLARE less_than VARCHAR(10);
    DECLARE partition_exists INT;

    SET next_year = YEAR(NOW()) + 1;

    WHILE i <= 12 DO
        -- 파티션명 (예: p202601)
        SET partition_name = CONCAT('p', next_year, LPAD(i, 2, '0'));

        -- LESS THAN 값
        IF i = 12 THEN
            SET less_than = CONCAT(next_year + 1, '-01');
        ELSE
            SET less_than = CONCAT(next_year, '-', LPAD(i + 1, 2, '0'));
        END IF;

        -- 파티션 존재 여부 확인
        SELECT COUNT(*) INTO partition_exists
        FROM INFORMATION_SCHEMA.PARTITIONS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'settlement'
        AND PARTITION_NAME = partition_name;

        -- 없으면 추가
        IF partition_exists = 0 THEN
            SET @sql = CONCAT(
                'ALTER TABLE settlement REORGANIZE PARTITION p_future INTO (',
                'PARTITION ', partition_name, ' VALUES LESS THAN (''', less_than, '''),',
                'PARTITION p_future VALUES LESS THAN (MAXVALUE)',
                ')'
            );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;


-- 오래된 테이블 삭제 프로시저
DELIMITER $$

CREATE PROCEDURE sp_drop_old_partitions()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE p_name VARCHAR(50);
    DECLARE three_years_ago VARCHAR(8);

    SET three_years_ago = DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 YEAR), '%Y%m%d');

    -- payment 오래된 파티션 삭제
    BEGIN
        DECLARE cur_payment CURSOR FOR
            SELECT PARTITION_NAME
            FROM INFORMATION_SCHEMA.PARTITIONS
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'payment'
            AND PARTITION_NAME != 'p_future'
            AND REPLACE(PARTITION_NAME, 'p', '') < three_years_ago;

        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

        OPEN cur_payment;
        payment_loop: LOOP
            FETCH cur_payment INTO p_name;
            IF done THEN LEAVE payment_loop; END IF;
            SET @sql = CONCAT('ALTER TABLE payment DROP PARTITION ', p_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END LOOP;
        CLOSE cur_payment;
    END;

    -- done 초기화
    SET done = FALSE;

    -- settlement 오래된 파티션 삭제 (3년 이상된 월)
    BEGIN
        DECLARE cur_settlement CURSOR FOR
            SELECT PARTITION_NAME
            FROM INFORMATION_SCHEMA.PARTITIONS
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'settlement'
            AND PARTITION_NAME != 'p_future'
            AND REPLACE(PARTITION_NAME, 'p', '') < DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 YEAR), '%Y%m');

        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

        OPEN cur_settlement;
        settlement_loop: LOOP
            FETCH cur_settlement INTO p_name;
            IF done THEN LEAVE settlement_loop; END IF;
            SET @sql = CONCAT('ALTER TABLE settlement DROP PARTITION ', p_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END LOOP;
        CLOSE cur_settlement;
    END;
END$$

DELIMITER ;



-- payment 파티션 생성 (매월 1일 새벽 2시)
CREATE EVENT IF NOT EXISTS evt_add_payment_partition
ON SCHEDULE EVERY 1 MONTH
STARTS '2026-05-01 02:00:00'
DO
    CALL sp_add_payment_partition();

-- settlement 파티션 생성 (매년 1월 1일 새벽 3시)
CREATE EVENT IF NOT EXISTS evt_add_settlement_partition
ON SCHEDULE EVERY 1 YEAR
STARTS '2027-01-01 03:00:00'
DO
    CALL sp_add_settlement_partition();

-- 오래된 파티션 삭제 (매월 1일 새벽 4시)
CREATE EVENT IF NOT EXISTS evt_drop_old_partitions
ON SCHEDULE EVERY 1 MONTH
STARTS '2026-05-01 04:00:00'
DO
    CALL sp_drop_old_partitions();


-- 등록된 이벤트 확인
SHOW EVENTS;

-- 이벤트 활성화 여부 확인
SHOW VARIABLES LIKE 'event_scheduler';
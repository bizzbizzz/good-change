CREATE TABLE member (
    member_id   BIGINT          NOT NULL AUTO_INCREMENT,
    role_id     BIGINT          NULL,
    login_id    VARCHAR(50)     NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    name        VARCHAR(50)     NOT NULL,
    birth_date  DATE            NOT NULL,
    gender      ENUM('MALE', 'FEMALE') NOT NULL,
    phone       VARCHAR(20)     NOT NULL,
    address     VARCHAR(255)    NOT NULL,
    email       VARCHAR(100)    NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    referrer_id BIGINT          NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (member_id),
    UNIQUE KEY uq_login_id (login_id),
    CONSTRAINT fk_member_role
        FOREIGN KEY (role_id)
            REFERENCES role (role_id)
            ON DELETE SET NULL,
    CONSTRAINT fk_member_referrer
        FOREIGN KEY (referrer_id)
            REFERENCES member (member_id)
            ON DELETE SET NULL
);

-- 카드 테이블 (member 다음에 생성)
CREATE TABLE card (
    card_id     BIGINT          NOT NULL AUTO_INCREMENT,
    member_id   BIGINT          NOT NULL,
    card_number CHAR(16)        NOT NULL,
    card_alias  VARCHAR(50)     NULL,
    card_type   ENUM('CREDIT', 'DEBIT') NOT NULL,
    status      ENUM('ACTIVE', 'BLOCKED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME       NOT NULL,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (card_id),
    UNIQUE KEY uq_card_number (card_number),
    CONSTRAINT fk_card_member
        FOREIGN KEY (member_id)
        REFERENCES member (member_id)
        ON DELETE RESTRICT,
    INDEX idx_card_member_id (member_id)
);



-- merchant 먼저 생성
CREATE TABLE merchant (
    merchant_id     BIGINT          NOT NULL AUTO_INCREMENT,
    login_id        VARCHAR(50)     NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    merchant_name   VARCHAR(100)    NOT NULL,
    representative  VARCHAR(50)     NOT NULL,
    business_number VARCHAR(20)     NOT NULL,
    contact         VARCHAR(20)     NOT NULL,
    phone           VARCHAR(20)     NOT NULL,
    address         VARCHAR(255)    NOT NULL,
    homepage        VARCHAR(255)    NULL,
    manager_name    VARCHAR(50)     NULL,
    email           VARCHAR(100)    NULL,
    referrer_id     BIGINT          NULL,
    terminal_id     VARCHAR(100)    NULL,
    created_at  DATETIME       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (merchant_id),
    UNIQUE KEY uq_merchant_login_id        (login_id),
    UNIQUE KEY uq_merchant_business_number (business_number),
    CONSTRAINT fk_merchant_referrer
        FOREIGN KEY (referrer_id)
        REFERENCES merchant (merchant_id)
        ON DELETE SET NULL
);

-- merchant 다음에 생성
CREATE TABLE merchant_category (
    category_id     BIGINT          NOT NULL AUTO_INCREMENT,
    merchant_id     BIGINT          NOT NULL,
    category_name   VARCHAR(50)     NOT NULL,
    created_at  DATETIME       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (category_id),
    CONSTRAINT fk_merchant_category
        FOREIGN KEY (merchant_id)
        REFERENCES merchant (merchant_id)
        ON DELETE CASCADE
);

CREATE TABLE payment (
    payment_id               BIGINT       NOT NULL AUTO_INCREMENT,
    card_id                  BIGINT       NULL,
    koces_ip                 VARCHAR(50)  NULL,
    message_number           VARCHAR(50)  NULL,
    institution_code         VARCHAR(20)  NULL,
    transmission_date        VARCHAR(8)   NULL,
    trace_number             VARCHAR(50)  NULL,
    terminal_id              VARCHAR(100) NULL,
    business_number          VARCHAR(20)  NULL,
    merchant_number          VARCHAR(50)  NULL,
    merchant_name            VARCHAR(100) NULL,
    representative           VARCHAR(50)  NULL,
    phone                    VARCHAR(20)  NULL,
    address                  VARCHAR(255) NULL,
    card_type                VARCHAR(20)  NULL,
    input_method             VARCHAR(20)  NULL,
    track                    VARCHAR(255) NULL,
    amount                   BIGINT       NULL,
    transaction_type         VARCHAR(20)  NULL,
    approval_number          VARCHAR(50)  NULL,
    response_code            VARCHAR(10)  NULL,
    cancel_code              VARCHAR(20)  NULL,
    original_trade_date      VARCHAR(8)   NULL,
    original_approval_number VARCHAR(50)  NULL,
    acquirer_code            VARCHAR(20)  NULL,
    acquirer_name            VARCHAR(100) NULL,
    filter_value             VARCHAR(255) NULL,
    status                   ENUM('SUCCESS', 'FAILED', 'CANCELED') NOT NULL DEFAULT 'SUCCESS',
    created_at  DATETIME       NOT NULL,
    updated_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (payment_id),
    CONSTRAINT fk_payment_card
        FOREIGN KEY (card_id)
        REFERENCES card (card_id)
        ON DELETE RESTRICT,
    INDEX idx_card_id           (card_id),
    INDEX idx_merchant_number   (merchant_number),
    INDEX idx_approval_number   (approval_number),
    INDEX idx_trace_number      (trace_number),
    INDEX idx_transmission_date (transmission_date)
);

CREATE TABLE settlement (
    settlement_id     BIGINT       NOT NULL AUTO_INCREMENT,
    merchant_id       BIGINT       NOT NULL,
    merchant_name     VARCHAR(100) NOT NULL,
    business_number   VARCHAR(20)  NOT NULL,
    login_id          VARCHAR(50)  NOT NULL,
    settlement_date   DATE         NOT NULL,
    settlement_amount BIGINT       NOT NULL,
    status            ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME       NOT NULL,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (settlement_id),
    CONSTRAINT fk_settlement_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchant (merchant_id)
        ON DELETE RESTRICT,
    INDEX idx_settlement_date            (settlement_date),
    INDEX idx_settlement_business_number (business_number)
);


CREATE TABLE allowed_ip (
                            ip_id      BIGINT       NOT NULL AUTO_INCREMENT,
                            ip_address VARCHAR(50)  NOT NULL,
                            description VARCHAR(100) NULL,
                            created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            PRIMARY KEY (ip_id),
                            UNIQUE KEY uq_ip_address (ip_address)
);

-- role 테이블 생성
CREATE TABLE role (
      role_id     BIGINT          NOT NULL AUTO_INCREMENT,
      role_name   VARCHAR(20)     NOT NULL,
      description VARCHAR(100)    NULL,
      created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

      PRIMARY KEY (role_id),
      UNIQUE KEY uq_role_name (role_name)
);
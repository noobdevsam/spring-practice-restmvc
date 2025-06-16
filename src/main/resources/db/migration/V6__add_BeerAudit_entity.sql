CREATE TABLE beer_audit
(
    audit_id           VARCHAR(36) NOT NULL,
    id                 VARCHAR(36) NOT NULL,
    version            INT NULL,
    beer_name          VARCHAR(50) NULL,
    beer_style         SMALLINT NULL,
    upc                VARCHAR(255) NULL,
    quantity_on_hand   INT NULL,
    price              DECIMAL NULL,
    created_date       datetime NULL,
    update_date        datetime NULL,
    created_date_audit datetime NULL,
    principal_name     VARCHAR(255) NULL,
    audit_event_type   VARCHAR(255) NULL,
    CONSTRAINT pk_beeraudit PRIMARY KEY (audit_id)
);
CREATE TABLE medications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    generic_name VARCHAR(100),
    description TEXT,
    dosage VARCHAR(50),
    form VARCHAR(50),
    manufacturer VARCHAR(50),
    batch_number VARCHAR(50),
    price DOUBLE NOT NULL,
    stock_quantity INT NOT NULL,
    side_effects VARCHAR(255),
    contraindications VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_medications_name UNIQUE (name)
);

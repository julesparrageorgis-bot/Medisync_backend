CREATE TABLE monthly_financial_reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    report_month VARCHAR(7) NOT NULL,
    total_revenue DECIMAL(19, 2) NOT NULL,
    unpaid_invoices BIGINT NOT NULL,
    total_appointments BIGINT NOT NULL,
    room_occupancy_rate DOUBLE NOT NULL,
    no_show_rate DOUBLE NOT NULL,
    generated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_monthly_financial_reports_month UNIQUE (report_month)
);

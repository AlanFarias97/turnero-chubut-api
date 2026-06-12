ALTER TABLE app_users ADD COLUMN first_name VARCHAR(80);
ALTER TABLE app_users ADD COLUMN last_name VARCHAR(80);
ALTER TABLE app_users ADD COLUMN phone_number VARCHAR(40);
ALTER TABLE app_users ADD COLUMN address VARCHAR(180);

UPDATE app_users
SET
    first_name = display_name,
    last_name = '-',
    phone_number = '-',
    address = '-'
WHERE first_name IS NULL
   OR last_name IS NULL
   OR phone_number IS NULL
   OR address IS NULL;

ALTER TABLE app_users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE app_users ALTER COLUMN last_name SET NOT NULL;
ALTER TABLE app_users ALTER COLUMN phone_number SET NOT NULL;
ALTER TABLE app_users ALTER COLUMN address SET NOT NULL;

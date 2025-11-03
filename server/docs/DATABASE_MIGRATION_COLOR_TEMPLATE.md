# 📊 Database Migration Guide - Thêm Color và Template cho CV

## 🎯 Mục đích

Thêm hai cột mới `color` và `template` vào bảng `cvs` để lưu trữ thông tin về màu sắc và template layout của mỗi CV.

---

## 🔧 Các bước Migration

### 1️⃣ Tạo Migration Script

Tạo file migration mới trong thư mục migrations của bạn (tùy theo framework):

#### Flyway Migration (SQL)

**File**: `V2__add_color_template_to_cvs.sql`

```sql
-- Add color column with default value
ALTER TABLE cvs
ADD COLUMN color VARCHAR(20) NOT NULL DEFAULT '#3498db';

-- Add template column with default value
ALTER TABLE cvs
ADD COLUMN template VARCHAR(50) NOT NULL DEFAULT 'modern';

-- Add comment for documentation
COMMENT ON COLUMN cvs.color IS 'Primary color theme for CV in hex format (e.g., #3498db)';
COMMENT ON COLUMN cvs.template IS 'Template layout type (e.g., modern, classic, minimal)';

-- Optional: Create index for filtering by template
CREATE INDEX idx_cvs_template ON cvs(template);

-- Optional: Add check constraint for valid hex color format
ALTER TABLE cvs
ADD CONSTRAINT chk_color_format
CHECK (color ~ '^#[0-9A-Fa-f]{6}$');

-- Optional: Add check constraint for valid template values
ALTER TABLE cvs
ADD CONSTRAINT chk_template_values
CHECK (template IN ('modern', 'classic', 'minimal', 'creative', 'executive', 'compact'));
```

---

### 2️⃣ Liquibase Migration (XML)

**File**: `db/changelog/changes/v002-add-color-template.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.0.xsd">

    <changeSet id="2" author="developer">
        <comment>Add color and template columns to cvs table</comment>

        <!-- Add color column -->
        <addColumn tableName="cvs">
            <column name="color" type="VARCHAR(20)" defaultValue="#3498db">
                <constraints nullable="false"/>
            </column>
        </addColumn>

        <!-- Add template column -->
        <addColumn tableName="cvs">
            <column name="template" type="VARCHAR(50)" defaultValue="modern">
                <constraints nullable="false"/>
            </column>
        </addColumn>

        <!-- Add index for template -->
        <createIndex indexName="idx_cvs_template" tableName="cvs">
            <column name="template"/>
        </createIndex>

        <rollback>
            <dropIndex indexName="idx_cvs_template" tableName="cvs"/>
            <dropColumn tableName="cvs" columnName="template"/>
            <dropColumn tableName="cvs" columnName="color"/>
        </rollback>
    </changeSet>

</databaseChangeLog>
```

---

### 3️⃣ JPA/Hibernate Auto-DDL (Development Only)

Nếu sử dụng `spring.jpa.hibernate.ddl-auto=update` trong development:

**application.yml**

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update # Chỉ dùng trong development
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

Hibernate sẽ tự động thêm cột dựa trên Entity definition:

```java
@Entity
@Table(name = "cvs")
public class CV {
    // ... existing fields

    @Column(nullable = false)
    private String color = "#3498db";

    @Column(nullable = false)
    private String template = "modern";
}
```

⚠️ **Lưu ý**: Không dùng `ddl-auto=update` trong production!

---

### 4️⃣ Manual Migration (Production)

Nếu chạy migration manual trong production:

```sql
-- 1. Kiểm tra cấu trúc bảng hiện tại
DESCRIBE cvs;
-- hoặc
\d cvs

-- 2. Thêm cột color
ALTER TABLE cvs
ADD COLUMN color VARCHAR(20);

-- 3. Set giá trị mặc định cho các bản ghi cũ
UPDATE cvs
SET color = '#3498db'
WHERE color IS NULL;

-- 4. Đặt constraint NOT NULL
ALTER TABLE cvs
ALTER COLUMN color SET NOT NULL;

-- 5. Set default value cho insert mới
ALTER TABLE cvs
ALTER COLUMN color SET DEFAULT '#3498db';

-- 6. Lặp lại với template
ALTER TABLE cvs
ADD COLUMN template VARCHAR(50);

UPDATE cvs
SET template = 'modern'
WHERE template IS NULL;

ALTER TABLE cvs
ALTER COLUMN template SET NOT NULL;

ALTER TABLE cvs
ALTER COLUMN template SET DEFAULT 'modern';

-- 7. Verify
SELECT id, title, color, template FROM cvs LIMIT 5;
```

---

## ✅ Validation & Testing

### 1. Kiểm tra cấu trúc bảng sau migration

```sql
-- PostgreSQL
SELECT column_name, data_type, column_default, is_nullable
FROM information_schema.columns
WHERE table_name = 'cvs'
AND column_name IN ('color', 'template');

-- MySQL
SHOW COLUMNS FROM cvs WHERE Field IN ('color', 'template');
```

Expected output:

```
column_name | data_type      | column_default | is_nullable
------------|----------------|----------------|------------
color       | varchar(20)    | '#3498db'      | NO
template    | varchar(50)    | 'modern'       | NO
```

### 2. Test INSERT với giá trị mới

```sql
-- Test với explicit values
INSERT INTO cvs (id, user_id, title, color, template, privacy, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'user-uuid-here',
    'Test CV',
    '#10b981',
    'classic',
    'PRIVATE',
    NOW(),
    NOW()
);

-- Test với default values (không truyền color/template)
INSERT INTO cvs (id, user_id, title, privacy, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'user-uuid-here',
    'Test CV 2',
    'PRIVATE',
    NOW(),
    NOW()
);

-- Verify defaults được áp dụng
SELECT id, title, color, template FROM cvs ORDER BY created_at DESC LIMIT 2;
```

### 3. Test UPDATE

```sql
-- Update color
UPDATE cvs
SET color = '#8b5cf6'
WHERE id = 'cv-id-here';

-- Update template
UPDATE cvs
SET template = 'minimal'
WHERE id = 'cv-id-here';

-- Verify
SELECT id, title, color, template FROM cvs WHERE id = 'cv-id-here';
```

---

## 🔄 Rollback Plan

Nếu cần rollback migration:

```sql
-- Drop constraints first (nếu có)
ALTER TABLE cvs DROP CONSTRAINT IF EXISTS chk_color_format;
ALTER TABLE cvs DROP CONSTRAINT IF EXISTS chk_template_values;

-- Drop index
DROP INDEX IF EXISTS idx_cvs_template;

-- Drop columns
ALTER TABLE cvs DROP COLUMN IF EXISTS template;
ALTER TABLE cvs DROP COLUMN IF EXISTS color;

-- Verify
DESCRIBE cvs;
```

---

## 📊 Data Migration (Nếu có logic đặc biệt)

Nếu muốn migrate dữ liệu cũ với logic phức tạp:

```sql
-- Assign colors based on user preferences or CV type
UPDATE cvs
SET color = CASE
    WHEN title LIKE '%Engineer%' OR title LIKE '%Developer%' THEN '#3498db'
    WHEN title LIKE '%Designer%' OR title LIKE '%Creative%' THEN '#8b5cf6'
    WHEN title LIKE '%Manager%' OR title LIKE '%Executive%' THEN '#64748b'
    ELSE '#3498db'
END
WHERE color IS NULL;

-- Assign templates based on creation date or user type
UPDATE cvs
SET template = CASE
    WHEN created_at > '2024-01-01' THEN 'modern'
    ELSE 'classic'
END
WHERE template IS NULL;
```

---

## 🚀 Deployment Checklist

- [ ] **Backup database** trước khi chạy migration
- [ ] **Test migration script** trên database development/staging
- [ ] **Review rollback plan** để đảm bảo có thể revert nếu cần
- [ ] **Run migration** trong production với downtime window (nếu cần)
- [ ] **Verify data integrity** sau migration
- [ ] **Monitor application logs** sau deployment
- [ ] **Test create/update CV** từ UI để đảm bảo hoạt động đúng

---

## 🛠️ Application.properties Configuration

### Development

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Production

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

---

## 📝 Migration Script Execution Order

1. **Backup** database hiện tại
2. **Stop** application (nếu cần downtime)
3. **Run** migration script
4. **Verify** schema changes
5. **Test** data operations (INSERT, UPDATE, SELECT)
6. **Deploy** new application version với updated Entity
7. **Monitor** logs và database performance
8. **Rollback** nếu có issue

---

## 🐛 Common Issues & Solutions

### Issue 1: Default value không được áp dụng

```sql
-- Solution: Set default explicitly
ALTER TABLE cvs ALTER COLUMN color SET DEFAULT '#3498db';
ALTER TABLE cvs ALTER COLUMN template SET DEFAULT 'modern';
```

### Issue 2: Existing rows có giá trị NULL

```sql
-- Solution: Update existing rows
UPDATE cvs SET color = '#3498db' WHERE color IS NULL;
UPDATE cvs SET template = 'modern' WHERE template IS NULL;
```

### Issue 3: JPA Entity không sync với database

```java
// Solution: Restart application hoặc force schema validation
spring.jpa.hibernate.ddl-auto=validate
```

---

## 📚 References

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [Spring Boot Database Migration](https://spring.io/guides/gs/accessing-data-jpa/)
- [PostgreSQL ALTER TABLE](https://www.postgresql.org/docs/current/sql-altertable.html)

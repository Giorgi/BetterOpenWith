ALTER TABLE HandleItem ADD COLUMN "UseGlobalTimeout"  INTEGER DEFAULT 1;
ALTER TABLE HandleItem ADD COLUMN "customTimeout" integer NOT NULL  DEFAULT -1;
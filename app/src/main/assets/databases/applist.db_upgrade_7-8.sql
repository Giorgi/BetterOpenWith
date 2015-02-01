CREATE TABLE "HiddenApp" ("_id" INTEGER PRIMARY KEY  NOT NULL , "itemId" INTEGER , "siteId" INTEGER, "packageName" TEXT NOT NULL );

INSERT INTO "HandleItem" VALUES(12,NULL,NULL,'ic_action_phone_start','dialer','ic_action_phone_start','tel:123456',NULL,NULL,1,0,'com.aboutmycode.betteropenwith.app.dialer',-1,1,NULL,NULL);
INSERT INTO "HandleItem" VALUES(13,NULL,NULL,'ic_action_calendar_month','calendar','ic_action_calendar_month',NULL,'vnd.android.cursor.item/event',NULL,1,0,'com.aboutmycode.betteropenwith.app.calendar',-1,1,NULL,NULL);
INSERT INTO "HandleItem" VALUES(14,NULL,NULL,'ic_action_camera','camera','ic_action_camera',NULL,NULL,NULL,1,0,'com.aboutmycode.betteropenwith.app.camera',-1,1,NULL,NULL);

update HandleItem set customTimeout= -1 where useGlobalTimeout=1 and customTimeout=5;
ALTER TABLE "HandleItem" ADD COLUMN "lastPackageName" TEXT;
ALTER TABLE "HandleItem" ADD COLUMN "lastClassName" TEXT;

INSERT INTO "HandleItem" VALUES(8,NULL,NULL,'ic_action_map32','geo','ic_action_map32_light','geo:41.716667,44.783333',NULL,NULL,1,0,'com.aboutmycode.openwith.app.GeoLocationActivity',5,1,NULL,NULL);
INSERT INTO "HandleItem" VALUES(9,NULL,NULL,'ic_action_epub','epub','ic_action_epub_light','file:///mnt/sdcard/file.epub','application/epub+zip',NULL,1,0,'com.aboutmycode.openwith.app.EpubActivity',5,1,NULL,NULL);
INSERT INTO "HandleItem" VALUES(10,NULL,NULL,'ic_action_txt','txt','ic_action_txt_light','file:///mnt/sdc/file.txt','text/plain','',1,0,'com.aboutmycode.openwith.app.TextActivity',5,1,NULL,NULL);
INSERT INTO "HandleItem" VALUES(11,NULL,NULL,'ic_action_email','email','ic_action_email_light','mailto:android@aboutmycode.com',NULL,NULL,1,0,'com.aboutmycode.openwith.app.EmailActivity',5,1,NULL,NULL);

CREATE TABLE "Site" ("_id" integer PRIMARY KEY ,"packageName" TEXT,"className" TEXT,"name" TEXT, "iconResource" TEXT, "intentData" TEXT,"intentType" TEXT,"installed" INTEGER,"enabled" INTEGER,"skipList" INTEGER,"appComponentName" TEXT,"customTimeout" integer NOT NULL  DEFAULT (5) , "useGlobalTimeout" INTEGER DEFAULT 1, "domain" TEXT, "lastPackageName" TEXT, "lastClassName" TEXT);

INSERT INTO "Site" VALUES(1,NULL,NULL,'Youtube',NULL,'http://www.youtube.com/watch?v=z5a15wEMKCY',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'youtube',NULL,NULL);
INSERT INTO "Site" VALUES(2,NULL,NULL,'Twitter',NULL,'http://twitter.com/armarn/status/502675535630438401',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'twitter',NULL,NULL);
INSERT INTO "Site" VALUES(4,NULL,NULL,'Play Store',NULL,'https://play.google.com/store/apps/details?id=com.aboutmycode.betteropenwith',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'play.google.com',NULL,NULL);
INSERT INTO "Site" VALUES(5,NULL,NULL,'Google +',NULL,'https://plus.google.com/communities/110383670951588070492',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'plus.google.com',NULL,NULL);
INSERT INTO "Site" VALUES(6,NULL,NULL,'Reddit',NULL,'http://www.reddit.com/r/android',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'reddit',NULL,NULL);
INSERT INTO "Site" VALUES(7,NULL,NULL,'Wikipedia',NULL,'http://en.wikipedia.org/wiki/Android',NULL,1,1,0,'com.aboutmycode.betteropenwith.app.BrowserHandlerActivity',5,1,'wikipedia',NULL,NULL);

alter table HandleItem RENAME TO tmp_HandleItem;

CREATE TABLE HandleItem (_id integer PRIMARY KEY ,packageName TEXT,className TEXT,darkIconResource TEXT,nameResource TEXT,lightIconResource TEXT,
intentData TEXT,intentType TEXT,installed INTEGER,enabled INTEGER,skipList INTEGER,
appComponentName TEXT,customTimeout integer NOT NULL  DEFAULT (-1) , useGlobalTimeout INTEGER DEFAULT 1);

INSERT INTO HandleItem 
SELECT _id,packageName,className,darkIconResource,nameResource,lightIconResource,intentData,intentType,installed,
enabled,skipList,appComponentName,customTimeout,1
FROM tmp_HandleItem;

drop TABLE tmp_HandleItem;
DROP TABLE IF EXISTS City CASCADE;
DROP TABLE IF EXISTS UserAccountUserAccount CASCADE;
DROP TABLE IF EXISTS UserAccount CASCADE;
DROP TABLE IF EXISTS Route CASCADE;
DROP TABLE IF EXISTS TIP CASCADE;
DROP TABLE IF EXISTS TIPtype CASCADE;
DROP TABLE IF EXISTS Comment CASCADE;
DROP TABLE IF EXISTS Rating CASCADE;
DROP TABLE IF EXISTS TIPUserAccount CASCADE;
DROP TABLE IF EXISTS UserAccountRoute CASCADE;
DROP TABLE IF EXISTS RouteTIP CASCADE;
DROP TABLE IF EXISTS OSMType CASCADE;
DROP TABLE IF EXISTS Admin CASCADE;
DROP TABLE IF EXISTS Config CASCADE;

CREATE TABLE City(
  id SERIAL,
  name VARCHAR(50),
  geom GEOMETRY,
  osmId BIGINT,
  CONSTRAINT City_PK PRIMARY KEY(id),
  CONSTRAINT City_Geometry CHECK(geometrytype(geom) = ANY(ARRAY['POLYGON','MULTIPOLYGON']) AND geom IS NOT NULL)
);
CREATE INDEX City_Geometry_Gix ON City USING GIST (geom);

CREATE TABLE UserAccount(
  id SERIAL,
  name VARCHAR(50),
  registrationDate TIMESTAMP,
  facebookUserId BIGINT,
  facebookProfileUrl TEXT,
  facebookProfilePhotoUrl TEXT,
  CONSTRAINT UserAccount_PK PRIMARY KEY(id),
  CONSTRAINT UserAccount_UNIQUE UNIQUE(facebookUserId)
);

CREATE TABLE Route(
  id SERIAL,
  name VARCHAR(100),
  description TEXT,
  geom GEOMETRY,
  googleMapsUrl TEXT,
  userId INTEGER,
  travelMode VARCHAR(50),
  CONSTRAINT Route_PK PRIMARY KEY(id),
  CONSTRAINT Route_UserAccount_FK FOREIGN KEY (userId) REFERENCES UserAccount(id),
  CONSTRAINT Route_Geometry CHECK(geometrytype(geom) = ANY(ARRAY['LINESTRING','MULTILINESTRING']) AND geom IS NOT NULL)
);
CREATE INDEX Route_Geometry_Gix ON Route USING GIST (geom);

CREATE TABLE TIPtype(
  id SERIAL,
  name VARCHAR(20),
  icon VARCHAR(20),
  CONSTRAINT TIPtype_PK PRIMARY KEY(id)
);

CREATE TABLE TIP(
  id SERIAL,
  typeId INTEGER,
  name VARCHAR(50),
  geom GEOMETRY,
  address VARCHAR(255),
  description TEXT,
  photoUrl TEXT,
  infoUrl TEXT,
  googleMapsUrl TEXT,
  cityId INTEGER,
  osmId BIGINT,
  reviewed BOOLEAN DEFAULT FALSE,
  CONSTRAINT TIP_PK PRIMARY KEY(id),
  CONSTRAINT TIP_TIPtype_FK FOREIGN KEY(typeId) REFERENCES TIPtype(id) ON DELETE SET NULL,
  CONSTRAINT TIP_City_FK FOREIGN KEY(cityId) REFERENCES City(id) ON DELETE SET NULL,
  CONSTRAINT TIP_Geometry CHECK(geometrytype(geom) = 'POINT' AND geom IS NOT NULL)
);
CREATE INDEX TIP_Geometry_Gix ON TIP USING GIST (geom);

CREATE TABLE Comment(
  id SERIAL,
  commentText TEXT,
  commentDate TIMESTAMP,
  TIPId INTEGER,
  userId INTEGER,
  CONSTRAINT Comment_PK PRIMARY KEY(id),
  CONSTRAINT Comment_TIP_FK FOREIGN KEY(TIPId) REFERENCES TIP(id) ON DELETE CASCADE,
  CONSTRAINT Comment_UserAccount_FK FOREIGN KEY(userId) REFERENCES UserAccount(id) ON DELETE CASCADE
);

CREATE TABLE Rating(
  id SERIAL,
  ratingValue NUMERIC,
  ratingDate TIMESTAMP,
  TIPId INTEGER,
  userId INTEGER,
  CONSTRAINT Rating_PK PRIMARY KEY(id),
  CONSTRAINT Rating_TIP_FK FOREIGN KEY(TIPId) REFERENCES TIP(id) ON DELETE CASCADE,
  CONSTRAINT Rating_UserAccount_FK FOREIGN KEY(userId) REFERENCES UserAccount(id) ON DELETE CASCADE
);

CREATE TABLE UserAccountUserAccount(
  userAccountId INTEGER,
  friendId INTEGER,
  CONSTRAINT UserAccountUserAccount_PK PRIMARY KEY (userAccountId,friendId),
  CONSTRAINT UserAccountUserAccount_UserAccount_FK FOREIGN KEY (userAccountId) REFERENCES UserAccount(id),
  CONSTRAINT UserAccountUserAccount_Friend_FK FOREIGN KEY (friendId) REFERENCES UserAccount(id)
);

CREATE TABLE TIPUserAccount(
  TIPId INTEGER,
  userId INTEGER,
  favouriteDate TIMESTAMP,
  CONSTRAINT TIPUserAccount_PK PRIMARY KEY(TIPId,userId),
  CONSTRAINT TIPUserAccount_TIP_FK FOREIGN KEY(TIPId) REFERENCES TIP(id) ON DELETE CASCADE,
  CONSTRAINT TIPUserAccount_UserAccount_FK FOREIGN KEY(userId) REFERENCES UserAccount(id) ON DELETE CASCADE
);

CREATE TABLE RouteTIP(
  routeId INTEGER,
  TIPId INTEGER,
  ordination INTEGER,
  CONSTRAINT RouteTIP_PK PRIMARY KEY(routeId,TIPId),
  CONSTRAINT RouteTIP_Route_FK FOREIGN KEY(routeId) REFERENCES Route(id) ON DELETE CASCADE,
  CONSTRAINT RouteTIP_TIP_FK FOREIGN KEY(TIPId) REFERENCES TIP(id) ON DELETE CASCADE
);

CREATE TABLE Admin(
  id SERIAL,
  username VARCHAR(20),
  password VARCHAR(100),
  CONSTRAINT Admin_PK PRIMARY KEY (id)
);

CREATE TABLE Config(
  id SERIAL,
  boundingBox GEOMETRY,
  CONSTRAINT Config_PK PRIMARY KEY (id),
  CONSTRAINT City_Geometry CHECK(geometrytype(boundingBox) = ANY(ARRAY['POLYGON']) AND boundingBox IS NOT NULL)
);

CREATE TABLE OSMType(
  id SERIAL,
  keyName VARCHAR(50),
  value VARCHAR(50),
  TIPTypeId INTEGER,
  CONSTRAINT OSMType_PK PRIMARY KEY (id),
  CONSTRAINT OSMType_Unique_KeyValue UNIQUE (keyName,value),
  CONSTRAINT OSMType_TIPType_FK FOREIGN KEY (TIPTypeId) REFERENCES TIPtype(id)
);

INSERT INTO TIPtype VALUES(1,'Monument','fa-university');
INSERT INTO TIPtype VALUES(2,'Natural Space','fa-leaf');
INSERT INTO TIPtype VALUES(3,'Hotel','fa-bed');
INSERT INTO TIPtype VALUES(4,'Restaurant','fa-cutlery');

INSERT INTO OSMType(keyName, value, TIPTypeId) VALUES ('tourism','hostel',3);
INSERT INTO OSMType(keyName, value, TIPTypeId) VALUES ('tourism','hotel',3);
INSERT INTO OSMType(keyName, value, TIPTypeId) VALUES ('tourism','viewpoint',2);
INSERT INTO OSMType(keyName, value, TIPTypeId) VALUES ('historic','monument',1);
INSERT INTO OSMType(keyName, value, TIPTypeId) VALUES ('amenity','restaurant',4);

INSERT INTO Config VALUES (1,(SELECT ST_GeometryFromText('POLYGON((-8.45496654510498 43.357419050726,-8.45496654510498 43.39341675213421,-8.363170623779297 43.39341675213421,-8.363170623779297 43.357419050726,-8.45496654510498 43.357419050726))')));
create table customers (
     id bigserial NOT NULL,
     "name" varchar(96) NOT NULL,
     primary key (id)
);

INSERT INTO customers (id,"name") VALUES
	 (1,'Fake Company Ltd.'),
	 (2,'Zero Company Ltd.'),
	 (3,'Oracle'),
	 (4,'Microsoft'),
	 (5,'Apple'),
	 (6,'Space, Inc.'),
	 (7,'Good Company, Inc.'),
	 (8,'Bad Company Ltd.'),
	 (9,'Nice Company'),
	 (10,'BatCO');
INSERT INTO customers (id,"name") VALUES
	 (11,'Rearden Steel Ltd.'),
	 (12,'Taggart Transcontinental Inc.'),
	 (13,'NewBlend LLC'),
	 (14,'Blind Spot'),
	 (15,'Lenovo'),
	 (16,'Me & Co.'),
	 (17,'B&W Labs'),
	 (18,'Everything On The TABLE & Co'),
	 (19,'Gorilla'),
	 (20,'Crystal Code & Co');

CREATE TABLE contacts (
	id bigserial NOT NULL,
	customer_id int8 NOT NULL,
	type varchar(16) NOT NULL,
	"name" varchar(50) NOT NULL,
	isPrimary bool NOT NULL,
	"value" varchar(255) NULL,
	PRIMARY KEY (id)
);

ALTER TABLE contacts ADD CONSTRAINT contacts_fk FOREIGN KEY (customer_id) REFERENCES customers(id);

INSERT INTO contacts (id,customer_id,type,"name",isprimary,value) VALUES
	 (1,1,'Email','John Galt',true,'John.Galt@fake-company.com'),
	 (2,1,'Email','Johnny English',false,'johnny.english@fake-company.com'),
	 (3,1,'Email','Lena Bramer',false,'Lena.Bramer@fake-company.com'),
	 (4,2,'Phone','John Galt',true,'555-738-2215'),
	 (5,2,'Email','John Wayne',true,'john.wayne@zero-company.com'),
	 (6,2,'Email','Brad Pit',false,'bp@zero-company.com'),
	 (7,2,'Phone','John Wayne',true,'701-223-4065'),
	 (8,2,'Fax','John Wayne',true,'(701) 223-8202'),
	 (9,3,'Email','Mr. Vain',false,'mr.vain@blind-spot.mr'),
	 (10,3,'Email','Gregory David Roberts',true,'meghan_dicks@blind-spot.mr');
INSERT INTO contacts (id,customer_id,type,"name",isprimary,value) VALUES
	 (11,4,'Email','Gregory David Roberts',true,'gregory_roberts@ms.sun'),
	 (12,4,'Phone','Gregory David Roberts',true,'(555) 647-4155'),
	 (13,8,'Email','Abdel Khader Khan',true,'abdel.khan@bad-company.ltd'),
	 (14,8,'Email','Abdel Khader Khan',false,'sales@bad-company.ltd'),
	 (15,8,'Email','Abdel Khader Khan',false,'office@bad-company.ltd'),
	 (16,8,'Phone','Abdel Khader Khan',true,'555-778-5100'),
	 (17,9,'Email','Linbaba',true,'linbaba@nice-company.ltd'),
	 (18,9,'Email','Linbaba',false,'linbaba1@nice-company.ltd'),
	 (19,9,'Phone','Linbaba',true,'(555) 648-4351'),
	 (20,13,'Email','Prabaker',true,'parabaker@newblend.llc');
INSERT INTO contacts (id,customer_id,type,"name",isprimary,value) VALUES
	 (21,13,'Email','Prabaker',false,'office@newblend.llc'),
	 (22,13,'Email','Prabaker',false,'sales@newblend.llc'),
	 (23,13,'Email','Prabaker',false,'info@newblend.llc'),
	 (24,13,'Phone','Prabaker',true,'(555) 338-0342'),
	 (25,12,'Fax','Prabaker',true,'(555) 394-5233'),
	 (26,12,'Email','Dagny Taggart',true,'dagny.taggart@taggart-trans.inc'),
	 (27,12,'Email','Dagny Taggart',false,'taggart@taggart-trans.inc'),
	 (28,12,'Email','Dagny Taggart',false,'d.taggart@taggart-trans.inc'),
	 (29,12,'Phone','Dagny Taggart',true,'(555) 257-7030'),
	 (30,12,'Fax','Dagny Taggart',true,'(555) 784-5507');
INSERT INTO contacts (id,customer_id,type,"name",isprimary,value) VALUES
	 (31,11,'Email','Lillian Rearden',true,'edk_sami@reardensteel.ltd'),
	 (32,11,'Email','Lillian Rearden',false,'stamills@reardensteel.ltd'),
	 (33,11,'Phone','Lillian Rearden',true,'(555) 713-4520'),
	 (34,11,'Fax','Lillian Rearden',true,'(555) 713-4321'),
	 (35,16,'Email','Ragnar Danneskjold',true,'ragnar.danneskjold@blackandwhite.labs'),
	 (36,19,'Phone','Ragnar Danneskjold',true,'(555) 342-6175'),
	 (37,19,'Email','Francisco d''Anconia',true,'francisco.danconia@gorilla.info'),
	 (38,19,'Email','Robert Stadler',false,'robert.stadler@gorilla.info'),
	 (39,19,'Phone','Francisco d''Anconia',true,'(555) 732-1797'),
	 (40,14,'Email','Ellis Wyatt',true,'ellis.wyatt@lenovo.mars');

CREATE TABLE addresses (
	id bigserial NOT NULL,
	address varchar(196) NOT NULL,
	city varchar(50) NOT NULL,
	customer_id int8 NOT NULL,
	isPrimary bool NOT NULL,
	zip varchar(12) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT addresses_customerid_check CHECK ((customer_id >= 1))
);

ALTER TABLE addresses ADD CONSTRAINT addresses_fk FOREIGN KEY (customer_id) REFERENCES customers(id);

INSERT INTO addresses (id,address,city,customer_id,isPrimary,zip) VALUES
	 (1,'31754 US Hwy 24','Beloit',5,true,'67744420'),
	 (2,'P.O. Box 3776','Beloit',5,false,'67674420'),
	 (3,'P.O. Box 187777','Bismarck',11,true,'5865502'),
	 (4,'157703 Yegan Rd','Bismarck',11,false,'5268502'),
	 (5,'401231 E. Grand River Ave.','Portland',4,true,'2648875'),
	 (6,'P.O. Box 555788','Baldwin',18,true,'3260511'),
	 (7,'58458282 Gainesville Highway','Baldwin',18,false,'3260511'),
	 (8,'18759 Mountain road','Shamokin',19,true,'1237872'),
	 (9,'14578519 Collier Road','Delhi',13,true,'953226315'),
	 (10,'P. O. Box 504639','Falkville',6,true,'3575622');
INSERT INTO addresses (id,address,city,customer_id,isPrimary,zip) VALUES
	 (11,'429834 Hwy 33561 SW','Falkville',6,false,'3345622'),
	 (12,'P.O. Box 36370','Saint Ansgar',8,true,'5560472'),
	 (13,'28308 E. 6th','Saint Ansgar',8,false,'5360472'),
	 (14,'P.O. Box 947840','Rapid City',8,true,'57346701'),
	 (15,'4245786 Omaha St.','Rapid City',8,false,'5367701'),
	 (16,'27874 Herring Road','Morton',10,true,'33469117'),
	 (17,'1301 West Harrisburg Avenue','Rheems',14,true,'13467570'),
	 (18,'P. O. Box 23266','Rheems',14,false,'13467570'),
	 (19,'3368200 East Second Street','Neosho',12,true,'64534850'),
	 (20,'P.O. Box 43084','Gerald',13,true,'64573037');
INSERT INTO addresses (id,address,city,customer_id,isPrimary,zip) VALUES
	 (21,'2036780 West Springfield Road','Gerald',13,false,'68753037'),
	 (22,'5368213 W. Main St.','Turlock',20,true,'9456455380'),
	 (23,'P.O. Box 2363687  Ext 3323','Turlock',20,false,'94565380'),
	 (24,'4124324 15th St. S.E.','Demotte',18,true,'14622310'),
	 (25,'P.O. Box 460351','Demotte',18,false,'46353310'),
	 (26,'P.O. Box 92492','Kirksville',15,true,'6350531'),
	 (27,'312410 W Potler Ave.','Kirksville',15,false,'63225501'),
	 (28,'1156510 W, 450 N','Washington',19,true,'47525601'),
	 (29,'P.O. BOX 536239','Washington',19,false,'47643501'),
	 (30,'441461 N. 3rd St.','Denver',17,true,'1734517');
INSERT INTO addresses (id,address,city,customer_id,isPrimary,zip) VALUES
	 (31,'361246134611 Old Oakwood Road','Oakwood',17,true,'6443536'),
	 (32,'935717 6th St','Howard Lake',17,true,'5545349'),
	 (33,'Box 5424579','Howard Lake',17,false,'5985349'),
	 (34,'2245715 Kreamer Ave.','Kreamer',16,true,'1867833'),
	 (35,'P.O. Box 254738','Kreamer',16,false,'1783863'),
	 (36,'825705 Nokomis St.','Alexandria',12,true,'5637608'),
	 (37,'P.O. Box 572358','Alexandria',12,false,'5638908'),
	 (38,'22357 Stevens Road','Stevens',3,true,'175678'),
	 (39,'P.O. Box 23577','Stevens',3,false,'447890'),
	 (40,'4225379 Cherokee','Saint Joseph',9,true,'76576i04');


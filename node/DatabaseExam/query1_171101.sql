create database iot;
use iot;

create table user(
id integer primary key auto_increment,
email text,
password text,
name text,
age integer);

create table device(
id integer primary key auto_increment,
user_id integer,
mac_address text,
created_at DATETIME default current_timestamp);


insert into user(email, password, name, age)
values('test@test.com', 'test1234', 'Test', 20);

select * from user;
select * from user where id=1;

update user set name='Test!!!' WHERE id=1;

delete from user where id=1;

select count(*) from user;
CREATE TABLE IF NOT EXISTS categories (
    category_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    category_name varchar(255),
    parent_category_id bigint,
    primary key (category_id),
    foreign key (parent_category_id) references categories (category_id)
);

CREATE TABLE IF NOT EXISTS  member (
   member_id bigint not null auto_increment,
   created_date datetime(6),
    modified_date datetime(6),
    email varchar(255),
    nickname varchar(255),
    o_auth_id varchar(255),
    member_role varchar(255),
    user_password varchar(255),
    username varchar(255),
    withdrawal bit,
    primary key (member_id)
);

CREATE TABLE IF NOT EXISTS program (
    program_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    description varchar(10000),
    due_date date,
    max_member integer,
    price integer,
    program_name varchar(255),
    program_state varchar(255),
    category_id bigint,
    primary key (program_id),
    foreign key (category_id) references categories (category_id)
);


CREATE TABLE IF NOT EXISTS mission (
    mission_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    due_date date,
    content varchar(10000),
    title varchar(255),
    program_id bigint,
    member_id bigint,
    primary key (mission_id),
    foreign key (program_id) references program (program_id),
    foreign key (member_id) references member (member_id)
);

CREATE TABLE IF NOT EXISTS applicants (
                                          applicant_id bigint not null auto_increment,
                                          created_date datetime(6),
    modified_date datetime(6),
    approval bit,
    message varchar(1000),
    program_role varchar(255),
    member_id bigint,
    program_id bigint,
    primary key (applicant_id),
    foreign key (member_id) references member (member_id),
    foreign key (program_id) references program (program_id)
    );

CREATE TABLE IF NOT EXISTS board (
    board_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    content varchar(10000),
    title varchar(255),
    mission_id bigint,
    member_id bigint,
    temporal bit,
    primary key (board_id),
    foreign key (mission_id) references mission (mission_id),
    foreign key (member_id) references member (member_id)
);

CREATE TABLE IF NOT EXISTS attach_image (
    attach_image_id bigint not null auto_increment,
    file_path varchar(255),
    origin_filename varchar(255),
    save_filename varchar(255),
    media_type varchar(255),
    board_id bigint,
    created_date datetime(6),
    modified_date datetime(6),
    primary key (attach_image_id),
    foreign key (board_id) references board (board_id)
);

CREATE TABLE IF NOT EXISTS career (
    career_id bigint not null auto_increment,
    company_name varchar(255),
    end_date date,
    program_position varchar(255),
    start_date date,
    member_id bigint,
    primary key (career_id),
    foreign key (member_id) references member (member_id)
);

CREATE TABLE IF NOT EXISTS  mentee (
    mentee_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    payment bit,
    member_id bigint,
    program_id bigint,
    primary key (mentee_id),
    foreign key (member_id) references member (member_id),
    foreign key (program_id) references program (program_id)
);

CREATE TABLE IF NOT EXISTS  mentor (
                                       mentor_id bigint not null auto_increment,
                                       created_date datetime(6),
    modified_date datetime(6),
    host bit,
    member_id bigint,
    program_id bigint,
    primary key (mentor_id),
    foreign key (member_id) references member (member_id),
    foreign key (program_id) references program (program_id)
    );

CREATE TABLE IF NOT EXISTS reply (
    reply_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    content varchar(10000),
    board_id bigint,
    member_id bigint,
    removal bit,
    primary key (reply_id),
    foreign key (board_id) references board (board_id),
    foreign key (member_id) references member (member_id)
);

CREATE TABLE IF NOT EXISTS hibernate_sequence (
    next_val bigint
);
insert ignore into hibernate_sequence values ( 1 );

-- 이모지 설정
SET collation_connection = 'utf8mb4_unicode_ci';
ALTER DATABASE mentoreedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE categories CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE member CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE program CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mission CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE applicants CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE board CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE attach_image CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE career CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mentee CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mentor CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE reply CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 카테고리 데이터
insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'Programming', null
from DUAL where not exists(select * from categories where category_name = 'Programming');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'Music', null
from DUAL where not exists(select * from categories where category_name = 'Music');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'Creative', null
from DUAL where not exists(select * from categories where category_name = 'Creative');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'JAVA', 1
from DUAL where not exists(select * from categories where category_name = 'JAVA');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'PYTHON', 1
from DUAL where not exists(select * from categories where category_name = 'PYTHON');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'C/C++', 1
from DUAL where not exists(select * from categories where category_name = 'C/C++');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'JavaScript', 1
from DUAL where not exists(select * from categories where category_name = 'JavaScript');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'C#', 1
from DUAL where not exists(select * from categories where category_name = 'C#');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'Ruby', 1
from DUAL where not exists(select * from categories where category_name = 'Ruby');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'Go', 1
from DUAL where not exists(select * from categories where category_name = 'Go');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'GUITAR', 2
from DUAL where not exists(select * from categories where category_name = 'GUITAR');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'PIANO', 2
from DUAL where not exists(select * from categories where category_name = 'PIANO');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'DRUM', 2
from DUAL where not exists(select * from categories where category_name = 'DRUM');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'VOCAL', 2
from DUAL where not exists(select * from categories where category_name = 'VOCAL');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'SAXOPHONE', 2
from DUAL where not exists(select * from categories where category_name = 'SAXOPHONE');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'KALIMBA', 2
from DUAL where not exists(select * from categories where category_name = 'KALIMBA');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'HARMONICA', 2
from DUAL where not exists(select * from categories where category_name = 'HARMONICA');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'UX/UI', 3
from DUAL where not exists(select * from categories where category_name = 'UX/UI');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'GRAPHIC', 3
from DUAL where not exists(select * from categories where category_name = 'GRAPHIC');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'EDITING', 3
from DUAL where not exists(select * from categories where category_name = 'EDITING');

insert into categories (created_date, modified_date, category_name, parent_category_id)
select now(), now(), 'SOUNDS', 3
from DUAL where not exists(select * from categories where category_name = 'SOUNDS');

insert into member (created_date, modified_date, email, nickname, o_auth_id, member_role, user_password, username, withdrawal)
select now(), now(), 'tester@email.com', 'testerNickname', 'FORM', 'MENTOR', '{bcrypt}$2a$10$PAeTU0ut57vvZsZLJG4RKeSqUUMEuSlhloXXpeRT4avZmoVHTlDcq', 'memberA', false
from DUAL where not exists(select * from member where email = 'tester@email.com');





drop table if exists attach_image;
drop table if exists applicants;
drop table if exists career;
drop table if exists mentor;
drop table if exists mentee;
drop table if exists reply;
drop table if exists board;
drop table if exists mission;
drop table if exists member;
drop table if exists program;
drop table if exists categories;
drop table if exists hibernate_sequence;

CREATE TABLE categories (
    category_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    category_name varchar(255),
    parent_category_id bigint,
    primary key (category_id),
    foreign key (parent_category_id) references categories (category_id)
    );

CREATE TABLE member (
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

CREATE TABLE program (
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

CREATE TABLE mission (
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

CREATE TABLE applicants (
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

CREATE TABLE board (
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

CREATE TABLE attach_image (
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

CREATE TABLE career (
    career_id bigint not null auto_increment,
    company_name varchar(255),
    end_date date,
    program_position varchar(255),
    start_date date,
    member_id bigint,
    primary key (career_id),
    foreign key (member_id) references member (member_id)
    );

CREATE TABLE mentee (
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

CREATE TABLE mentor (
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

CREATE TABLE reply (
    reply_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    content varchar(255),
    board_id bigint,
    member_id bigint,
    removal bit,
    primary key (reply_id),
    foreign key (board_id) references board (board_id),
    foreign key (member_id) references member (member_id)
    );

CREATE TABLE hibernate_sequence (
    next_val bigint
);
insert into hibernate_sequence values ( 1 );
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

create table applicants (
    applicant_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    approval bit,
    message varchar(255),
    program_role varchar(255),
    member_id bigint,
    program_id bigint,
    primary key (applicant_id)
);

create table board (
    board_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    content varchar(255),
    title varchar(255),
    mission_id bigint,
    member_id bigint,
    primary key (board_id)
);

create table career (
    career_id bigint not null auto_increment,
    company_name varchar(255),
    end_date date,
    program_position varchar(255),
    start_date date,
    member_id bigint,
    primary key (career_id)
);

create table categories (
    category_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    category_name varchar(255),
    parent_category_id bigint,
    primary key (category_id)
);

create table member (
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

create table mentee (
    mentee_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    payment bit,
    member_id bigint,
    program_id bigint,
    primary key (mentee_id)
);

create table mentor (
    mentor_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    host bit,
    member_id bigint,
    program_id bigint,
    primary key (mentor_id)
);

create table mission (
    mission_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    due_date date,
    content varchar(255),
    title varchar(255),
    program_id bigint,
    member_id bigint,
    primary key (mission_id)
);

create table program (
    program_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    description varchar(255),
    due_date date,
    max_member integer,
    price integer,
    program_name varchar(255),
    program_state varchar(255),
    category_id bigint,
    primary key (program_id)
);

create table reply (
    reply_id bigint not null auto_increment,
    created_date datetime(6),
    modified_date datetime(6),
    content varchar(255),
    board_id bigint,
    member_id bigint,
    primary key (reply_id)
);

create table hibernate_sequence (
    next_val bigint
);
insert into hibernate_sequence values ( 1 );

alter table applicants
add constraint FK5p5eko6qofhy95r7xv6b5qjta
foreign key (member_id)
references member (member_id);

alter table applicants
add constraint FKnigbqp7odah386tx95h5iiwik
foreign key (program_id)
references program (program_id);

alter table board
add constraint FKhbggv8yyweag0hevkxhofdgt3
foreign key (mission_id)
references mission (mission_id);

alter table board
add constraint FKsds8ox89wwf6aihinar49rmfy
foreign key (member_id)
references member (member_id);

alter table career
add constraint FKe6mrlvn1suc8aguikew6wqj6w
foreign key (member_id)
references member (member_id);

alter table categories
add constraint FK9il7y6fehxwunjeepq0n7g5rd
foreign key (parent_category_id)
references categories (category_id);

alter table mentee
add constraint FKm0waqnmojthwy4ixuemublq2p
foreign key (member_id)
references member (member_id);

alter table mentee
add constraint FK8n0cg0nocvgrxlrx4lcr5fim6
foreign key (program_id)
references program (program_id);

alter table mentor
add constraint FKfunjevhtgc06gx9m5a6m1w17y
foreign key (member_id)
references member (member_id);

alter table mentor
add constraint FK2k3miyn9h1aoy1wygsk5qxllv
foreign key (program_id)
references program (program_id);

alter table mission
add constraint FK18qhmk5shnan47rwtgdtvbbq3
foreign key (program_id)
references program (program_id);
alter table mission
add constraint FKehv8k9k5lb69ky5n9nwic5y5c
foreign key (member_id)
references member (member_id);

alter table program
add constraint FK19wwc0i3gwwllx6lfrf1tl0mk
foreign key (category_id)
references categories (category_id);

alter table reply
add constraint FKcs9hiip0bv9xxfrgoj0lwv2dt
foreign key (board_id)
references board (board_id);

alter table reply
add constraint FKen6vrmi5oth4bg6ybfc202fmu
foreign key (member_id)
references member (member_id);

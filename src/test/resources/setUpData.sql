insert into categories(created_date, modified_date, category_name)
values (now(), now(), 'Programming');

insert into categories(created_date, modified_date, category_name)
values (now(), now(), 'Music');

insert into categories(created_date, modified_date, category_name, parent_category_id)
values (now(), now(), 'JAVA', 1);

insert into categories(created_date, modified_date, category_name, parent_category_id)
values (now(), now(), 'PYTHON', 1);

insert into categories(created_date, modified_date, category_name, parent_category_id)
values (now(), now(), 'GUITAR', 2);

insert into member (created_date, modified_date,
            email, nickname, o_auth_id, member_role,
            user_password, username, withdrawal)
values (now(), now(), 'memberA@email.com', 'memberANickname',
        'FORM', 'MENTOR',  '{bcrypt}$2a$10$PAeTU0ut57vvZsZLJG4RKeSqUUMEuSlhloXXpeRT4avZmoVHTlDcq', 'memberA', false);

insert into career (company_name, end_date, program_position, start_date, member_id)
values ('testCompany', '2022-10-10', 'Backend', '2021-1-1', 1);

insert into member (created_date, modified_date,
                    email, nickname, o_auth_id, member_role,
                    user_password, username, withdrawal)
values (now(), now(), 'memberB@email.com', 'memberBNickname',
        'FORM', 'MENTEE', '{bcrypt}$2a$10$PAeTU0ut57vvZsZLJG4RKeSqUUMEuSlhloXXpeRT4avZmoVHTlDcq', 'memberB', false);

insert into member (created_date, modified_date,
                    email, nickname, o_auth_id, member_role,
                    user_password, username, withdrawal)
values (now(), now(), 'memberC@email.com', 'memberCNickname',
        'FORM', 'MENTEE', '{bcrypt}$2a$10$PAeTU0ut57vvZsZLJG4RKeSqUUMEuSlhloXXpeRT4avZmoVHTlDcq', 'memberC', false);

insert into program(created_date, modified_date, description,
        due_date, max_member, price, program_name, program_state,
        category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram',
        'OPEN', 3);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram2',
        'OPEN', 3);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram3',
        'OPEN', 3);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram4',
        'OPEN', 3);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram5',
        'OPEN', 3);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram6',
        'OPEN', 4);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram7',
        'OPEN', 4);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram8',
        'OPEN', 4);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram9',
        'OPEN', 5);

insert into program(created_date, modified_date, description,
                    due_date, max_member, price, program_name, program_state,
                    category_id)
values (now(), now(), 'Test program description',
        '2022-12-07', 5, 10000, 'testProgram10',
        'OPEN', 5);

insert into mentor(created_date, modified_date, host, member_id,
          program_id)
values ( now(), now(), true, 1, 1);

insert into applicants(created_date, modified_date, approval,
   message, program_role, member_id, program_id)
values ( now(), now(), false, 'wanna join in', 'MENTEE', 2, 1);

insert into applicants(created_date, modified_date, approval,
                       message, program_role, member_id, program_id)
values ( now(), now(), false, 'plzzzz ', 'MENTEE', 3, 1);

insert into mission(created_date, modified_date, due_date, content, title, program_id, member_id)
values (now(), now(), '2099-12-05', 'testMission description', 'testMission', 1, 1);

insert into mission(created_date, modified_date, due_date, content, title, program_id, member_id)
values (now(), now(), '2099-12-05', 'testMission2 description', 'testMission2', 1, 1);

insert into board(created_date, modified_date, content, title, mission_id, member_id, temporal)
values (now(), now(), 'test board description', 'testBoard', 1, 1, 0);

insert into reply(created_date, modified_date, content, board_id, member_id, removal)
values (now(), now(), 'test reply', 1, 1, 0);
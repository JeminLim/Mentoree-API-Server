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
        'FORM', 'MENTOR', '1234QWer!@', 'memberA', false);

insert into member (created_date, modified_date,
                    email, nickname, o_auth_id, member_role,
                    user_password, username, withdrawal)
values (now(), now(), 'memberB@email.com', 'memberBNickname',
        'FORM', 'MENTEE', '1234QWer!@', 'memberB', false);

insert into member (created_date, modified_date,
                    email, nickname, o_auth_id, member_role,
                    user_password, username, withdrawal)
values (now(), now(), 'memberC@email.com', 'memberCNickname',
        'FORM', 'MENTEE', '1234QWer!@', 'memberC', false);

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

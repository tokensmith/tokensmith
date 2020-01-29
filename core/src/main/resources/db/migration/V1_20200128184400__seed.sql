insert into response_type values ('95bd2346-483c-40dc-991c-3d7aa86440b3', 'CODE');
insert into response_type values ('0b2af677-9712-4c71-a517-5049f6a20526', 'TOKEN');
insert into response_type values ('5b2f101f-3a3a-49e3-9ec5-c0a439b8bc98', 'ID_TOKEN');
insert into response_type values ('16a02908-0ffc-40af-96c3-341098f492a1', 'PASSWORD');

insert into scope values ('886313e1-3b8a-5372-9b90-0c9aee199e5d', 'email');
insert into scope values ('d9baba84-b7aa-5e73-8d80-67adca38bb6f', 'profile');
insert into scope values ('ab44da09-2865-594f-b367-ff3933461af1', 'openid');

insert into nonce_type
values (
    '0e550916-4424-47bf-be17-059fd76f52f2',
    'welcome'
);

insert into nonce_type
values (
    '40141610-9729-4bc6-a14b-f923821b2f0b',
    'reset_password'
);

insert into configuration
values (
    '6898348b-1423-4e00-ab8c-17b13e61af1c',
     32,
     32,
     32,
     3600,
     3600,
     3600,
     3600,
     3600,
     120,
     1209600
);
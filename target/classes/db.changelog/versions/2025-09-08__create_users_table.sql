create table users (
                       user_id UUID primary key default gen_random_uuid(),
                       login varchar(32) unique not null ,
                       password varchar(512) not null ,
                       username varchar(64) ,
                       role varchar(16) check ( role = 'USER' or role = 'ADMIN' ) default 'USER'
)
create table item (id serial primary key, description text, price numeric (8,2));
create table sale (id serial primary key, item_id int references item(id), price numeric(8,2));

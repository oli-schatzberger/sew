create table population (
    gemid integer not null,
    gemeinde varchar(50) not null,
    jahr integer not null,
    einwohner integer not null
);

alter table population add primary key (gemid, jahr);
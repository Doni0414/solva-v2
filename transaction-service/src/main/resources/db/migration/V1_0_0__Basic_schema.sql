create schema if not exists transaction;

create table transaction.t_transaction(
    id serial primary key,
    account_from bigint not null,
    account_to bigint not null,
    currency_shortname varchar not null,
    sum float8 not null check(sum >= 0),
    expense_category varchar not null,
    datetime timestamp with time zone not null,
    limit_exceeded bool default false
);

create table transaction.t_monthly_limit(
    id serial primary key,
    amount float8 not null,
    datetime timestamp with time zone not null,
    currency_shortname varchar not null,
    expense_category varchar not null
);

create table transaction.t_currency(
    id serial primary key,
    currency_shortname varchar not null,
    in_usd float8 check(in_usd > 0)
);
alter table transaction.t_monthly_limit
add column limit_sum float8 not null default 0;
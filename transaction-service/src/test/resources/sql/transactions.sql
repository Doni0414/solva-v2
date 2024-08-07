insert into transaction.t_transaction(id, account_from, account_to, currency_shortname, sum, expense_category, datetime, limit_exceeded, monthly_limit_id)
values (1, 123, 1000, 'USD', 100, 'SERVICE', '2003-04-12 04:05:06+06', false, 5),
       (2, 123, 1001, 'USD', 100, 'SERVICE', '2003-04-12 04:05:06+06', true, 5),
       (3, 123, 1002, 'USD', 100, 'PRODUCT', '2003-04-12 04:05:06+06', true, 6);
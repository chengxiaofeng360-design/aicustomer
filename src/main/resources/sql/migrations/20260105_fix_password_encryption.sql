-- 修复明文密码为 BCrypt 加密密码 (密码: 123456)
UPDATE sys_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6aBECYnZhTaXvdWq4.t.n.u.u' WHERE username IN ('admin', 'staff') AND (password = '123456' OR password IS NULL OR password NOT LIKE '$2a$%');

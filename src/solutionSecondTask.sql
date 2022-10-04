CREATE TABLE employee_chat(
  employee_id  BIGINT references employee,
  chat_id BIGINT references chat,
  PRIMARY KEY (employee_id, chat_id)
);
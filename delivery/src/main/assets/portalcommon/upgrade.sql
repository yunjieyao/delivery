delete from express_info;

--创建数据库
create table if not exists express_info
(
    expressage_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    expressage_lead_num varchar,
    expressage_delivery_time varchar,
    expressage_delivery_site varchar,
    expressage_release_time varchar,
    expressage_desc varchar,
    expressage_lead_reward varchar,
    expressage_lead_type varchar,
    expressage_delivery_user_name varchar,
    expressage_delivery_phone_number varchar,
    expressage_lead_remark varchar,
    expressage_release_status varchar,
    expressage_receive_status varchar,
    expressage_type varchar
);







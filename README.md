# yygh
## 在线预约挂号平台
#### 项目文件介绍
1. resources文件夹：存放项目sql语句、工具类等资源
2. yygh_admin文件夹：存放项目后台管理系统的前端页面，基于vue-admin-template开发
3. yygh_site文件夹：存放预约挂号系统前端页面，基于nuxt.js开发
4. yygh_parent文件夹：存放项目后端代码
#### 项目功能总结（后台管理系统）
1、医院设置管理
- 医院设置列表、添加、锁定、删除
- 医院列表、详情、排班、下线

2、数据管理
- 数据字典树形显示、导入、导出

3、用户管理
- 用户列表、查看、锁定
- 认证用户审批

4、订单管理
- 订单列表、详情

#### 项目功能总结（前台管理系统）
1、首页数据显示
- 医院列表

2、医院详情显示
- 医院科室显示

3、用户登录功能
- 手机号登录（短信验证码发送）
- 微信扫描登录

4、用户实名认证

5、就诊人管理
- 列表、添加、详情、删除

6、预约挂号功能
- 排班和挂号详情信息
- 确认挂号信息
- 生成预约挂号订单
- 挂号订单支付（微信）
- 取消预约订单

7、就医提醒功能

#### 项目技术点总结（后端技术）
1、SpringBoot

2、SpringCloud
（1）Nacos注册中心
（2）Feign
（3）GateWay

3、Redis
（1）使用Redis作为缓存
（2）验证码有效时间、支付二维码有效时间

4、MongoDB
（1）使用MongoDB存储 医院相关数据

5、EasyExcel
（1）操作excel表格，进行读和写操作

6、MyBatisPlus

7、RabbitMQ
（1）订单相关操作，发送mq消息

8、Docker
（1）下载镜像 docker pull 
（2）创建容器 docker run

9、阿里云OSS

10、阿里云短信服务

11、微信登录/支付

12、定时任务

#### 项目技术点总结（前端技术）
1、vue
（1）指令

2、Element-ui

3、nuxt

4、npm



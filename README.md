vtbtn-server
=====================

为**所有**按钮项目设计的服务器，提供如下功能:

- 按钮数据
- 统计数据

## 运行

### 从 DockerHub
- 拉取 Docker 镜像
```shell script
docker pull imkiva/vtbtn-server
```

- 创建数据目录
```shell script
mkdir /path/to/data/dir
```

- 启动容器
```shell script
docker run -d -p 8080:8080 \
  --env VTBTN_SERVER_ROOT_NAME=<超级用户 ID> \
  --env VTBTN_SERVER_ROOT_PASSWORD=<超级用户密码> \
  --volume <数据目录>:/data/db \
  imkiva/vtbtn-server
```

**为了您和用户的身心健康，请勿直接传输明文密码，推荐使用非对称加密算法保证用户的隐私**

服务器将被启动在 `localhost:8080`

### 从源码
- 编译项目并打包
```shell script
./gradlew shadowJar
```

- 编译 Docker 镜像
```shell script
docker build -t imkiva/vtbtn-server:latest .
```

- 启动容器
```shell script
docker run -d -p 8080:8080 \
  --env VTBTN_SERVER_ROOT_NAME=<超级用户 ID> \
  --env VTBTN_SERVER_ROOT_PASSWORD=<超级用户密码> \
  --volume <数据目录>:/data/db \
  imkiva/vtbtn-server
```

**为了您和用户的身心健康，请勿直接传输明文密码，推荐使用非对称加密算法保证用户的隐私**

## 开发

我们使用 [JetBrains](https://www.jetbrains.com/?from=KiVM) 提供的 [Intellij IDEA](https://www.jetbrains.com/idea/?from=KiVM).
进行开发。我们相信，这是世界上最棒的 IDE.

[<img src="logo/jetbrains.png" width="200"/>](https://www.jetbrains.com/?from=KiVM)

## API 文档

API 采用 RESTful 风格设计，请求和响应的数据格式一律为 `application/json`

### 按钮数据相关 API

#### 获取所有 Vtuber 的列表
```http request
GET /vtubers
```

##### 响应
```json
{
  "<Vtuber 的名字>": "<该 Vtuber 的资源路径>",
   ...
}
```

例如：
```json
{
  "fubuki": "/vtubers/fubuki"
}
```

说明当前服务器上共存储了一位 Vtuber 的按钮信息（fubuki），获取该 Vtuber 的所有按钮数据
（语音/分组）都应该请求 `/vtuber/fubuki` 路径下的资源

#### 获取某个 Vtuber 的所有语音
```http request
GET /vtubers/:name
```
|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|

##### 响应
```json
{
  "name": "<Vtuber 的名字>",
  "groups": [ group, group, ... ]
}
```

每一个 `group` 都具有如下的结构
```json
{
  "name": "<组的名字>",
  "desc": {
    "zh": "<中文翻译>",
    "en": "<英文翻译>",
    "ja": "<日文翻译>"
  },
  "voices": [ voice, voice, ... ]
}
```

每一个 `voice` 都具有如下的结构
```json
{
  "name": "<音频名字>",
  "url": "<音频路径>",
  "group": "<音频所属组>",
  "desc": {
    "zh": "<中文翻译>",
    "en": "<英文翻译>",
    "ja": "<日文翻译>"
  }
}
```

#### 获取某个 Vtuber 的某一分组下的所有语音
```http request
GET /vtubers/:name/:group
```

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|
|group|组名|

##### 响应
返回的数据为一个 `group` （关于 `group` 的结构请查看上文）

#### 新增一个组 (Group)
```http request
POST /vtubers/:name/add-group
```

该操作会在 Vtuber 名为 `:name` 的语音数据库中新增一个组。

注意：该操作需要对应 Vtuber 的**管理员**权限

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|

##### 请求体
```json
{
  "name": "<组的名字>",
  "desc": {
    "zh": "<中文翻译>",
    "en": "<英文翻译>",
    "ja": "<日文翻译>"
  }
}
```

##### 响应
如果操作成功，服务器返回 `200 OK`

如果操作失败，服务器可能返回以下任一错误码:
- 403: 权限不足
- 500: 服务器内部错误 ~~(可以提 issue 了)~~

无论是哪种错误，响应体中均会包含如下格式的信息
```json
{
  "msg": "<操作失败的原因>"
}
```

#### 新增一条语音 (Voice)
```http request
POST /vtubers/:name/:group/add-voice
```

该操作会在 Vtuber 名为 `:name` 的语音数据库中新增一条语音，并且该语音的组被设置为 `:group`。

注意：该操作需要对应 Vtuber 的**管理员**权限

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|
|group|组名|

##### 请求体
```json
{
  "name": "<音频名字>",
  "url": "<音频路径>",
  "desc": {
    "zh": "<中文翻译>",
    "en": "<英文翻译>",
    "ja": "<日文翻译>"
  }
}
```

##### 响应
如果操作成功，服务器返回 `200 OK`

如果操作失败，服务器可能返回以下任一错误码:
- 403: 权限不足
- 500: 服务器内部错误 ~~(可以提 issue 了)~~

无论是哪种错误，响应体中均会包含如下格式的信息
```json
{
  "msg": "<操作失败的原因>"
}
```

### 统计数据相关 API

所有统计数据 API 均支持以下 Query Parameters

|参数|说明|
|:---:|:----:|
|from|起始时间|
|to|结束时间|

#### 总体统计数据
```http request
GET /statistics/:name
```
|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|

响应
```json
{
    "vtuber": "<Vtuber的名字>",
    "from": "<开始时间>",
    "to": "<当前时间>",
    "click": "<点击次数>"
}
```
例如
```json
{
    "vtuber": "fubuki",
    "from": "1970-01-01",
    "to": "2020-06-15",
    "click": 2
}
```
说明名叫`fubuki`的 Vtuber 对应的所有按钮

从`1970-01-01`到`2020-06-15`一共被点击了`2`次

#### 分组语音统计数据
```http request
GET /statistics/:name/:group
```

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|
|group|组名|

响应
```json
{
    "vtuber": "<Vtuber的名字>",
    "group": "<分组名称>",
    "from": "<开始时间>",
    "to": "<当前时间>",
    "click": "<点击次数>"
}
```
例如
```json
{
    "vtuber": "fubuki",
    "group": "actmoe",
    "from": "1970-01-01",
    "to": "2020-06-15",
    "click": 2
}
```
说明名叫`fubuki`的 Vtuber 对应的`actmoe`按钮

从`1970-01-01`到`2020-06-15`一共被点击了`2`次

#### 单个语音点击数据
```http request
GET /statistics/:name/:group/:voiceName
```

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|
|group|组名|
|voiceName|音频文件名|

响应
```json
{
    "vtuber": "<Vtuber的名字>",
    "name": "<音频文件名>",
    "group": "<分组名称>",
    "from": "<开始时间>",
    "to": "<当前时间>",
    "click": "<点击次数>"
}
```

例如
```json
{
    "vtuber": "fubuki",
    "name": "f-006",
    "group": "actmoe",
    "from": "1970-01-01",
    "to": "2020-06-15",
    "click": 2
}
```
说明名叫`fubuki`的 Vtuber 对应的`actmoe`中的`f-006`按钮

从`1970-01-01`到`2020-06-15`一共被点击了`2`次

#### 为某个按钮的点击次数+1
```http request
POST /statistics/:name/click
```

|参数|说明|
|:---:|:----:|
|name|Vtuber 的名字|

请求体
```json
{
  "group": "<分组名称>",
  "name": "<音频文件名>"
}
```
- `group`: 按钮所属分组
- `name`: 次数要+1的按钮

### 管理权限相关 API

#### 你好
```http request
GET /users/hi
```

获取用户信息

##### 响应
如果操作成功，服务器返回 `200 OK`，响应体中包含如下内容
```json
{
    "msg": "欢迎消息",
    "uid": "用户 ID",
    "root": 是否为超级管理员(true|false),
    "verified": 用户是否已验证(true|false),
    "admin": ["管理的 Vtuber 1", "管理的 Vtuber 2", ...],
    "profile": {
        "name": "昵称",
        "email": "邮箱"
    }
}
```

如果操作失败，服务器可能返回以下任一错误码:
- 403: 权限不足
- 500: 服务器内部错误 ~~(可以提 issue 了)~~

无论是哪种错误，响应体中均会包含如下格式的信息
```json
{
  "msg": "<操作失败的原因>"
}
```

#### 登录
```http request
POST /users/login
```

##### 请求体
```json
{
  "uid": "<用户 ID>",
  "password": "<用户密码>"
}
```

**为了您和用户的身心健康，请勿直接传输明文密码，推荐使用非对称加密算法保证用户的隐私**

##### 响应
若登录成功，服务器返回 `200 OK`，并会自动设置 `Set-Cookie` 响应头

若失败，服务器返回 `403 Forbidden` 并且响应体包含如下格式的信息
```json
{
  "msg": "<登陆失败的原因>"
}
```

#### 注册
```http request
POST /users/register
```

##### 请求体
```json
{
  "uid": "<用户 ID>",
  "password": "<用户密码>",
  "name": "<用户昵称>",
  "email": "<用户邮箱>"
}
```

**为了您和用户的身心健康，请勿直接传输明文密码，推荐使用非对称加密算法保证用户的隐私**

##### 响应
若注册成功，服务器返回 `200 OK`

若失败，服务器返回 `403 Forbidden` 并且响应体包含如下格式的信息
```json
{
  "msg": "<注册失败的原因>"
}
```

#### 修改可管理的 Vtuber 列表
```http request
POST /users/change-admin-vtuber
```

注意：该操作需要**超级管理员**用户

##### 请求体
```json
{
  "uid": "<被修改的用户 ID>",
  "add": [],
  "remove": []
}
```

参数说明:
- `add`: 将要添加到可管理列表中的 Vtuber 的名字
- `remove`: 将要从可管理列表中移除的 Vtuber 的名字

如果 `remove` 中包含 `add` 中的元素，则该元素并不会添加到可管理列表中

##### 响应
如果操作成功，服务器返回 `200 OK`

如果操作失败，服务器可能返回以下任一错误码:
- 403: 权限不足
- 500: 服务器内部错误 ~~(可以提 issue 了)~~

无论是哪种错误，响应体中均会包含如下格式的信息
```json
{
  "msg": "<操作失败的原因>"
}
```



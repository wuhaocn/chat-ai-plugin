# 消息路由系统 API 文档

## 1. 消息发送接口

### 1.1 发送消息
- **接口**: `/api/messages/send`
- **方法**: POST
- **Content-Type**: application/x-www-form-urlencoded

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| channelType | string | 是 | 频道类型，固定值：PERSON |
| fromUserId | string | 是 | 发送者用户ID |
| toUserId | string | 是 | 接收者用户ID |
| content | string | 是 | 消息内容，JSON格式：`{"content": "消息内容"}` |
| msgTimestamp | string | 是 | 消息时间戳 |
| timestamp | string | 是 | 时间戳 |
| objectName | string | 是 | 消息类型，固定值：RC:TxtMsg |
| sensitiveType | string | 是 | 敏感类型，固定值：0 |
| source | string | 是 | 消息来源，固定值：Android |
| msgUID | string | 是 | 消息唯一ID，格式：CLU2-XXXX-XXXX-XXXX |
| clientIp | string | 是 | 客户端IP地址 |

#### 请求示例
```http
POST /api/messages/send
Content-Type: application/x-www-form-urlencoded

channelType=PERSON&fromUserId=test&toUserId=222&msgTimestamp=1743499753581&timestamp=1743499753581&objectName=RC%3ATxtMsg&content=%7B%22content%22%3A%22hello%22%7D&sensitiveType=0&source=Android&msgUID=CLU2-NFGR-CQ82-O4IS&clientIp=10.41.20.57%3A22263
```

#### 响应示例
```json
{
    "status": "success",
    "message": "消息发送成功"
}
```

### 1.2 获取会话历史
- **接口**: `/api/messages/history`
- **方法**: GET

#### 响应示例
```json
[
    {
        "fromUserId": "test",
        "toUserId": "222",
        "content": {
            "content": "消息内容"
        },
        "msgTimestamp": "1743499753581",
        "objectName": "RC:TxtMsg"
    }
]
```

## 2. 错误处理

### 2.1 错误响应格式
```json
{
    "status": "error",
    "errorMessage": "错误描述信息"
}
```

### 2.2 常见错误码
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

## 3. 注意事项

1. 所有时间戳使用毫秒级时间戳
2. 消息内容必须使用JSON格式包装
3. 消息ID格式为：CLU2-XXXX-XXXX-XXXX
4. 所有请求必须包含必要的请求头
5. 建议在发送消息前进行参数验证 
# **提交规范**

## **例子**

```
feat(miniprogram):新增自开奖页面

新增活动页面，但是产品还有需求遗漏，待完善
- 新增内容1
- 新增内容2
```

```
fix(miniprogram):活动页面设置渠道后,自动添加渠道文字类型判断错误
```



## **Commit message 格式**

为了方便使用，我们避免了过于复杂的规定，格式较为简单且不限制中英文：



```
<type>(<scope>): <subject>
// 空一行
<body>
// 空一行

// 注意冒号 : 后有空格
// 如 feat(miniprogram): 增加了小程序模板消息相关功能
```

大致分为两个个部分:

1. 标题行: 包括` <type>(<scope>): <subject>` 
2. 主题内容: 描述了为什么修改,做了什么修改,以及开发思路等

|         | 说明                   | 必填   |
| ------- | ---------------------- | ------ |
| type    | 修改类型               | 必填   |
| scope   | 作用范围               | 非必填 |
| subject | 对commit的简单描述     | 必填   |
| body    | 本次 commit 的详细描述 | 非必填 |

### **1.type**

```
feat：新功能（feature）
fix：修补bug
docs：文档（documentation）
style： 格式（不影响代码运行的变动）
refactor：重构（即不是新增功能，也不是修改bug的代码变动）
test：增加测试
chore：构建过程或辅助工具的变动
```

如果type为feat和fix，则该 commit 将肯定出现在 Change log 之中。其他情况（docs、chore、style、refactor、test）由你决定，要不要放入 Change log，建议是不要。

### **2.scope**

scope用于说明 commit 影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。

### **3. subject**

subject是 commit 目的的简短描述，不超过50个字符。

```
以动词开头，使用第一人称现在时，比如change，而不是changed或changes
第一个字母小写
结尾不加句号（.）
```

### **4. body** 

Body 部分是对本次 commit 的详细描述，可以分成多行。描述为什么修改, 做了什么样的修改, 以及开发的思路等等
package com.petshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetManagementSystemApplication.class, args);
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffff我**100% 用最简单、最实用、不啰嗦**的方式告诉你！\r\n" + //
                        "\r\n" + //
                        "# 一句话结论\r\n" + //
                        "**你现在根本不用管什么 mysqldump！\r\n" + //
                        "和你项目能不能登录、能不能跑 一毛钱关系都没有！**\r\n" + //
                        "\r\n" + //
                        "---\r\n" + //
                        "\r\n" + //
                        "# 你现在只需要搞懂 2 件事\r\n" + //
                        "## 1. **mysqldump 是什么？**\r\n" + //
                        "就是 **MySQL 数据库备份命令**\r\n" + //
                        "用来导出整个数据库成 `.sql` 文件\r\n" + //
                        "**和你写代码、写登录、跑项目完全无关！**\r\n" + //
                        "\r\n" + //
                        "## 2. **你现在最致命的问题只有一个：**\r\n" + //
                        "## **没有 LoginController，表单提交 /login 404 报错！**\r\n" + //
                        "不是数据库问题！\r\n" + //
                        "不是导出问题！\r\n" + //
                        "不是样式问题！\r\n" + //
                        "\r\n" + //
                        "---\r\n" + //
                        "\r\n" + //
                        "# 你现在的项目状态（超级直白）\r\n" + //
                        "- 前端表单提交到 `/login` ✅\r\n" + //
                        "- **后端没有任何接口接收 /login** ❌\r\n" + //
                        "- 所以 **点登录 = 404 / 无法登录**\r\n" + //
                        "\r\n" + //
                        "---\r\n" + //
                        "\r\n" + //
                        "# 你现在只需要做一件事：\r\n" + //
                        "# **写一个 LoginController.java**\r\n" + //
                        "**写完就能登录！**\r\n" + //
                        "**其他都是浪费时间！**\r\n" + //
                        "\r\n" + //
                        "---\r\n" + //
                        "\r\n" + //
                        "# 我现在直接给你**完整可运行代码**\r\n" + //
                        "**你直接复制就能用！**\r\n" + //
                        "\r\n" + //
                        "## 文件名：`LoginController.java`\r\n" + //
                        "```java\r\n" + //
                        "package com.petshop.controller;\r\n" + //
                        "\r\n" + //
                        "import com.petshop.entity.User;\r\n" + //
                        "import com.petshop.entity.Merchant;\r\n" + //
                        "import com.petshop.service.UserService;\r\n" + //
                        "import com.petshop.service.MerchantService;\r\n" + //
                        "import jakarta.servlet.http.HttpSession;\r\n" + //
                        "import org.springframework.beans.factory.annotation.Autowired;\r\n" + //
                        "import org.springframework.stereotype.Controller;\r\n" + //
                        "import org.springframework.web.bind.annotation.PostMapping;\r\n" + //
                        "import org.springframework.web.bind.annotation.RequestParam;\r\n" + //
                        "\r\n" + //
                        "@Controller\r\n" + //
                        "public class LoginController {\r\n" + //
                        "\r\n" + //
                        "    @Autowired\r\n" + //
                        "    private UserService userService;\r\n" + //
                        "\r\n" + //
                        "    @Autowired\r\n" + //
                        "    private MerchantService merchantService;\r\n" + //
                        "\r\n" + //
                        "    // 这就是你必须要的 /login 接口\r\n" + //
                        "    @PostMapping(\"/login\")\r\n" + //
                        "    public String login(\r\n" + //
                        "            @RequestParam String email,\r\n" + //
                        "            @RequestParam String password,\r\n" + //
                        "            @RequestParam String type,\r\n" + //
                        "            HttpSession session) {\r\n" + //
                        "\r\n" + //
                        "        if (\"user\".equals(type)) {\r\n" + //
                        "            User user = userService.login(email, password);\r\n" + //
                        "            if (user != null) {\r\n" + //
                        "                session.setAttribute(\"user\", user);\r\n" + //
                        "                return \"redirect:/user/dashboard\";\r\n" + //
                        "            }\r\n" + //
                        "        } else if (\"admin\".equals(type)) {\r\n" + //
                        "            Merchant merchant = merchantService.login(email, password);\r\n" + //
                        "            if (merchant != null && \"approved\".equals(merchant.getStatus())) {\r\n" + //
                        "                session.setAttribute(\"merchant\", merchant);\r\n" + //
                        "                return \"redirect:/merchant/dashboard\";\r\n" + //
                        "            }\r\n" + //
                        "        }\r\n" + //
                        "\r\n" + //
                        "        return \"redirect:/login.html?error\";\r\n" + //
                        "    }\r\n" + //
                        "}\r\n" + //
                        "```\r\n" + //
                        "\r\n" + //
                        "---\r\n" + //
                        "\r\n" + //
                        "# 最后超级真话\r\n" + //
                        "## **你现在别管 mysqldump、别管前端、别管样式！\r\n" + //
                        "## 就把上面这段代码复制进去，项目立刻能登录！**\r\n" + //
                        "\r\n" + //
                        "你现在**复制进去就能跑**，不懂我再教你！ffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
        System.out.println("Pefffffffffffffffffffffffffff!");
    }
}
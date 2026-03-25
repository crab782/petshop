<template>
  <div class="admin-login">
    <div class="header">
      <h1>宠物管理系统 - 管理员登录</h1>
    </div>
    
    <div class="login-container">
      <h2>管理员登录</h2>
      <form @submit.prevent="handleLogin">
        <div v-if="error" class="error-message">
          <p>{{ error }}</p>
        </div>
        <div v-if="success" class="success-message">
          <p>{{ success }}</p>
        </div>
        <div class="form-group">
          <label for="email">邮箱</label>
          <input type="email" id="email" v-model="form.email" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="form.password" required>
        </div>
        <input type="hidden" name="type" value="admin">
        <button type="submit" class="btn">登录</button>
      </form>
      <div class="back-link">
        <p><router-link to="/">返回首页</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminLogin',
  data() {
    return {
      form: {
        email: '',
        password: ''
      },
      error: '',
      success: ''
    }
  },
  methods: {
    handleLogin() {
      // 这里可以添加登录逻辑，调用API进行登录
      console.log('Admin login data:', this.form);
      // 登录成功后跳转到管理员 dashboard
      this.$router.push('/admin/dashboard');
    }
  }
}
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background-color: #f5f5f5;
}
.header {
  background-color: #4CAF50;
  color: white;
  padding: 20px;
  text-align: center;
}
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}
.login-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #666;
}
.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}
.btn {
  width: 100%;
  background-color: #4CAF50;
  color: white;
  padding: 15px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.btn:hover {
  background-color: #45a049;
}
.error-message {
  background-color: #f44336;
  color: white;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 20px;
}
.success-message {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 20px;
}
.back-link {
  text-align: center;
  margin-top: 20px;
}
.back-link a {
  color: #4CAF50;
  text-decoration: none;
}
.back-link a:hover {
  text-decoration: underline;
}
</style>
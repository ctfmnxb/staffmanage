<template>
  <div class="login-container" :style="{ backgroundImage: `url(${require('@/assets/login.png')})` }">
    <el-popover
      ref="clickPopover"
      placement="top"
      width="120"
      trigger="manual"
    >
      点击成功
    </el-popover>
    <h1 class="system-title animated-title">企业员工管理系统</h1>
    <div class="login-box-wrapper">
      <div class="login-box animated-login-box">
        <h2>用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-item">
            <label for="username">用户名</label>
            <input type="text" id="username" v-model="username" required />
          </div>
          <div class="form-item">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="password" required />
          </div>
          <div class="button-group">
            <button type="submit" @click="(e) => showClickFeedback(e.target)">登录</button>
            <button type="button" @click="(e) => { goToRegister(); showClickFeedback(e.target); }">前往注册</button>
          </div>
          <button type="button" @click="(e) => { goToAdminLogin(); showClickFeedback(e.target); }" class="admin-login-btn">前往管理员登录</button>
          <div class="third-party-login">
            <p>第三方登录</p>
            <div class="third-party-buttons">
              <button @click="(e) => { showUnimplemented(); showClickFeedback(e.target); }">
                <img src="@/assets/logo/qq.png" alt="QQ登录" />
              </button>
              <button @click="showUnimplemented">
                <img src="@/assets/logo/github.png" alt="GitHub登录" />
              </button>
              <button @click="showUnimplemented">
                <img src="@/assets/logo/wechat.png" alt="微信登录" />
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { Message, Alert } from 'element-ui'

export default {
  methods: {
    showClickFeedback(el) {
      if (this.$refs.clickPopover) {
        this.$refs.clickPopover.doShow();
        this.$refs.clickPopover.$refs.popper.style.left = `${el.getBoundingClientRect().left + el.offsetWidth / 2}px`;
        this.$refs.clickPopover.$refs.popper.style.top = `${el.getBoundingClientRect().top - 30}px`;
        setTimeout(() => {
          if (this.$refs.clickPopover) {
            this.$refs.clickPopover.doClose();
          }
        }, 1000);
      }
    },
    showUnimplemented() {
      Message.warning('功能尚未开发');
    },
    async handleLogin() {
      const router = this.$router
      try {
        const response = await this.$axios.post('/api/staff/login', {
          username: this.username,
          password: this.password
        });
        Message.success('登录成功');
        localStorage.setItem('username', response.data.username);
        localStorage.setItem('staffId', response.data.id);
        this.$router.push('/staff');
      } catch (error) {
        let errorMessage = '登录出错，请稍后重试';
        if (error.response) {
          if (error.response.status === 401) {
            errorMessage = '用户名或密码错误';
          } else {
            errorMessage = error.response.data.message || errorMessage;
          }
        }
        this.$alert(errorMessage, '登录错误', {
          confirmButtonText: '确定',
          type: 'error'
        });
      }
    },
    goToRegister() {
      this.$router.push('/user-register');
    },
    goToAdminLogin() {
      this.$router.push('/admin-login');
    }
  },
  name: 'UserLogin',
  data() {
    return {
      username: '',
      password: '',
      // 确保组件初始化时 clickPopover 引用已定义
      clickPopover: null
    };
  },
  components: {
    [Alert.name]: Alert
  }
};
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

button:active {
  transform: scale(0.95);
  transition: transform 0.1s ease;
}

@keyframes textAnimation {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 0.5;
  }
}

.animated-login-box {
  animation: fadeInUp 0.8s ease-out;
}

.animated-title {
  animation: textAnimation 3s infinite;
}

.login-container {
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  position: relative;
}

.system-title {
  width: 100%;
  text-align: center;
  color: white;
  font-size: 2.5rem;
  margin-top: 2rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.login-box-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 2rem;
  border-radius: 8px;
  width: 350px;
  overflow: hidden;
}

.login-box h2 {
  background-color: #42b983;
  color: white;
  padding: 1rem;
  margin: -2rem -2rem 1.5rem -2rem;
  position: relative;
}

.login-box h2::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: 0;
  width: 100%;
  height: 20px;
  background-color: #42b983;
  border-radius: 0 0 50% 50%;
}

.button-group {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.button-group button {
  flex: 1;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-item {
  margin-bottom: 1rem;
}

.form-item label {
  display: block;
  margin-bottom: 0.5rem;
}

.form-item input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 0.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  margin-top: 0.5rem;
}

.admin-login-btn {
  background-color: #00008B;
  color: white;
  transition: background-color 0.3s ease;
}

.admin-login-btn:hover {
  background-color: #000058;
}

.admin-login-btn:active {
  background-color: #000066;
  transform: scale(0.98);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.third-party-login {
  margin-top: 1rem;
  text-align: center;
}

.third-party-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 0.5rem;
}

.third-party-buttons button {
  width: auto;
  background: none;
  padding: 0;
}

.third-party-buttons button img {
  width: 30px;
  height: 30px;
  transition: transform 0.3s ease;
}

.third-party-buttons button img:hover {
  transform: scale(1.2);
}

button:hover {
  background-color: #33a06f;
}

.admin-btn {
  background-color: #ff6b6b;
  border-radius: 20px;
}

.admin-btn:hover {
  background-color: #ff5252;
}

button:active {
  transform: scale(0.98);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
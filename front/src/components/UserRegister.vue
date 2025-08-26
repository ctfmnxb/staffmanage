<template>
  <div class="register-container" :style="{ backgroundImage: `url(${require('@/assets/register.jpg')})` }">
    <el-popover
      ref="clickPopover"
      placement="top"
      width="120"
      trigger="manual"
    >
      点击成功
    </el-popover>
    <h1 class="system-title animated-title">企业员工管理系统</h1>
    <div class="register-box-wrapper">
      <div class="register-box animated-register-box">
        <h2>用户注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-item">
            <label for="username">用户名</label>
            <input type="text" id="username" v-model="username" required />
          </div>
          <div class="form-item">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="password" required />
          </div>
          <div class="button-group">
            <button type="submit" @click="(e) => showClickFeedback(e.target)">注册</button>
            <button type="button" @click="(e) => { goToLogin(); showClickFeedback(e.target); }">返回登录</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>


export default {
  name: 'UserRegister',
  data() {
    return {
      username: '',
      password: ''
    };
  },
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
    async handleRegister() {
      try {
        const response = await this.$axios.post('/api/staff/register', {
              username: this.username,
              password: this.password
            });
        if (response.data.success) {
          this.$message.success('注册成功');
          this.$router.push('/user-login');
        } else {
          this.$message.error(response.data.message || '注册失败，请重试');
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '注册出错，请稍后重试');
      }
    },
    goToLogin() {
      this.$router.push('/user-login');
    }
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

.animated-register-box {
  animation: fadeInUp 0.8s ease-out;
}

.animated-title {
  animation: textAnimation 3s infinite;
}

button {
  transition: background-color 0.3s ease;
}

button:hover {
  transform: scale(1.05);
}

.register-container {
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

.register-box-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 2rem;
  border-radius: 8px;
  width: 350px;
  overflow: hidden;
}

.register-box h2 {
  background-color: #42b983;
  color: white;
  padding: 1rem;
  margin: -2rem -2rem 1.5rem -2rem;
  position: relative;
}

.register-box h2::after {
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

.register-box h2 {
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


</style>
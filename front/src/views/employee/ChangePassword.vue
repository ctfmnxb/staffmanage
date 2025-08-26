<template>
  <div class="change-password-container">
    <el-card class="box-card">
      <h2>修改个人密码</h2>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('passwordForm')">提交</el-button>
          <el-button @click="resetForm('passwordForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'ChangePassword',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'));
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'));
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      username: '', // 添加 username 属性
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitForm(formName) {

      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 使用组件中的 username 属性，而不是每次都从 localStorage 中获取
          console.log('从 this.username 获取到的用户名:', this.username);
          if (!this.username) {
            Message.error('未找到用户信息，请重新登录');
            this.$router.push('/login');
            return;
          }
          axios.put('/api/staff/change-password', {
            username: this.username,
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then(response => {
            if (response.data.success) {
              Message.success('密码修改成功');
              this.resetForm(formName);
            } else {
              Message.error(response.data.message || '密码修改失败');
            }
          }).catch(error => {
            console.error('密码修改请求失败:', error);
            Message.error('密码修改请求失败，请稍后再试');
          });
        } else {
          console.log('表单验证失败!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  },
  created() {
    const username = localStorage.getItem('username');
    if (username) {
      this.username = username;
    } else {
      this.$message.error('未找到用户信息，请重新登录');
      // 可以选择跳转到登录页面或执行其他操作
    }
  }
};
</script>

<style scoped>
.change-password-container {
  /* display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px); */
  /* background-color: #f0f2f5; */
  padding: 20px; /* 添加一些内边距，避免内容紧贴边缘 */
}

.box-card {
  width: 100%; /* 让卡片宽度自适应父容器 */
  max-width: 480px; /* 保持最大宽度，避免过宽 */
  margin: 0 auto; /* 水平居中 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-button {
  width: 100px;
}
</style>
<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #545c64;">
      <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        router
      >
        <div style="display: flex; align-items: center; justify-content: center; padding: 20px 0;">
          <img src="../assets/title.png" alt="Logo" style="width: 30px; height: 30px; margin-right: 10px;">
          <h3 style="color: #fff;">管理员系统</h3>
        </div>
        <el-menu-item index="/admin/employee">
          <i class="el-icon-user"></i>
          <span slot="title">员工信息管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/department">
          <i class="el-icon-office-building"></i>
          <span slot="title">部门管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/attendance">
          <i class="el-icon-date"></i>
          <span slot="title">考勤管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/salary">
          <i class="el-icon-money"></i>
          <span slot="title">工资管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/leave-review">
          <i class="el-icon-s-check"></i>
          <span slot="title">请假审核</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="text-align: right; font-size: 12px; background-color: #B3C0D1;">
        <el-dropdown>
          <i class="el-icon-setting" style="margin-right: 15px"></i>
          <el-dropdown-menu slot="dropdown">

            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <span>Administer</span>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { Message } from 'element-ui';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      adminUsername: '管理员'
    };
  },
  mounted() {
    // const username = localStorage.getItem('username');
    // if (username) {
    //   this.adminUsername = username;
    //   Message.success(`欢迎 ${username} 登录`);
    //   // localStorage.removeItem('username'); // 移除，避免重复显示，如果需要保持登录状态则不移除
    // }
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('token');
      localStorage.removeItem('staffId');
      localStorage.removeItem('username');
      // 跳转到登录页面
      this.$router.push('/admin-login');
      this.$message.success('已成功退出登录！');
    }
  }
};
</script>

<style scoped>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>
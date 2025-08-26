<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #545c64;">
      <div class="aside-header">
        <img src="@/assets/title.png" alt="系统Logo" class="system-logo">
        <span class="system-title">员工管理系统</span>
      </div>
      <el-menu
        default-active="personal-attendance"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        :router="false"
        @select="selectComponent"
      >
        <el-menu-item index="personal-attendance">
          <i class="el-icon-date"></i>
          <span slot="title">个人考勤记录</span>
        </el-menu-item>
        <el-menu-item index="personal-salary">
          <i class="el-icon-money"></i>
          <span slot="title">个人工资记录</span>
        </el-menu-item>
        <el-menu-item index="submit-leave">
          <i class="el-icon-s-promotion"></i>
          <span slot="title">提交请假申请</span>
        </el-menu-item>
        <el-menu-item index="change-password">
          <i class="el-icon-lock"></i>
          <span slot="title">修改个人密码</span>
        </el-menu-item>
        <el-menu-item index="personal-leave-review">
          <i class="el-icon-document"></i>
          <span slot="title">个人请假审核</span>
        </el-menu-item>
        <el-menu-item index="department-info">
          <i class="el-icon-office-building"></i>
          <span slot="title">查看部门信息</span>
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
        <span>{{ username }}</span>
      </el-header>
        <el-main>
          <!-- 主要内容区域 -->
          <component :is="selectedComponent"></component>
        </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { Message } from 'element-ui';
import PersonalAttendance from '@/views/employee/PersonalAttendance.vue';
import PersonalSalary from '@/views/employee/PersonalSalary.vue';
import SubmitLeave from '@/views/employee/SubmitLeave.vue';
import ChangePassword from '@/views/employee/ChangePassword.vue';
import PersonalLeaveReview from '@/views/employee/PersonalLeaveReview.vue';
import DepartmentInfo from '@/views/employee/DepartmentInfo.vue';

export default {
  name: 'EmployeeDashboard',
  data() {
    return {
      username: '',
      selectedComponent: 'personal-attendance' // 默认显示个人考勤记录
    };
  },
  components: {
    PersonalAttendance,
    PersonalSalary,
    SubmitLeave,
    ChangePassword,
    PersonalLeaveReview,
    DepartmentInfo
  },
  mounted() {
    const username = localStorage.getItem('username');
    const loggedIn = localStorage.getItem('loggedIn');
    if (username) {
      this.username = username;
      // 根据当前路由路径设置默认选中的组件
      const currentPath = this.$route.path;
      if (currentPath === '/change-password') {
        this.selectedComponent = 'change-password';
      } else if (currentPath === '/employee/personal-salary') {
        this.selectedComponent = 'personal-salary';
      } else if (currentPath === '/employee/submit-leave') {
        this.selectedComponent = 'submit-leave';
      } else if (currentPath === '/employee/personal-leave-review') {
        this.selectedComponent = 'personal-leave-review';
      } else if (currentPath === '/employee/department-info') {
        this.selectedComponent = 'department-info';
      } else {
        this.selectedComponent = 'personal-attendance';
      }
    }
    if (username && loggedIn === 'true') {
      Message.success(`欢迎 ${username} 登录`);
      localStorage.setItem('loggedIn', 'false'); // 移除，避免重复显示
    }
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    selectComponent(index) {
      this.selectedComponent = index;
    },
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('token');
      localStorage.removeItem('staffId');
      localStorage.removeItem('username');
      // 跳转到登录页面
      this.$router.push('/user-login');
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

.aside-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  border-bottom: 1px solid #4b545c;
}

.system-logo {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.system-title {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
</style>
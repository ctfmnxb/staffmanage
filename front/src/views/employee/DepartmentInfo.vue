<template>
  <div class="department-info-container">
    <h2 style="text-align: center; margin-bottom: 20px;">部门信息</h2>

    <!-- 根据员工是否加入部门显示不同内容 -->

    <div v-if="currentStaff && currentStaff.departmentId">
      <!-- 已加入部门的员工显示部门内部信息 -->
      <el-card class="box-card department-detail-card">
        <div slot="header" class="clearfix">
          <span>我的部门：{{ currentStaff.departmentName }}</span>
        </div>
        <div>
          <p><strong>部门经理：</strong>{{ currentStaff.departmentManagerName || '无' }}</p>
          <h3>部门成员：</h3>
          <el-table :data="departmentMembers" style="width: 100%;" border>
            <el-table-column prop="username" label="姓名">
              <template slot-scope="scope">
                <el-link v-if="currentStaff.isManager && scope.row.staffId !== currentStaff.id" type="primary" @click="showRemoveMemberConfirm(scope.row)">{{ scope.row.username }}</el-link>
                <span v-else>{{ scope.row.username }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="身份">
              <template slot-scope="scope">
                <span :class="{'manager-text': scope.row.isManager}">{{ scope.row.isManager ? '部长' : '成员' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
    <div v-else-if="currentStaff && !currentStaff.departmentId">
      <!-- 未加入部门的员工显示提示和部门选择 -->
      <el-card class="box-card join-department-card">
        <div slot="header" class="clearfix">
          <span>你未加入任何部门，请选择一个加入</span>
        </div>
        <el-table :data="departments" style="width: 100%;" border>
          <el-table-column prop="name" label="部门名称"></el-table-column>
          <el-table-column prop="managerName" label="部门经理"></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="joinDepartment(scope.row.id)">加入</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <div v-else>
      <!-- 加载中或未登录状态 -->
      <p>加载中...</p>
    </div>


  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'DepartmentInfo',
  data() {
    return {
      departments: [],
      currentStaff: null,
      departmentMembers: [],

    };
  },
  created() {
    this.fetchCurrentStaffInfo();
  },
  methods: {
    async fetchCurrentStaffInfo() {
      try {
        const staffId = localStorage.getItem('staffId'); // 从 localStorage 获取 staffId
        if (!staffId) {
          console.error('未找到 staffId，请重新登录。');
          this.currentStaff = null;
          return;
        }
        const response = await axios.get(`http://localhost:8081/api/staff/current?staffId=${staffId}`);
        this.currentStaff = response.data;
        // 确保 isManager 属性被正确识别和响应式更新
        if (response.data && typeof response.data.isManager !== 'undefined') {
          this.$set(this.currentStaff, 'isManager', response.data.isManager);
        }
        console.log('当前员工信息:', this.currentStaff);
        if (this.currentStaff && this.currentStaff.departmentId) {
          this.fetchDepartmentMembers(this.currentStaff.departmentId);
        } else if (this.currentStaff && !this.currentStaff.departmentId) {
          this.fetchDepartments();
        }
      } catch (error) {
        console.error('获取当前员工信息失败:', error);
        this.currentStaff = null;
      }
    },
    async fetchDepartments() {
      try {
        const response = await axios.get('http://localhost:8081/api/departments');
        this.departments = response.data;
      } catch (error) {
        console.error('获取部门信息失败:', error);
        Message.error('获取部门信息失败！');
      }
    },
    async fetchDepartmentMembers(deptId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/departments/${deptId}/members`);
        let members = response.data;

        // 查找部长
        let manager = members.find(member => member.isManager);

        // 将部长置于列表顶端，其他员工按字母顺序排序
        if (manager) {
          members = members.filter(member => member.staffId !== manager.staffId);
          members.sort((a, b) => a.username.localeCompare(b.username));
          this.departmentMembers = [manager, ...members];
        } else {
          this.departmentMembers = members.sort((a, b) => a.username.localeCompare(b.username));
        }
        console.log('部门成员信息:', this.departmentMembers);
      } catch (error) {
        console.error('获取部门成员信息失败:', error);
        Message.error('获取部门成员信息失败！');
      }
    },
    showRemoveMemberConfirm(employee) {
      this.$confirm(`确定要将员工 ${employee.username} 踢出部门吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.removeDepartmentMember(employee.staffId);
      }).catch(() => {
        Message.info('已取消踢出操作');
      });
    },
    async removeDepartmentMember(staffId) {
      try {
        await axios.post(`http://localhost:8081/api/departments/${this.currentStaff.departmentId}/remove-member`, {
          staffId: staffId
        });
        Message.success('员工已踢出部门！');
        this.fetchDepartmentMembers(this.currentStaff.departmentId); // 刷新列表
      } catch (error) {
        console.error('踢出部门失败:', error);
        Message.error('踢出部门失败！');
      }
    },
    async joinDepartment(deptId) {
      try {
        // 从本地存储获取 staffId，或者从 currentStaff 中获取
        const staffId = this.currentStaff ? this.currentStaff.id : null;
        if (!staffId) {
          Message.error('无法获取员工ID，请重新登录！');
          return;
        }
        const response = await axios.post(`http://localhost:8081/api/staff/joinDepartment/${deptId}?staffId=${staffId}`);
        Message.success('成功加入部门！');
        this.currentStaff = response.data; // 更新 currentStaff
        // 重新获取当前员工信息，触发数据更新和视图渲染
        this.fetchCurrentStaffInfo();
      } catch (error) {
        console.error('加入部门失败:', error);
        Message.error('加入部门失败！');
      }
    },

  },

};
</script>

<style scoped>
.department-info-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.department-detail-card .el-table,
.join-department-card .el-table {
  margin-top: 15px;
}

.manager-text {
  color: red;
  font-weight: bold;
}
</style>
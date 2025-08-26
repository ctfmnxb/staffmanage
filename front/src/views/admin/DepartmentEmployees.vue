<template>
  <div class="department-employees-container">
    <h2>{{ deptName }}-员工信息</h2>
    <el-table :data="employees" style="width: 100%; margin-top: 20px;" border>
      <el-table-column prop="staffId" label="员工ID" width="80"></el-table-column>
      <el-table-column label="用户名">
        <template slot-scope="scope">
          <el-link type="primary" @click="showEmployeeActions(scope.row)">{{ scope.row.username }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="身份">
        <template slot-scope="scope">
          <span :class="{'manager-text': scope.row.isManager}">{{ scope.row.isManager ? '部长' : '成员' }}</span>
        </template>
      </el-table-column>
    </el-table>
    <el-button @click="goBack" style="margin-top: 20px;">返回部门管理</el-button>

    <!-- 操作按钮对话框 -->
    <el-dialog
      title="员工操作"
      :visible.sync="dialogVisible"
      width="30%"
      v-if="dialogVisible">
      <span>对员工 {{ selectedEmployee.username }} 进行操作：</span>
      <div style="margin-top: 20px;">
        <el-button
          type="success"
          @click="setDepartmentManager"
          v-if="!currentManagerId">设为部长</el-button>
        <el-button
          type="danger"
          @click="removeDepartmentMember">踢出部门</el-button>
        <el-button
          type="warning"
          @click="unsetDepartmentManager"
          v-if="selectedEmployee.isManager">取消部长</el-button>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'DepartmentEmployees',
  data() {
    return {
      deptId: null,
      deptName: null,
      employees: [],
      currentManagerId: null, // 用于存储当前部门的部长ID
      selectedEmployee: null, // 用于存储当前选中的员工信息
      dialogVisible: false // 控制操作按钮对话框的显示
    };
  },

  created() {
    this.deptId = Number(this.$route.query.deptId); // 确保 deptId 是数字类型
    this.deptName = this.$route.query.deptName;
    this.fetchDepartmentEmployees();
  },
  methods: {
    async fetchDepartmentEmployees() {
      if (!this.deptId) {
        console.error('部门ID未定义，无法获取员工信息。');
        Message.error('部门ID未定义，无法获取员工信息！');
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8081/api/departments/${this.deptId}/members`);
        let members = response.data;

        // 查找部长
        let manager = members.find(member => member.isManager);
        this.currentManagerId = manager ? manager.staffId : null;

        // 将部长置于列表顶端，其他员工按字母顺序排序
        if (manager) {
          members = members.filter(member => member.staffId !== manager.staffId);
          members.sort((a, b) => a.username.localeCompare(b.username));
          this.employees = [manager, ...members];
        } else {
          this.employees = members.sort((a, b) => a.username.localeCompare(b.username));
        }
      } catch (error) {
        console.error('获取部门员工信息失败:', error);
        Message.error('获取部门员工信息失败！');
      }
    },
    showEmployeeActions(employee) {
      this.selectedEmployee = employee;
      this.dialogVisible = true;
    },
    async setDepartmentManager() {
      try {
        const response = await axios.post(`http://localhost:8081/api/departments/${this.deptId}/set-manager`, {
          staffId: this.selectedEmployee.staffId
        });
        Message.success('设置部长成功！');
        this.dialogVisible = false;
        this.fetchDepartmentEmployees(); // 刷新列表
      } catch (error) {
        console.error('设置部长失败:', error);
        Message.error('设置部长失败！');
      }
    },
    async removeDepartmentMember() {
      try {
        await axios.post(`http://localhost:8081/api/departments/${this.deptId}/remove-member`, {
          staffId: this.selectedEmployee.staffId
        });
        Message.success('员工已踢出部门！');
        this.dialogVisible = false;
        this.fetchDepartmentEmployees(); // 刷新列表
      } catch (error) {
        console.error('踢出部门失败:', error);
        Message.error('踢出部门失败！');
      }
    },
    async unsetDepartmentManager() {
      try {
        await axios.post(`http://localhost:8081/api/departments/${this.deptId}/unset-manager`, {
          staffId: this.selectedEmployee.staffId
        });
        Message.success('取消部长成功！');
        this.dialogVisible = false;
        this.fetchDepartmentEmployees(); // 刷新列表
      } catch (error) {
        console.error('取消部长失败:', error);
        Message.error('取消部长失败！');
      }
    },
    goBack() {
      this.$router.push({ name: 'DepartmentManagement' });
    }
  }
};
</script>

<style scoped>
.department-employees-container {
  padding: 20px;
}

.manager-text {
  color: red;
  font-weight: bold;
}
</style>
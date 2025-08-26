<template>
  <div class="department-management-container">
    <h2 style="text-align: center; margin-bottom: 20px;">部门管理</h2>

    <!-- 创建新部门表单 -->
    <el-card class="box-card create-department-card">
        <div slot="header" class="clearfix">
          <span>创建新部门</span>
        </div>
        <el-form :model="newDepartmentForm" :rules="rules" ref="newDepartmentForm" label-width="100px">
          <el-form-item label="部门名称" prop="name">
            <el-input v-model="newDepartmentForm.name"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="createDepartment">立即创建</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 部门信息表格 -->
      <el-table :data="departments" style="width: 100%; margin-top: 20px;" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column label="部门名称">
          <template v-slot:default="scope">
            <router-link :to="{ name: 'DepartmentEmployees', query: { deptId: scope.row.id, deptName: scope.row.name } }" class="department-name-link">
              {{ scope.row.name }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="manager_name" label="组长" width="150">
          <template v-slot:default="scope">
            {{ scope.row.manager_name || '无' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template v-slot:default="scope">
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row.id)">删除</el-button>
            <el-button
              size="mini"
              @click="viewEmployees(scope.row)">查看员工</el-button>

          </template>
        </el-table-column>
      </el-table>
    <!-- 查看部门成员对话框 -->
    <el-dialog
      title="部门成员"
      :visible.sync="dialogVisible"
      width="50%">
      <el-table :data="selectedDepartmentMembers" style="width: 100%;" border>
        <el-table-column prop="username" label="姓名"></el-table-column>
        <el-table-column prop="role" label="身份">
          <template v-slot:default="scope">
            {{ scope.row.isManager ? '部长' : '成员' }}
          </template>
        </el-table-column>
      </el-table>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { Message, MessageBox } from 'element-ui';

export default {
  name: 'DepartmentManagement',
  data() {
    return {
      departments: [],
      departmentMembers: [], // 存储部门成员信息
      newDepartmentForm: {
        name: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入部门名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ]
      },
      dialogVisible: false, // 控制查看部门成员对话框的显示
      selectedDepartmentMembers: [] // 存储选定部门的成员信息
    };
  },
  created() {
    this.fetchDepartments();
  },
  methods: {

    async fetchDepartments() {
      try {
        const response = await axios.get('http://localhost:8081/api/departments');
        this.departments = response.data;
        console.log('fetchDepartments 成功，departments:', this.departments);
      } catch (error) {
        console.error('fetchDepartments 失败:', error);
        Message.error('获取部门信息失败！');
      }
    },
    async fetchDepartmentMembers(deptId) {
      try {
        // 假设后端有一个接口来获取指定部门的成员信息
        const response = await axios.get(`http://localhost:8081/api/departments/${deptId}/members`);
        this.departmentMembers = response.data;
      } catch (error) {
        console.error('获取部门成员信息失败:', error);
        Message.error('获取部门成员信息失败！');
      }
    },

    createDepartment() {
      this.$refs.newDepartmentForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await axios.post('http://localhost:8081/api/departments', {
              name: this.newDepartmentForm.name
            });
            console.log('创建部门成功响应:', response.data);
            Message.success('部门创建成功！');
            this.newDepartmentForm.name = '';
            this.fetchDepartments(); // 刷新列表
          } catch (error) {
            console.error('创建部门失败，错误详情:', error.response ? error.response.data : error.message || error);
            if (error.response && error.response.status === 400) {
              Message.error('部门名称已存在！');
            } else {
              Message.error('创建部门失败！');
            }
          }
        } else {
          Message.warning('请检查部门名称输入！');
          return false;
        }
      });
    },
    handleDelete(deptId) {
      MessageBox.confirm('此操作将永久删除该部门, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
        await axios.delete(`http://localhost:8081/api/departments/${deptId}`);
          Message.success('删除成功!');
          this.fetchDepartments(); // 刷新列表
        } catch (error) {
          console.error('删除部门失败:', error);
          Message.error('删除部门失败！');
        }
      });
    },
    viewEmployees(department) {
      this.$router.push({
        name: 'DepartmentEmployees',
        query: { deptId: department.id, deptName: department.name }
      });
    },

  }
};
</script>

<style scoped>
.department-management-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
}

.create-department-card {
  width: 60%; /* 调整宽度以适应居中布局 */
  max-width: 600px; /* 设置最大宽度，避免过宽 */
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  border-radius: 8px; /* 圆角 */
}

.create-department-card .el-form-item {
  margin-bottom: 20px;
}

.create-department-card .el-button {
  width: 100%;
}

.department-name-link {
  color: #409EFF;
  text-decoration: none;
}

.department-name-link:hover {
  text-decoration: underline;
}

.department-detail-card,
.join-department-card {
  width: 80%; /* 调整宽度以适应居中布局 */
  max-width: 800px; /* 设置最大宽度，避免过宽 */
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  border-radius: 8px; /* 圆角 */
}

.department-detail-card h3 {
  margin-top: 20px;
  margin-bottom: 10px;
}

.department-detail-card p {
  margin-bottom: 10px;
}
</style>
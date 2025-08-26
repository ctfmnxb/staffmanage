<template>
  <div class="employee-management-container">
    <h2>员工信息管理</h2>
    <el-table :data="paginatedStaffs" stripe border>
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="password" label="密码"></el-table-column>
      <el-table-column prop="attendanceDays" label="考勤天数"></el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalStaffs">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'EmployeeManagement',
  data() {
    return {
      staffs: [],
      currentPage: 1,
      pageSize: 10,
      totalStaffs: 0
    };
  },
  computed: {
    paginatedStaffs() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.staffs.slice(start, end);
    }
  },
  created() {
    this.fetchStaffs();
  },
  methods: {
    fetchStaffs() {
      this.$axios.get('/api/staffs')
        .then(response => {
          this.staffs = response.data;
          this.totalStaffs = response.data.length;
        })
        .catch(error => {
          console.error('获取员工信息失败:', error);
        });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1; // Reset to first page when page size changes
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    }
  }
};
</script>

<style scoped>
.employee-management-container {
  padding: 20px;
  max-width: 1000px; /* 限制最大宽度，防止过宽 */
  margin: 0 auto; /* 居中显示 */
}
</style>
<template>
  <div>
    <h2>考勤管理</h2>
    <el-date-picker
      v-model="selectedDate"
      type="date"
      placeholder="选择日期"
      @change="fetchAttendance"
      value-format="yyyy-MM-dd">
    </el-date-picker>

    <el-table :data="attendanceRecords" stripe style="width: 100%; margin-top: 20px;" :row-class-name="tableRowClassName">
      <el-table-column prop="staffId" label="员工ID" width="180"></el-table-column>
      <el-table-column prop="name" label="员工姓名" width="180"></el-table-column>
      <el-table-column prop="checkDate" label="日期" width="180" :formatter="formatDate"></el-table-column>
      <el-table-column label="考勤状态">
        <template slot-scope="scope">
          <span v-if="scope.row.status === 1" style="color: green;">已签到</span>
          <span v-else-if="scope.row.status === 2" style="color: orange;">请假</span>
          <span v-else style="color: red;">未签到</span>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="attendanceRecords.length === 0 && selectedDate" style="margin-top: 20px; color: #909399;">
      当日无考勤记录。
    </div>
  </div>
</template>

<script>
export default {
  name: 'AttendanceManagement',
  data() {
    return {
      selectedDate: new Date().toISOString().slice(0, 10), // 初始化为当前日期
      attendanceRecords: []
    };
  },
  methods: {
    tableRowClassName({row, rowIndex}) {
      if (row.status === 2) {
        return 'warning-row'; // 请假状态显示黄色
      } else if (row.status === 1) {
        return 'success-row'; // 已签到状态显示绿色
      }
      return '';
    },
    formatDate(row, column) {
      const date = new Date(row.checkDate);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    async fetchAttendance() {
      if (!this.selectedDate) {
        this.attendanceRecords = [];
        return;
      }

      try {
        // 获取指定日期的所有员工考勤状态
        console.log('请求考勤状态，日期:', this.selectedDate);
        const response = await this.$axios.get(`/api/admin/attendance-status/${this.selectedDate}`);
        console.log('考勤状态响应:', response.data);
        this.attendanceRecords = response.data.map(record => ({
          staffId: record.staffId,
          name: record.staffName,
          checkDate: record.checkDate,
          status: record.status, // 0: 未签到, 1: 已签到, 2: 请假
          leaveReason: record.leaveReason // 请假原因
        }));

        console.log('最终考勤记录:', this.attendanceRecords);
      } catch (error) {
        console.error('获取考勤信息失败:', error);
        const errorMessage = error.response?.data?.message || 
          (error.response?.status === 404 ? '未找到考勤记录' : 
           error.response?.status === 401 ? '未授权访问' : 
           error.response?.status === 500 ? '服务器内部错误' : 
           '获取考勤信息失败，请检查网络连接或联系管理员');
        this.$message.error(errorMessage);
        this.attendanceRecords = [];
      }
    }
  },
  mounted() {
    this.fetchAttendance(); // 在组件挂载时调用fetchAttendance方法
  }

};
</script>

<style scoped>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>
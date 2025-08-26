<template>
  <div class="personal-leave-review-container">
    <h2>我的请假申请</h2>
    <el-table :data="leaveApplications" style="width: 100%">
      <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
      <el-table-column prop="reason" label="请假原因"></el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120" :formatter="formatDateColumn"></el-table-column>
      <el-table-column prop="endDate" label="结束日期" width="120" :formatter="formatDateColumn"></el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="180" :formatter="formatDateColumn"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button
            v-if="(scope.row.status === 1 || scope.row.status === 2) && !scope.row.isRead"
            size="mini"
            type="primary"
            @click="markAsRead(scope.row.id)"
          >已读</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="danger"
            @click="withdrawApplication(scope.row.id)"
          >撤回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'PersonalLeaveReview',
  data() {
    return { 
      leaveApplications: [] 
    }
  },
  created() {
    this.fetchPersonalLeaveApplications();
  },
  methods: {
    async fetchPersonalLeaveApplications() {
      try {
        const staffId = localStorage.getItem('staffId'); // 获取当前登录员工的ID
        if (!staffId) {
          this.$message.error('未找到员工ID，请重新登录。');
          return;
        }
        const response = await axios.get(`/api/leave-applications/staff/${staffId}`);
        this.leaveApplications = response.data;
      }
      catch (error) {
        this.$message.error('获取个人请假申请失败。');
        console.error('Error fetching personal leave applications:', error);
      }
    },
    async markAsRead(id) {
      try {
        await axios.put(`/api/leave-applications/${id}/mark-as-read`);
        this.$message.success('已标记为已读。');
        this.fetchPersonalLeaveApplications(); // Refresh the list
      } catch (error) {
        this.$message.error('标记为已读失败。');
        console.error('Error marking as read:', error);
      }
    },
    async withdrawApplication(id) {
      try {
        await axios.delete(`/api/leave-applications/${id}`);
        this.$message.success('请假申请已撤回。');
        this.fetchPersonalLeaveApplications(); // Refresh the list
      } catch (error) {
        this.$message.error('撤回请假申请失败。');
        console.error('Error withdrawing application:', error);
      }
    },
    getStatusTagType(status) {
      switch (status) {
        case 0:
          return 'warning';
        case 1:
          return 'success';
        case 2:
          return 'danger';
        case 3:
          return 'info'; // For withdrawn status
        default:
          return 'info';
      }
    },
    getStatusText(status) {
      switch (status) {
        case 0:
          return '未审核';
        case 1:
          return '已同意';
        case 2:
          return '不同意';
        case 3:
          return '已撤回';
        default:
          return '未知';
      }
    },
    formatDateColumn(row, column, cellValue) {
      if (cellValue) {
        const date = new Date(cellValue);
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
      }
      return '';
    }
  }
}
</script>

<style scoped>
.personal-leave-review-container {
  padding: 20px;
}
</style>
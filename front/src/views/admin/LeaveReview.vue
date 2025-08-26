<template>
  <div class="message-center-container">
    <div class="header-buttons">
      <el-button type="primary" @click="markAsRead">标记为已读</el-button>
      <el-button type="danger" @click="deleteMessages">删除</el-button>
      <el-button @click="markAllAsRead">全部标为已读</el-button>
    </div>

    <el-table :data="messages" style="width: 100%" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column label="消息内容">
        <template slot-scope="scope">
          <a href="#" @click.prevent="handleContentClick(scope.row)" style="color: blue;">{{ scope.row.content }}</a>
        </template>
      </el-table-column>
      <el-table-column prop="receiveTime" label="接收时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="120"></el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalMessages">
    </el-pagination>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';
export default {
  name: 'LeaveReview',
  data() {
    return {
      messages: [],
      currentPage: 1,
      pageSize: 10,
      totalMessages: 0,
      selectedMessages: [] // 用于存储选中的消息
    };
  },
  created() {
    this.fetchMessages();
  },
  methods: {
    async fetchMessages() {
      try {
        const leaveApplicationsResponse = await axios.get('http://localhost:8080/api/leave-applications/all');
        const staffResponse = await axios.get('http://localhost:8080/api/staffs');
        const staffs = staffResponse.data;

        this.messages = leaveApplicationsResponse.data.map(item => {
          const staff = staffs.find(s => s.id === item.staffId);
          const username = staff ? staff.username : '未知用户';
          return {
            id: item.id,
            username: username,
            content: `请假申请：${item.reason} (从 ${item.startDate} 到 ${item.endDate})`,
            receiveTime: item.createdAt,
            status: item.status === 0 ? '待审批' : (item.status === 1 ? '已批准' : '已拒绝'),
            originalData: item // 保存原始数据，方便后续操作
          };
        });
        this.totalMessages = this.messages.length;
      } catch (error) {
        console.error('获取请假消息失败:', error);
        this.$message.error('获取请假消息失败！');
      }
    },
    async markAsRead() {
      if (this.selectedMessages.length === 0) {
        this.$message.warning('请选择要标记为已读的消息！');
        return;
      }
      try {
        for (const message of this.selectedMessages) {
          try {
            await axios.put(`http://localhost:8080/api/leave-applications/${message.id}/mark-as-read`);
          } catch (error) {
            console.error(`标记消息 ${message.id} 为已读失败:`, error);
            this.$message.error(`标记消息 ${message.id} 为已读失败！`);
          }
        }
        this.$message.success('已将选中消息标记为已读！');
        this.fetchMessages(); // 刷新列表
      } catch (error) {
        console.error('标记为已读失败:', error);
        this.$message.error('标记为已读失败！');
      }
    },
    async deleteMessages() {
      if (this.selectedMessages.length === 0) {
        this.$message.warning('请选择要删除的消息！');
        return;
      }
      try {
        for (const message of this.selectedMessages) {
          await axios.delete(`http://localhost:8080/api/leave-applications/${message.id}`);
        }
        this.$message.success('已删除选中消息！');
        this.fetchMessages(); // 刷新列表
      } catch (error) {
        console.error('删除消息失败:', error);
        this.$message.error('删除消息失败！');
      }
    },
    async markAllAsRead() {
      try {
        // 假设后端有一个接口可以批量处理所有待审批的请假申请
        // 这里简化处理，直接获取所有待审批的请假，并逐一批准
        const response = await axios.get('http://localhost:8080/api/leave-applications/pending');
        const pendingApplications = response.data;

        if (pendingApplications.length === 0) {
          this.$message.info('没有待审批的请假申请。');
          return;
        }

        for (const app of pendingApplications) {
          try {
            await axios.put(`http://localhost:8080/api/leave-applications/${app.id}/mark-as-read`);
          } catch (error) {
            console.error(`标记所有待审批消息 ${app.id} 为已读失败:`, error);
            this.$message.error(`标记所有待审批消息 ${app.id} 为已读失败！`);
          }
        }
        this.$message.success('已将所有待审批消息标记为已读！');
        this.fetchMessages(); // 刷新列表
      } catch (error) {
        console.error('全部标记为已读失败:', error);
        this.$message.error('全部标记为已读失败！');
      }
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchMessages();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchMessages();
    },
    handleSelectionChange(selection) {
      this.selectedMessages = selection;
    },
    handleContentClick(row) {
      // 点击消息内容跳转到新的详情页面
      this.$router.push({
        name: 'LeaveMessageDetail',
        params: { id: row.id, username: row.username, content: row.originalData }
      });
    }
  }
};
</script>

<style scoped>
.message-center-container {
  padding: 20px;
}

.header-buttons {
  margin-bottom: 20px;
  text-align: right; /* 将按钮靠右对齐 */
}

.el-table {
  margin-bottom: 20px;
}
</style>
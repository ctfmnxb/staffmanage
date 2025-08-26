<template>
  <div class="leave-message-detail-container">
    <h2>来自 {{ username }} 的消息</h2>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>请假详情</span>
      </div>
      <div class="text item">
        <p><strong>员工姓名:</strong> {{ username }}</p>
        <p><strong>请假原因:</strong> {{ message.reason }}</p>
        <p><strong>开始日期:</strong> {{ message.startDate }}</p>
        <p><strong>结束日期:</strong> {{ message.endDate }}</p>
        <p><strong>申请时间:</strong> {{ message.createdAt }}</p>
        <p><strong>当前状态:</strong> {{ statusText }}</p>
      </div>
    </el-card>

    <div class="action-buttons">
      <el-button type="success" @click="approveLeave" :disabled="isProcessed">同意</el-button>
      <el-button type="danger" @click="rejectLeave" :disabled="isProcessed">不同意</el-button>
      <el-button @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'LeaveMessageDetail',
  props: {
    id: [String, Number],
    username: String,
    content: Object // 接收传递过来的原始请假数据
  },
  data() {
    return {
      message: {},
      isProcessed: false // 标记消息是否已处理
    };
  },
  created() {
    if (this.content) {
      this.message = this.content;
      this.isProcessed = this.message.status !== 0; // 如果状态不是待审批，则表示已处理
      // 如果消息未读且未被拒绝，则自动标记为已读
      if (!this.message.isRead && this.message.status !== 2) {
        this.markAsRead(this.message.id);
      }
    } else if (this.id) {
      // 如果直接通过ID访问，需要从后端获取数据
      this.fetchMessageDetail(this.id);
    }
  },
  computed: {
    statusText() {
      if (this.message.status === 0) return '待审批';
      if (this.message.status === 1) return '已批准';
      if (this.message.status === 2) return '已拒绝';
      return '未知';
    }
  },
  methods: {
    async fetchMessageDetail(id) {
      try {
        const leaveResponse = await axios.get(`http://localhost:8080/api/leave-applications/${id}`);
        this.message = leaveResponse.data;
        this.isProcessed = this.message.status !== 0;

        // 获取员工用户名
        const staffResponse = await axios.get(`http://localhost:8080/api/staffs/${this.message.staffId}`);
        this.username = staffResponse.data.username;

      } catch (error) {
        console.error('获取请假详情失败:', error);
        Message.error('获取请假详情失败！');
      }
    },
    async approveLeave() {
      try {
        await axios.put(`http://localhost:8080/api/leave-applications/${this.message.id}/approve`);
        Message.success('请假申请已同意！');
        this.message.status = 1; // 更新本地状态
        this.isProcessed = true;
      } catch (error) {
        console.error('同意请假失败:', error);
        Message.error('同意请假失败！');
      }
    },
    async rejectLeave() {
      try {
        await axios.put(`http://localhost:8080/api/leave-applications/${this.message.id}/reject`);
        Message.success('请假申请已拒绝！');
        this.message.status = 2; // 更新本地状态
        this.isProcessed = true;
      } catch (error) {
        console.error('拒绝请假失败:', error);
        Message.error('拒绝请假失败！');
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    async markAsRead(messageId) {
      try {
        await axios.put(`http://localhost:8080/api/leave-applications/${messageId}/mark-as-read`);
        // Message.success('消息已标记为已读！'); // 不再显示成功消息，避免打扰用户
        this.message.isRead = true; // 更新本地状态
      } catch (error) {
        console.error(`标记消息 ${messageId} 为已读失败:`, error);
        // Message.error(`标记消息 ${messageId} 为已读失败！`); // 不再显示错误消息，避免打扰用户
        
      }
    }
  }
};
</script>

<style scoped>
.leave-message-detail-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.action-buttons {
  text-align: center;
}

.text.item p {
  margin-bottom: 10px;
}
</style>
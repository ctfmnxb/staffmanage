<template>
  <div class="personal-attendance-container">
    <el-card class="box-card">
      <h2>{{ username }} 的个人考勤记录</h2>
      <div class="calendar-container">
        <el-calendar v-model="value">
          <template
            slot="dateCell"
            slot-scope="{date, data}">
            <div v-if="attendanceStatus[data.day]">
              <p :class="{'is-selected': data.isSelected, 'is-past-date': !isToday(data.day) && new Date(data.day) < new Date()}">
                {{ data.day.split('-').slice(1).join('-') }}
              </p>
              <div v-if="attendanceStatus[data.day].status === 1" class="signed-icon-container">
                <span class="signed-icon">✔</span>
              </div>
              <div v-else-if="attendanceStatus[data.day].status === 2" class="leave-icon-container">
                <span class="leave-icon">—</span>
                <el-tooltip class="item" effect="dark" :content="attendanceStatus[data.day].leaveReason" placement="top">
                  <span class="leave-text">请假</span>
                </el-tooltip>
              </div>
              <div v-else-if="attendanceStatus[data.day].status === 0">
                <el-button v-if="isToday(data.day)" type="primary" size="mini" @click="signIn(data.day)">签到</el-button>
              </div>
            </div>
            <div v-else>
              <p :class="{'is-selected': data.isSelected, 'is-past-date': !isToday(data.day) && new Date(data.day) < new Date()}">
                {{ data.day.split('-').slice(1).join('-') }}
              </p>
              <el-button v-if="isToday(data.day)" type="primary" size="mini" @click="signIn(data.day)">签到</el-button>
            </div>
          </template>
        </el-calendar>
      </div>

      <h3>考勤记录</h3>
      <el-table :data="attendanceRecords" style="width: 100%">
        <el-table-column prop="checkDate" label="日期" :formatter="formatDateColumn"></el-table-column>
        <el-table-column prop="staffId" label="员工ID"></el-table-column>
        <el-table-column prop="staffName" label="员工姓名"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { Message } from 'element-ui';
import axios from 'axios';

export default {
  name: 'PersonalAttendance',
  data() {
    return {
      value: new Date(),
      attendanceRecords: [],
      attendanceStatus: {}, // 存储每日考勤状态，包括请假信息
      staffId: null,
      username: ''
    };
  },
  watch: {
    value(newVal, oldVal) {
      // 当日历显示月份改变时，重新获取考勤状态
      if (newVal.getMonth() !== oldVal.getMonth() || newVal.getFullYear() !== oldVal.getFullYear()) {
        this.fetchAttendanceStatusForMonth();
      }
    }
  },
  created() {
    const storedStaffId = localStorage.getItem('staffId');
    const storedUsername = localStorage.getItem('username');
    if (storedStaffId && !isNaN(Number(storedStaffId))) {
      this.staffId = Number(storedStaffId);
      this.fetchAttendanceStatusForMonth(); // Initial fetch
    } else {
      console.error('Invalid staffId in localStorage:', storedStaffId);
      Message.error('员工ID无效，无法获取考勤记录。请重新登录。');
    }
    if (storedUsername) {
      this.username = storedUsername;
    }
  },
  methods: {
    isToday(dateString) {
      const today = new Date();
      const date = new Date(dateString);
      return date.getFullYear() === today.getFullYear() &&
             date.getMonth() === today.getMonth() &&
             date.getDate() === today.getDate();
    },
    isSigned(dateString) {
      // 检查 attendanceStatus 中是否存在该日期的签到记录，并且状态为已签到 (status: 1)。
      return this.attendanceStatus[dateString] && this.attendanceStatus[dateString].status === 1;
    },
    async fetchAttendanceStatusForMonth() {
      if (!this.staffId) return;
      const year = this.value.getFullYear();
      const month = this.value.getMonth();
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      this.attendanceStatus = {}; // Clear previous status

      for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(year, month, i);
        const dateString = this.formatDate(date);
        try {
          const response = await axios.get(`/api/attendances/status/${this.staffId}/${dateString}`);
          if (response.status === 200) {
            this.$set(this.attendanceStatus, dateString, response.data);
          }
        } catch (error) {
          console.error(`Error fetching attendance status for ${dateString}:`, error);
          this.$set(this.attendanceStatus, dateString, { status: '未知', leaveReason: null });
        }
      }
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
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
    },
    async signIn(dateString) {
      if (!this.isToday(dateString)) {
        Message.warning('只能签到今日日期！');
        return;
      }
      if (this.isSigned(dateString)) {
        Message.info('今日已签到！');
        return;
      }

      if (!this.staffId || isNaN(Number(this.staffId))) {
        Message.error('员工ID无效，无法签到。请重新登录。');
        console.error('Invalid staffId:', this.staffId);
        return;
      }

      try {
        const response = await axios.post('/api/daily-checkin', {
          staffId: Number(this.staffId),
          checkDate: dateString
        });
        if (response.status === 200) {
          Message.success(`${dateString} 签到成功！`);
          this.fetchAttendanceStatusForMonth(); // 重新获取考勤记录以更新日历显示
        }
      } catch (error) {
        console.error('签到失败:', error);
        Message.error('签到失败！');
      }
    },

    // async fetchAttendanceRecords() {
    //   console.log('Fetching attendance records for staffId:', this.staffId);
    //   if (!this.staffId) {
    //     Message.error('员工ID未找到，无法获取考勤记录。请重新登录。');
    //     console.error('Staff ID is null or undefined.');
    //     return;
    //   }
    //   try {
    //     const response = await axios.get(`/api/daily-checkin/${this.staffId}`);
    //     if (response.status === 200) {
    //       this.attendanceRecords = response.data.map(record => ({
    //         checkDate: record.checkDate,
    //         staffId: record.staffId,
    //         staffName: record.staffName || '未知员工' // 如果后端没有提供员工姓名，则显示“未知员工”
    //       }));
    //       console.log('Attendance records fetched successfully:', this.attendanceRecords);
    //     } else {
    //       Message.error(`获取考勤记录失败: ${response.status} - ${response.statusText}`);
    //       console.error('Failed to fetch attendance records:', response);
    //     }
    //   } catch (error) {
    //     console.error('获取考勤记录请求失败:', error.response ? error.response.data : error.message);
    //     Message.error('获取考勤记录失败！请检查网络或联系管理员。');
    //   }
    // }
  }
};
</script>

<style scoped>
.personal-attendance-container {
  padding: 20px;
}

.box-card {
  width: 100%;
  max-width: 900px; /* 调整最大宽度以适应日历和表格 */
  margin: 0 auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center; /* 使内容居中 */
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.calendar-container {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
}

.signed-icon {
  color: #67c23a; /* 绿色 */
  font-size: 24px;
  font-weight: bold;
  display: block;
  text-align: center;
  margin-top: 10px;
}

.leave-icon-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.leave-icon {
  color: #E6A23C; /* 黄色 */
  font-size: 24px;
  font-weight: bold;
  display: block;
  text-align: center;
}

.leave-text {
  color: #E6A23C; /* 黄色 */
  font-size: 12px;
  margin-top: 2px;
}

.el-calendar-day {
  box-sizing: border-box;
  padding: 8px;
  height: 80px; /* 增加高度以容纳按钮 */
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.el-calendar-day p {
  margin: 0;
  font-size: 16px;
  line-height: 1;
}

.el-calendar-day .el-button {
  margin-top: 5px;
}

.is-selected {
  color: #409EFF;
  font-weight: bold;
}

.is-past-date {
  color: #ccc;
  text-decoration: line-through;
}

.el-calendar-day .signed-icon-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-calendar-day .leave-icon-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}



.signed-icon-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

h3 {
  margin-top: 30px;
  margin-bottom: 15px;
  color: #333;
}
</style>
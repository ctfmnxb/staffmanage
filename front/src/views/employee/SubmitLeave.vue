<template>
  <div>
    <h2>提交请假申请</h2>
    <el-form :model="leaveForm" :rules="rules" ref="leaveForm" label-width="100px" class="leave-form">
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="leaveForm.startDate"
          type="date"
          placeholder="选择日期"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptionsStart">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="leaveForm.endDate"
          type="date"
          placeholder="选择日期"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptionsEnd">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="请假原因" prop="reason">
        <el-input type="textarea" v-model="leaveForm.reason" maxlength="200" show-word-limit></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('leaveForm')">提交申请</el-button>
        <el-button @click="resetForm('leaveForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'SubmitLeave',
  data() {
    return {
      leaveForm: {
        staffId: null,
        startDate: '',
        endDate: '',
        reason: '',
        status: 0
      },
      pickerOptionsStart: {
        disabledDate: (time) => {
          return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
        }
      },
      pickerOptionsEnd: {
        disabledDate: (time) => {
          const startDate = new Date(this.leaveForm.startDate);
          const tenDaysLater = new Date(startDate.getTime() + 10 * 24 * 60 * 60 * 1000);
          return time.getTime() < startDate.getTime() || time.getTime() > tenDaysLater.getTime();
        }
      },
      rules: {
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        endDate: [
          { required: true, message: '请选择结束日期', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入请假原因', trigger: 'blur' },

          { max: 200, message: '请假原因不能超过200个字符', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.leaveForm.staffId = localStorage.getItem('staffId');
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          try {
            const response = await axios.post('http://localhost:8080/api/leave-applications', this.leaveForm);
            if (response.status === 200) {
              Message.success('请假申请提交成功！');
              this.$refs[formName].resetFields();
            }
          } catch (error) {
            console.error('请假申请提交失败:', error);
            Message.error('请假申请提交失败！');
          }
        } else {
          Message.error('请检查表单填写！');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
};
</script>

<style scoped>
.leave-form .el-textarea__inner {
  min-height: 180px !important; /* 调整文本框的最小高度，使其显示更大 */
  resize: none; /* 禁止用户缩放 */
  width: 500px; /* 调整文本框的宽度 */
}
</style>
<template>
  <div>
    <h2>工资管理</h2>
    <el-form :model="salaryForm" label-width="120px">
      <el-form-item label="员工ID">
        <el-input v-model="salaryForm.staffId"></el-input>
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="salaryForm.date" type="date" placeholder="选择日期" :picker-options="datePickerOptions" value-format="yyyy-MM-dd"></el-date-picker>
      </el-form-item>
      <el-form-item label="基本工资">
        <el-input v-model="salaryForm.baseSalary"></el-input>
      </el-form-item>
      <el-form-item label="奖金">
        <el-input v-model="salaryForm.bonus"></el-input>
      </el-form-item>
      <el-form-item label="扣除项">
        <el-input v-model="salaryForm.deduction"></el-input>
      </el-form-item>
      <el-form-item label="总工资">
        <el-input v-model="totalSalary" disabled></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'SalaryManagement',
  data() {
    return {
      salaryForm: {
        staffId: '',
        date: '',
        baseSalary: '',
        bonus: '',
        deduction: ''
      }
    };
  },
  computed: {
    totalSalary() {
      const base = parseFloat(this.salaryForm.baseSalary) || 0;
      const bonus = parseFloat(this.salaryForm.bonus) || 0;
      const deduction = parseFloat(this.salaryForm.deduction) || 0;
      return (base + bonus - deduction).toFixed(2);
    },
    datePickerOptions() {
      return {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
        }
      };
    }
  },
  methods: {
    async submitForm() {
      try {
        const payload = {
          ...this.salaryForm,
          totalSalary: this.totalSalary
        };
        const response = await axios.post('/api/salaries', payload);
        if (response.status === 200) {
          this.$message.success('工资数据提交成功！');
          // 清空表单
          this.salaryForm = {
            staffId: '',
            date: '',
            baseSalary: '',
            bonus: '',
            deduction: ''
          };
        }
      } catch (error) {
        console.error('提交工资数据失败:', error);
        this.$message.error('提交工资数据失败！');
      }
    }
  }
};
</script>

<style scoped>
/* 样式 */
</style>
<template>
  <div>
    <h2>个人工资记录</h2>
    <el-table :data="salaryRecords" style="width: 100%">
      <el-table-column prop="date" label="日期">
        <template slot-scope="scope">
          {{ new Date(scope.row.date).toLocaleDateString() }}
        </template>
      </el-table-column>
      <el-table-column prop="baseSalary" label="基本工资"></el-table-column>
      <el-table-column prop="bonus" label="奖金">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.bonus > 0 ? 'green' : 'inherit' }">{{ scope.row.bonus }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="deduction" label="扣除">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.deduction > 0 ? 'red' : 'inherit' }">{{ scope.row.deduction }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="totalSalary" label="总工资"></el-table-column>
    </el-table>
  </div>
</template>

<script>
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  name: 'PersonalSalary',
  data() {
    return {
      salaryRecords: [],
      staffId: null
    };
  },
  created() {
    this.staffId = localStorage.getItem('staffId');
    if (this.staffId) {
      this.fetchSalaryRecords();
    }
  },
  methods: {
    async fetchSalaryRecords() {
      try {
        const response = await axios.get(`/api/salaries/staff/${this.staffId}`);
        if (response.status === 200) {
          this.salaryRecords = response.data;
        }
      } catch (error) {
        console.error('获取工资记录失败:', error);
        Message.error('获取工资记录失败！');
      }
    }
  }
};
</script>

<style scoped>
/* 样式 */
</style>
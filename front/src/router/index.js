import Vue from 'vue'
import VueRouter from 'vue-router'
import UserLogin from '@/components/UserLogin.vue'
import AdminLogin from '@/components/AdminLogin.vue'
import UserRegister from '@/components/UserRegister.vue'
import AdminRegister from '@/components/AdminRegister.vue'
import Staff from '@/components/EmployeeDashboard.vue'
import Admin from '@/components/AdminDashboard.vue'
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/user-login'
  },
  {
    path: '/user-login',
    name: 'UserLogin',
    component: UserLogin
  },
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/user-register',
    name: 'UserRegister',
    component: UserRegister
  },
  {
    path: '/admin-register',
    name: 'AdminRegister',
    component: AdminRegister
  },
  {
    path: '/staff',
    name: 'Staff',
    component: Staff,
    children: [
      {
        path: 'personal-leave-review',
        name: 'PersonalLeaveReview',
        component: () => import('@/views/employee/PersonalLeaveReview.vue')
      },
      {
        path: 'department-info',
        name: 'DepartmentInfo',
        component: () => import('@/views/employee/DepartmentInfo.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    children: [
      {
        path: 'employee',
        name: 'EmployeeManagement',
        component: () => import('@/views/admin/EmployeeManagement.vue')
      },
      {
        path: 'department',
        name: 'DepartmentManagement',
        component: () => import('@/views/admin/DepartmentManagement.vue')
      },
      {
        path: 'attendance',
        name: 'AttendanceManagement',
        component: () => import('@/views/admin/AttendanceManagement.vue')
      },
      {
        path: 'salary',
        name: 'SalaryManagement',
        component: () => import('../views/admin/SalaryManagement.vue')
      },
      {
        path: 'leave-review',
        name: 'LeaveReview',
        component: () => import('../views/admin/LeaveReview.vue')
      },
      {
        path: 'leave-message-detail/:id',
        name: 'LeaveMessageDetail',
        component: () => import('../views/admin/LeaveMessageDetail.vue'),
        props: true
      },

      {
        path: 'department-employees/:deptId/:deptName',
        name: 'DepartmentEmployees',
        component: () => import('../views/admin/DepartmentEmployees.vue'),
        props: true
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach(async (to, from, next) => {
  // 检查是否访问管理员路由
  if (to.path.startsWith('/admin') && to.path !== '/admin-login' && to.path !== '/admin-register') {
    const username = localStorage.getItem('username')
    if (!username) {
      next('/admin-login')
      return
    }
    try {
      // 验证用户是否为管理员
      const response = await fetch(`/api/admin/verify/${username}`)
      if (!response.ok) {
        localStorage.removeItem('username')
        next('/admin-login')
        return
      }
      next()
    } catch (error) {
      console.error('验证管理员身份时出错:', error)
      next('/admin-login')
    }
  } else {
    next()
  }
})

export default router
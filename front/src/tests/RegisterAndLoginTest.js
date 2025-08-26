const axios = require('axios');

const testUser = {
    username: 'James',
    password: '123456'
};

// 测试注册功能
async function testRegister() {
    try {
        const response = await axios.post('http://localhost:8081/register', testUser);
        if (response.data.success) {
            console.log('注册成功');
            return true;
        } else {
            console.log('注册失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('注册出错:', error.message);
        return false;
    }
}

// 测试登录功能
async function testLogin() {
    try {
        const response = await axios.post('http://localhost:8081/login', testUser);
        if (response.data.success) {
            console.log('登录成功');
            return true;
        } else {
            console.log('登录失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('登录出错:', error.message);
        return false;
    }
}

// 执行测试
(async () => {
    const registerResult = await testRegister();
    if (registerResult) {
        await testLogin();
    }
})();
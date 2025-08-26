describe('管理员登录和注册测试', () => {
  it('管理员注册并登录测试', () => {
    // 访问注册页面
    cy.visit('http://localhost:8080/admin-register');
    cy.wait(1000); // 等待页面加载

    // 填写注册信息
    cy.get('input[id="username"]').should('be.visible').type('adminTest');
    cy.get('input[id="password"]').should('be.visible').type('admin123456');
    cy.wait(500); // 新增短暂等待，确保输入完成

    // 拦截注册请求
    cy.intercept('POST', 'http://localhost:8080/admin/register').as('registerRequest');
    cy.intercept('POST', 'http://localhost:8080/admin/login').as('loginRequest');

    // 点击注册按钮
    cy.get('form button[type="submit"]').should('be.enabled').click();

    // 等待注册请求完成并验证状态码
    cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);
    cy.wait(5000); // 增加等待时间，等待页面跳转

    // 验证注册成功，假设注册成功后跳转到登录页
    cy.url().should('include', '/admin-login');
    cy.wait(1000); // 等待登录页面加载

    // 填写登录信息
    cy.get('input[id="username"]').clear().should('be.visible').type('adminTest');
    cy.get('input[id="password"]').clear().should('be.visible').type('admin123456');
    cy.wait(500); // 新增短暂等待，确保输入完成

    // 点击登录按钮
    cy.get('form button[type="submit"]').should('be.enabled').click();

    // 等待登录请求完成并验证状态码
    cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);
    cy.wait(5000); // 增加等待时间，等待页面跳转

    // 可根据实际情况修改登录成功后的跳转页面验证逻辑
    // 此处暂时注释掉，待确认跳转页面后再修改
    // cy.url().should('include', '/admin');
  });
});
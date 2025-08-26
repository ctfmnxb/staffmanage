describe('注册和登录测试', () => {
  it('注册并登录测试', () => {
    // 访问注册页面
    cy.visit('http://localhost:8080/user-register');
    cy.wait(1000); // 等待页面加载

    // 填写注册信息
    cy.get('input[id="username"]').type('James');
    cy.get('input[id="password"]').type('123456');
    cy.wait(500); // 新增短暂等待，确保输入完成

    // 拦截注册请求
    cy.intercept('POST', 'http://localhost:8081/register').as('registerRequest');
    cy.intercept('POST', 'http://localhost:8081/login').as('loginRequest');

    // 点击注册按钮
    cy.get('button[type="submit"]').click();

    // 等待注册请求完成并验证状态码
    cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);
    cy.wait(5000); // 增加等待时间，等待页面跳转

    // 验证注册成功，这里假设注册成功后跳转到登录页
    cy.url().should('include', '/user-login');
    cy.wait(1000); // 等待登录页面加载

    // 填写登录信息
    cy.get('input[id="username"]').clear().type('James');
    cy.get('input[id="password"]').clear().type('123456');
    cy.wait(500); // 新增短暂等待，确保输入完成

    // 点击登录按钮
    cy.get('button[type="submit"]').click();

    // 等待登录请求完成并验证状态码
    cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);
    cy.wait(5000); // 增加等待时间，等待页面跳转

    // 验证登录成功，这里假设登录成功后跳转到 /dashboard 页面
    cy.url().should('include', '/dashboard');
  });
});
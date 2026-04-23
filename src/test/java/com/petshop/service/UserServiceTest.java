package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.User;
import com.petshop.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("plainPassword");
        testUser.setPhone("13800138000");
        testUser.setAvatar("https://example.com/avatar.jpg");
        testUser.setStatus("active");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("用户创建 - 正常创建")
    void testRegister_Success() {
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(testUser);

        assertNotNull(result);
        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户创建 - 密码加密验证")
    void testRegister_PasswordEncryption() {
        String originalPassword = testUser.getPassword();
        String encodedPassword = "encodedPassword456";
        when(passwordEncoder.encode(originalPassword)).thenReturn(encodedPassword);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(testUser);

        assertNotEquals(originalPassword, result.getPassword());
        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder, times(1)).encode(originalPassword);
    }

    @Test
    @DisplayName("用户查询 - 根据ID查询成功")
    void testFindById_Success() {
        when(userMapper.selectById(1)).thenReturn(testUser);

        User result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("用户查询 - 根据ID查询不存在")
    void testFindById_NotFound() {
        when(userMapper.selectById(999)).thenReturn(null);

        User result = userService.findById(999);

        assertNull(result);
        verify(userMapper, times(1)).selectById(999);
    }

    @Test
    @DisplayName("用户查询 - 根据邮箱查询成功")
    void testFindByEmail_Success() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        User result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户查询 - 根据邮箱查询不存在")
    void testFindByEmail_NotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        User result = userService.findByEmail("nonexistent@example.com");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户查询 - 查询所有用户成功")
    void testFindAll_Success() {
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> userList = Arrays.asList(testUser, user2);
        when(userMapper.selectList(null)).thenReturn(userList);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userMapper, times(1)).selectList(null);
    }

    @Test
    @DisplayName("用户查询 - 查询所有用户为空")
    void testFindAll_EmptyList() {
        when(userMapper.selectList(null)).thenReturn(Arrays.asList());

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userMapper, times(1)).selectList(null);
    }

    @Test
    @DisplayName("用户查询 - 分页查询成功")
    void testFindAll_Pageable() {
        Page<User> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testUser));
        mockPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户更新 - 正常更新")
    void testUpdate_Success() {
        testUser.setUsername("updatedUser");
        testUser.setEmail("updated@example.com");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.update(testUser);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("用户更新 - 更新部分字段")
    void testUpdate_PartialUpdate() {
        User updateUser = new User();
        updateUser.setId(1);
        updateUser.setPhone("13900139000");

        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.update(updateUser);

        assertNotNull(result);
        assertEquals("13900139000", result.getPhone());
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("用户删除 - 正常删除")
    void testDelete_Success() {
        when(userMapper.deleteById(1)).thenReturn(1);

        userService.delete(1);

        verify(userMapper, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("用户删除 - 删除不存在的用户")
    void testDelete_NotFound() {
        when(userMapper.deleteById(999)).thenReturn(0);

        userService.delete(999);

        verify(userMapper, times(1)).deleteById(999);
    }

    @Test
    @DisplayName("用户登录 - 邮箱登录成功")
    void testLogin_EmailSuccess() {
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.login("test@example.com", rawPassword);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("用户登录 - 用户名登录成功")
    void testLogin_UsernameSuccess() {
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null)
                .thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.login("testuser", rawPassword);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("用户登录 - 邮箱不存在")
    void testLogin_EmailNotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        User result = userService.login("nonexistent@example.com", "password");

        assertNull(result);
        verify(userMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        User result = userService.login("test@example.com", rawPassword);

        assertNull(result);
        verify(passwordEncoder, times(2)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void testLogin_UserNotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        User result = userService.login("nonexistent", "password");

        assertNull(result);
        verify(userMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户登录 - 空密码")
    void testLogin_EmptyPassword() {
        testUser.setPassword("encodedPassword123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches("", "encodedPassword123")).thenReturn(false);

        User result = userService.login("test@example.com", "");

        assertNull(result);
        verify(passwordEncoder, times(2)).matches("", "encodedPassword123");
    }

    @Test
    @DisplayName("用户登录 - 用户名和邮箱都匹配但密码错误")
    void testLogin_BothMatchButWrongPassword() {
        String wrongPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testUser)
                .thenReturn(testUser);
        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

        User result = userService.login("testuser", wrongPassword);

        assertNull(result);
        verify(passwordEncoder, times(2)).matches(wrongPassword, encodedPassword);
    }

    @Test
    @DisplayName("用户创建 - 多次注册不同用户")
    void testRegister_MultipleUsers() {
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded1", "encoded2");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result1 = userService.register(testUser);
        User result2 = userService.register(user2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("encoded1", result1.getPassword());
        assertEquals("encoded2", result2.getPassword());
        verify(userMapper, times(2)).insert(any(User.class));
        verify(passwordEncoder, times(2)).encode(anyString());
    }

    @Test
    @DisplayName("用户查询 - 根据ID查询多次")
    void testFindById_MultipleCalls() {
        when(userMapper.selectById(1)).thenReturn(testUser);

        User result1 = userService.findById(1);
        User result2 = userService.findById(1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
        verify(userMapper, times(2)).selectById(1);
    }

    @Test
    @DisplayName("用户更新 - 更新后验证字段")
    void testUpdate_VerifyFields() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername("newUsername");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPhone("13700137000");
        updatedUser.setAvatar("https://example.com/newavatar.jpg");
        updatedUser.setStatus("inactive");

        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.update(updatedUser);

        assertEquals("newUsername", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("13700137000", result.getPhone());
        assertEquals("https://example.com/newavatar.jpg", result.getAvatar());
        assertEquals("inactive", result.getStatus());
    }

    @Test
    @DisplayName("用户登录 - 邮箱大小写敏感")
    void testLogin_EmailCaseSensitive() {
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        User result = userService.login("TEST@EXAMPLE.COM", "plainPassword");

        assertNull(result);
        verify(userMapper, times(2)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户创建 - 空用户名")
    void testRegister_EmptyUsername() {
        testUser.setUsername("");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(testUser);

        assertNotNull(result);
        assertEquals("", result.getUsername());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户创建 - 空邮箱")
    void testRegister_EmptyEmail() {
        testUser.setEmail("");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(testUser);

        assertNotNull(result);
        assertEquals("", result.getEmail());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户删除 - 删除后再次查询")
    void testDelete_ThenFindById() {
        when(userMapper.deleteById(1)).thenReturn(1);
        when(userMapper.selectById(1)).thenReturn(null);

        userService.delete(1);
        User result = userService.findById(1);

        assertNull(result);
        verify(userMapper, times(1)).deleteById(1);
        verify(userMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("用户查询 - 分页查询第二页")
    void testFindAll_SecondPage() {
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");

        Page<User> mockPage = new Page<>(2, 5);
        mockPage.setRecords(Arrays.asList(user2));
        mockPage.setTotal(11);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(1, 5);
        Page<User> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(11, result.getTotal());
        assertEquals(2, result.getCurrent());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户登录 - 用户状态为inactive")
    void testLogin_InactiveUser() {
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword123";
        testUser.setPassword(encodedPassword);
        testUser.setStatus("inactive");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.login("test@example.com", rawPassword);

        assertNotNull(result);
        assertEquals("inactive", result.getStatus());
    }

    @Test
    @DisplayName("用户更新 - 更新状态")
    void testUpdate_StatusChange() {
        testUser.setStatus("inactive");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.update(testUser);

        assertEquals("inactive", result.getStatus());
        verify(userMapper, times(1)).updateById(any(User.class));
    }
}

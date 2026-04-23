package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.MerchantDetailDTO;
import com.petshop.entity.Merchant;
import com.petshop.mapper.MerchantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MerchantService 单元测试")
class MerchantServiceTest {

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MerchantService merchantService;

    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setId(1);
        testMerchant.setName("测试商家");
        testMerchant.setContactPerson("张三");
        testMerchant.setPhone("13800138000");
        testMerchant.setEmail("test@example.com");
        testMerchant.setPassword("rawPassword");
        testMerchant.setAddress("测试地址");
        testMerchant.setLogo("logo.png");
        testMerchant.setStatus("pending");
        testMerchant.setCreatedAt(LocalDateTime.now());
        testMerchant.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("商家注册功能测试")
    class RegisterTests {

        @Test
        @DisplayName("正常注册 - 密码应被加密")
        void testRegister_Success() {
            String encodedPassword = "encodedPassword123";
            when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.register(testMerchant);

            assertNotNull(result);
            assertEquals(encodedPassword, result.getPassword());
            assertEquals("pending", result.getStatus());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
            verify(passwordEncoder).encode("rawPassword");
            verify(merchantMapper).insert(any(Merchant.class));
        }

        @Test
        @DisplayName("注册 - 密码加密调用验证")
        void testRegister_PasswordEncoding() {
            String rawPassword = "myPassword123";
            String encodedPassword = "$2a$10$encodedPasswordHash";
            testMerchant.setPassword(rawPassword);
            
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            merchantService.register(testMerchant);

            ArgumentCaptor<Merchant> merchantCaptor = ArgumentCaptor.forClass(Merchant.class);
            verify(merchantMapper).insert(merchantCaptor.capture());
            
            Merchant capturedMerchant = merchantCaptor.getValue();
            assertEquals(encodedPassword, capturedMerchant.getPassword());
            verify(passwordEncoder, times(1)).encode(rawPassword);
        }

        @Test
        @DisplayName("注册 - 默认状态应为 pending")
        void testRegister_DefaultStatusIsPending() {
            Merchant merchantWithStatus = new Merchant();
            merchantWithStatus.setName("新商家");
            merchantWithStatus.setEmail("new@example.com");
            merchantWithStatus.setPassword("password");
            merchantWithStatus.setStatus("approved");
            
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.register(merchantWithStatus);

            assertEquals("pending", result.getStatus());
        }
    }

    @Nested
    @DisplayName("商家审核功能测试")
    class AuditMerchantTests {

        @Test
        @DisplayName("审核通过 - 状态应变为 approved")
        void testAuditMerchant_Approved() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.auditMerchant(1, "approved", null);

            assertEquals("approved", result.getStatus());
            assertNotNull(result.getUpdatedAt());
            verify(merchantMapper).updateById(testMerchant);
        }

        @Test
        @DisplayName("审核拒绝 - 状态应变为 rejected")
        void testAuditMerchant_Rejected() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.auditMerchant(1, "rejected", "资质不符");

            assertEquals("rejected", result.getStatus());
            assertNotNull(result.getUpdatedAt());
            verify(merchantMapper).updateById(testMerchant);
        }

        @Test
        @DisplayName("商家不存在 - 应抛出异常")
        void testAuditMerchant_MerchantNotFound() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> merchantService.auditMerchant(999, "approved", null)
            );

            assertTrue(exception.getMessage().contains("Merchant not found"));
            verify(merchantMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("无效状态 - 应抛出异常")
        void testAuditMerchant_InvalidStatus() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> merchantService.auditMerchant(1, "invalid_status", null)
            );

            assertTrue(exception.getMessage().contains("Invalid status"));
            verify(merchantMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("无效状态 - pending 不被接受")
        void testAuditMerchant_PendingStatusInvalid() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);

            assertThrows(
                IllegalArgumentException.class,
                () -> merchantService.auditMerchant(1, "pending", null)
            );
        }
    }

    @Nested
    @DisplayName("商家查询功能测试")
    class QueryTests {

        @Test
        @DisplayName("根据 ID 查询 - 存在")
        void testFindById_Exists() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);

            Merchant result = merchantService.findById(1);

            assertNotNull(result);
            assertEquals("测试商家", result.getName());
            verify(merchantMapper).selectById(1);
        }

        @Test
        @DisplayName("根据 ID 查询 - 不存在")
        void testFindById_NotExists() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            Merchant result = merchantService.findById(999);

            assertNull(result);
        }

        @Test
        @DisplayName("根据邮箱查询 - 存在")
        void testFindByEmail_Exists() {
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMerchant);

            Merchant result = merchantService.findByEmail("test@example.com");

            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            verify(merchantMapper).selectOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据邮箱查询 - 不存在")
        void testFindByEmail_NotExists() {
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            Merchant result = merchantService.findByEmail("nonexistent@example.com");

            assertNull(result);
        }

        @Test
        @DisplayName("查询所有商家")
        void testFindAll() {
            Merchant merchant2 = new Merchant();
            merchant2.setId(2);
            merchant2.setName("商家2");
            
            when(merchantMapper.selectList(null)).thenReturn(Arrays.asList(testMerchant, merchant2));

            List<Merchant> result = merchantService.findAll();

            assertEquals(2, result.size());
            verify(merchantMapper).selectList(null);
        }

        @Test
        @DisplayName("查询所有商家 - 空列表")
        void testFindAll_Empty() {
            when(merchantMapper.selectList(null)).thenReturn(Collections.emptyList());

            List<Merchant> result = merchantService.findAll();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("搜索商家 - 按名称")
        void testSearchMerchants_ByName() {
            when(merchantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(testMerchant));

            List<Merchant> result = merchantService.searchMerchants("测试", null, (Double) null, 0, 10);

            assertFalse(result.isEmpty());
            verify(merchantMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("搜索商家 - 按关键字")
        void testSearchMerchants_ByKeyword() {
            when(merchantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(testMerchant));

            List<Merchant> result = merchantService.searchMerchants("测试", "name", "asc", 0, 10);

            assertNotNull(result);
            verify(merchantMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取商家详情 - 存在")
        void testGetMerchantDetail_Exists() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);

            MerchantDetailDTO result = merchantService.getMerchantDetail(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("测试商家", result.getName());
            assertEquals("张三", result.getContactPerson());
            assertEquals("13800138000", result.getPhone());
            assertEquals("test@example.com", result.getEmail());
        }

        @Test
        @DisplayName("获取商家详情 - 不存在")
        void testGetMerchantDetail_NotExists() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            MerchantDetailDTO result = merchantService.getMerchantDetail(999);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("商家更新功能测试")
    class UpdateTests {

        @Test
        @DisplayName("正常更新")
        void testUpdate_Success() {
            testMerchant.setName("更新后的商家名");
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.update(testMerchant);

            assertEquals("更新后的商家名", result.getName());
            assertNotNull(result.getUpdatedAt());
            verify(merchantMapper).updateById(testMerchant);
        }

        @Test
        @DisplayName("更新 - updatedAt 应被设置")
        void testUpdate_UpdatedAtSet() {
            testMerchant.setUpdatedAt(null);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            Merchant result = merchantService.update(testMerchant);

            assertNotNull(result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("批量操作功能测试")
    class BatchOperationTests {

        @Test
        @DisplayName("批量更新状态 - 全部成功")
        void testBatchUpdateStatus_AllSuccess() {
            Merchant merchant2 = new Merchant();
            merchant2.setId(2);
            merchant2.setStatus("pending");
            
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectById(2)).thenReturn(merchant2);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            int count = merchantService.batchUpdateStatus(Arrays.asList(1, 2), "approved");

            assertEquals(2, count);
            verify(merchantMapper, times(2)).updateById(any(Merchant.class));
        }

        @Test
        @DisplayName("批量更新状态 - 部分存在")
        void testBatchUpdateStatus_PartialExists() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectById(999)).thenReturn(null);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            int count = merchantService.batchUpdateStatus(Arrays.asList(1, 999), "approved");

            assertEquals(1, count);
            verify(merchantMapper, times(1)).updateById(any(Merchant.class));
        }

        @Test
        @DisplayName("批量更新状态 - 空列表")
        void testBatchUpdateStatus_EmptyList() {
            int count = merchantService.batchUpdateStatus(Collections.emptyList(), "approved");

            assertEquals(0, count);
            verify(merchantMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("批量删除 - 全部成功")
        void testBatchDelete_AllSuccess() {
            Merchant merchant2 = new Merchant();
            merchant2.setId(2);
            
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectById(2)).thenReturn(merchant2);
            when(merchantMapper.deleteById(anyInt())).thenReturn(1);

            int count = merchantService.batchDelete(Arrays.asList(1, 2));

            assertEquals(2, count);
            verify(merchantMapper, times(2)).deleteById(anyInt());
        }

        @Test
        @DisplayName("批量删除 - 部分存在")
        void testBatchDelete_PartialExists() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectById(999)).thenReturn(null);
            when(merchantMapper.deleteById(1)).thenReturn(1);

            int count = merchantService.batchDelete(Arrays.asList(1, 999));

            assertEquals(1, count);
            verify(merchantMapper, times(1)).deleteById(anyInt());
        }

        @Test
        @DisplayName("批量删除 - 空列表")
        void testBatchDelete_EmptyList() {
            int count = merchantService.batchDelete(Collections.emptyList());

            assertEquals(0, count);
            verify(merchantMapper, never()).deleteById(anyInt());
        }
    }

    @Nested
    @DisplayName("密码修改功能测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("正常修改密码")
        void testChangePassword_Success() {
            testMerchant.setPassword("oldEncodedPassword");
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(passwordEncoder.matches("oldPassword", "oldEncodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            assertDoesNotThrow(() -> 
                merchantService.changePassword(1, "oldPassword", "newPassword123")
            );

            verify(passwordEncoder).matches("oldPassword", "oldEncodedPassword");
            verify(passwordEncoder).encode("newPassword123");
            verify(merchantMapper).updateById(any(Merchant.class));
        }

        @Test
        @DisplayName("原密码错误 - 应抛出异常")
        void testChangePassword_WrongOldPassword() {
            testMerchant.setPassword("oldEncodedPassword");
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(passwordEncoder.matches("wrongPassword", "oldEncodedPassword")).thenReturn(false);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.changePassword(1, "wrongPassword", "newPassword123")
            );

            assertEquals("原密码错误", exception.getMessage());
            verify(merchantMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("新密码长度不足 - 应抛出异常")
        void testChangePassword_NewPasswordTooShort() {
            testMerchant.setPassword("oldEncodedPassword");
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(passwordEncoder.matches("oldPassword", "oldEncodedPassword")).thenReturn(true);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.changePassword(1, "oldPassword", "12345")
            );

            assertEquals("新密码长度至少 6 位", exception.getMessage());
            verify(merchantMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("商家不存在 - 应抛出异常")
        void testChangePassword_MerchantNotFound() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.changePassword(999, "oldPassword", "newPassword123")
            );

            assertEquals("商家不存在", exception.getMessage());
        }

        @Test
        @DisplayName("新密码正好 6 位 - 应成功")
        void testChangePassword_ExactlySixCharacters() {
            testMerchant.setPassword("oldEncodedPassword");
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(passwordEncoder.matches("oldPassword", "oldEncodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("123456")).thenReturn("encoded123456");
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            assertDoesNotThrow(() -> 
                merchantService.changePassword(1, "oldPassword", "123456")
            );
        }
    }

    @Nested
    @DisplayName("登录功能测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功")
        void testLogin_Success() {
            testMerchant.setStatus("approved");
            testMerchant.setPassword("encodedPassword");
            
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMerchant);
            when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

            Merchant result = merchantService.login("test@example.com", "rawPassword");

            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void testLogin_WrongPassword() {
            testMerchant.setStatus("approved");
            testMerchant.setPassword("encodedPassword");
            
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMerchant);
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            Merchant result = merchantService.login("test@example.com", "wrongPassword");

            assertNull(result);
        }

        @Test
        @DisplayName("登录失败 - 商家未审核")
        void testLogin_NotApproved() {
            testMerchant.setStatus("pending");
            testMerchant.setPassword("encodedPassword");
            
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMerchant);
            when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

            Merchant result = merchantService.login("test@example.com", "rawPassword");

            assertNull(result);
        }

        @Test
        @DisplayName("登录失败 - 商家不存在")
        void testLogin_MerchantNotFound() {
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            Merchant result = merchantService.login("nonexistent@example.com", "password");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("删除功能测试")
    class DeleteTests {

        @Test
        @DisplayName("删除商家")
        void testDelete_Success() {
            when(merchantMapper.deleteById(1)).thenReturn(1);

            assertDoesNotThrow(() -> merchantService.delete(1));

            verify(merchantMapper).deleteById(1);
        }
    }

    @Nested
    @DisplayName("更新联系方式功能测试")
    class UpdateContactTests {

        @Test
        @DisplayName("更新电话 - 成功")
        void testUpdatePhone_Success() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            assertDoesNotThrow(() -> merchantService.updatePhone(1, "13900139000"));

            verify(merchantMapper).updateById(any(Merchant.class));
        }

        @Test
        @DisplayName("更新电话 - 商家不存在")
        void testUpdatePhone_MerchantNotFound() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.updatePhone(999, "13900139000")
            );

            assertEquals("商家不存在", exception.getMessage());
        }

        @Test
        @DisplayName("更新邮箱 - 成功")
        void testUpdateEmail_Success() {
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

            assertDoesNotThrow(() -> merchantService.updateEmail(1, "newemail@example.com"));

            verify(merchantMapper).updateById(any(Merchant.class));
        }

        @Test
        @DisplayName("更新邮箱 - 邮箱已被使用")
        void testUpdateEmail_EmailAlreadyUsed() {
            Merchant otherMerchant = new Merchant();
            otherMerchant.setId(2);
            otherMerchant.setEmail("used@example.com");
            
            when(merchantMapper.selectById(1)).thenReturn(testMerchant);
            when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(otherMerchant);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.updateEmail(1, "used@example.com")
            );

            assertEquals("邮箱已被使用", exception.getMessage());
        }

        @Test
        @DisplayName("更新邮箱 - 商家不存在")
        void testUpdateEmail_MerchantNotFound() {
            when(merchantMapper.selectById(999)).thenReturn(null);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> merchantService.updateEmail(999, "newemail@example.com")
            );

            assertEquals("商家不存在", exception.getMessage());
        }
    }
}

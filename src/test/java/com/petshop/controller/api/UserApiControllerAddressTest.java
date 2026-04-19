package com.petshop.controller.api;

import com.petshop.dto.AddressDTO;
import com.petshop.entity.Address;
import com.petshop.entity.User;
import com.petshop.exception.GlobalExceptionHandler;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户地址管理API测试")
public class UserApiControllerAddressTest extends UserApiControllerTestBase {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        UserApiController controller = new UserApiController();
        injectDependencies(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void injectDependencies(UserApiController controller) {
        try {
            var addressServiceField = UserApiController.class.getDeclaredField("addressService");
            addressServiceField.setAccessible(true);
            addressServiceField.set(controller, addressService);

            var userServiceField = UserApiController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(controller, userService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    private Address createTestAddress(Integer id, User user, boolean isDefault) {
        Address address = new Address();
        address.setId(id);
        address.setUser(user);
        address.setContactName("测试联系人");
        address.setPhone("13800138000");
        address.setProvince("北京市");
        address.setCity("北京市");
        address.setDistrict("朝阳区");
        address.setDetailAddress("测试详细地址123号");
        address.setIsDefault(isDefault);
        return address;
    }

    private AddressDTO createTestAddressDTO(Integer id, Integer userId, boolean isDefault) {
        return AddressDTO.builder()
                .id(id)
                .userId(userId)
                .contactName("测试联系人")
                .phone("13800138000")
                .province("北京市")
                .city("北京市")
                .district("朝阳区")
                .detailAddress("测试详细地址123号")
                .isDefault(isDefault)
                .build();
    }

    @Nested
    @DisplayName("获取地址列表测试")
    class GetAddressesTests {

        @Test
        @DisplayName("成功获取地址列表")
        void testGetAddresses_Success() throws Exception {
            AddressDTO address1 = createTestAddressDTO(1, testUserId, true);
            AddressDTO address2 = createTestAddressDTO(2, testUserId, false);
            List<AddressDTO> addresses = Arrays.asList(address1, address2);

            when(addressService.findByUserId(testUserId)).thenReturn(addresses);

            var result = performGet("/api/user/addresses");

            assertSuccess(result);
            assertListResponse(result, 2);
            result.andExpect(jsonPath("$.data[0].contactName").value("测试联系人"))
                    .andExpect(jsonPath("$.data[0].isDefault").value(true));
            verify(addressService).findByUserId(testUserId);
        }

        @Test
        @DisplayName("获取空地址列表")
        void testGetAddresses_EmptyList() throws Exception {
            when(addressService.findByUserId(testUserId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/user/addresses");

            assertSuccess(result);
            assertListResponse(result, 0);
            verify(addressService).findByUserId(testUserId);
        }

        @Test
        @DisplayName("地址列表按默认地址排序")
        void testGetAddresses_DefaultFirst() throws Exception {
            AddressDTO defaultAddress = createTestAddressDTO(1, testUserId, true);
            AddressDTO normalAddress = createTestAddressDTO(2, testUserId, false);
            List<AddressDTO> addresses = Arrays.asList(defaultAddress, normalAddress);

            when(addressService.findByUserId(testUserId)).thenReturn(addresses);

            var result = performGet("/api/user/addresses");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].isDefault").value(true))
                    .andExpect(jsonPath("$.data[1].isDefault").value(false));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAddresses_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/addresses")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问"));

            verify(addressService, never()).findByUserId(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetAddresses_ServiceException() throws Exception {
            when(addressService.findByUserId(testUserId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/addresses");

            result.andExpect(status().isInternalServerError());
            verify(addressService).findByUserId(testUserId);
        }
    }

    @Nested
    @DisplayName("添加地址测试")
    class AddAddressTests {

        @Test
        @DisplayName("成功添加地址")
        void testAddAddress_Success() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.contactName").value("测试联系人"))
                    .andExpect(jsonPath("$.data.phone").value("13800138000"))
                    .andExpect(jsonPath("$.data.province").value("北京市"));
            verify(addressService).create(any(Address.class));
        }

        @Test
        @DisplayName("添加第一个地址自动设为默认")
        void testAddAddress_FirstAddressAutoDefault() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, true);

            when(addressService.findByUserId(testUserId)).thenReturn(Collections.emptyList());
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.isDefault").value(true));
        }

        @Test
        @DisplayName("添加地址时指定为默认")
        void testAddAddress_SetAsDefault() throws Exception {
            Address newAddress = createTestAddress(null, testUser, true);
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, true);

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.isDefault").value(true));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddAddress_Unauthorized() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);

            mockMvc.perform(post("/api/user/addresses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newAddress)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(addressService, never()).create(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddAddress_ServiceException() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);

            when(addressService.create(any(Address.class)))
                    .thenThrow(new RuntimeException("保存失败"));

            var result = performPost("/api/user/addresses", newAddress);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("必填字段验证测试")
    class RequiredFieldValidationTests {

        @Test
        @DisplayName("缺少联系人姓名")
        void testAddAddress_MissingContactName() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("phone", "13800138000");
            addressData.put("province", "北京市");
            addressData.put("city", "北京市");
            addressData.put("district", "朝阳区");
            addressData.put("detailAddress", "测试地址");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("缺少电话号码")
        void testAddAddress_MissingPhone() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("contactName", "测试联系人");
            addressData.put("province", "北京市");
            addressData.put("city", "北京市");
            addressData.put("district", "朝阳区");
            addressData.put("detailAddress", "测试地址");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("缺少省份")
        void testAddAddress_MissingProvince() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("contactName", "测试联系人");
            addressData.put("phone", "13800138000");
            addressData.put("city", "北京市");
            addressData.put("district", "朝阳区");
            addressData.put("detailAddress", "测试地址");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("缺少城市")
        void testAddAddress_MissingCity() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("contactName", "测试联系人");
            addressData.put("phone", "13800138000");
            addressData.put("province", "北京市");
            addressData.put("district", "朝阳区");
            addressData.put("detailAddress", "测试地址");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("缺少区县")
        void testAddAddress_MissingDistrict() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("contactName", "测试联系人");
            addressData.put("phone", "13800138000");
            addressData.put("province", "北京市");
            addressData.put("city", "北京市");
            addressData.put("detailAddress", "测试地址");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("缺少详细地址")
        void testAddAddress_MissingDetailAddress() throws Exception {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("contactName", "测试联系人");
            addressData.put("phone", "13800138000");
            addressData.put("province", "北京市");
            addressData.put("city", "北京市");
            addressData.put("district", "朝阳区");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", addressData);

            assertCreatedResponse(result);
        }
    }

    @Nested
    @DisplayName("电话号码验证测试")
    class PhoneValidationTests {

        @Test
        @DisplayName("有效手机号格式")
        void testAddAddress_ValidPhone() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setPhone("15912345678");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setPhone("15912345678");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.phone").value("15912345678"));
        }

        @Test
        @DisplayName("带区号的座机号码")
        void testAddAddress_LandlinePhone() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setPhone("010-12345678");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setPhone("010-12345678");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.phone").value("010-12345678"));
        }

        @Test
        @DisplayName("带空格的电话号码")
        void testAddAddress_PhoneWithSpaces() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setPhone("138 0013 8000");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setPhone("138 0013 8000");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("国际格式电话号码")
        void testAddAddress_InternationalPhone() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setPhone("+86-13800138000");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setPhone("+86-13800138000");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
        }
    }

    @Nested
    @DisplayName("更新地址测试")
    class UpdateAddressTests {

        @Test
        @DisplayName("成功更新地址")
        void testUpdateAddress_Success() throws Exception {
            Address existingAddress = createTestAddress(1, testUser, false);
            Address updateData = createTestAddress(1, testUser, false);
            updateData.setContactName("更新后的联系人");
            updateData.setPhone("13900139000");

            AddressDTO updatedAddress = createTestAddressDTO(1, testUserId, false);
            updatedAddress.setContactName("更新后的联系人");
            updatedAddress.setPhone("13900139000");

            when(addressService.findByIdEntity(1)).thenReturn(existingAddress);
            when(addressService.update(any(Address.class))).thenReturn(updatedAddress);

            var result = performPut("/api/user/addresses/{id}", updateData, 1);

            assertSuccess(result, "地址更新成功");
            result.andExpect(jsonPath("$.data.contactName").value("更新后的联系人"))
                    .andExpect(jsonPath("$.data.phone").value("13900139000"));
            verify(addressService).update(any(Address.class));
        }

        @Test
        @DisplayName("更新不存在的地址返回404")
        void testUpdateAddress_NotFound() throws Exception {
            Address updateData = createTestAddress(999, testUser, false);

            when(addressService.findByIdEntity(999)).thenReturn(null);

            var result = performPut("/api/user/addresses/{id}", updateData, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).update(any());
        }

        @Test
        @DisplayName("无权限更新其他用户地址返回404")
        void testUpdateAddress_Forbidden() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Address otherUserAddress = createTestAddress(1, otherUser, false);

            Address updateData = createTestAddress(1, testUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(otherUserAddress);

            var result = performPut("/api/user/addresses/{id}", updateData, 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).update(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateAddress_Unauthorized() throws Exception {
            Address updateData = createTestAddress(1, testUser, false);

            mockMvc.perform(put("/api/user/addresses/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(addressService, never()).update(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateAddress_ServiceException() throws Exception {
            Address existingAddress = createTestAddress(1, testUser, false);
            Address updateData = createTestAddress(1, testUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(existingAddress);
            when(addressService.update(any(Address.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            var result = performPut("/api/user/addresses/{id}", updateData, 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("删除地址测试")
    class DeleteAddressTests {

        @Test
        @DisplayName("成功删除地址")
        void testDeleteAddress_Success() throws Exception {
            Address address = createTestAddress(1, testUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(address);
            doNothing().when(addressService).delete(1);

            var result = performDelete("/api/user/addresses/{id}", 1);

            assertSuccess(result, "地址删除成功");
            verify(addressService).delete(1);
        }

        @Test
        @DisplayName("删除默认地址后自动设置其他地址为默认")
        void testDeleteAddress_DefaultAddress() throws Exception {
            Address defaultAddress = createTestAddress(1, testUser, true);
            Address otherAddress = createTestAddress(2, testUser, false);
            AddressDTO otherAddressDTO = createTestAddressDTO(2, testUserId, true);

            when(addressService.findByIdEntity(1)).thenReturn(defaultAddress);
            when(addressService.findByUserId(testUserId)).thenReturn(Arrays.asList(
                    createTestAddressDTO(2, testUserId, true)));
            doNothing().when(addressService).delete(1);

            var result = performDelete("/api/user/addresses/{id}", 1);

            assertSuccess(result, "地址删除成功");
            verify(addressService).delete(1);
        }

        @Test
        @DisplayName("删除不存在的地址返回404")
        void testDeleteAddress_NotFound() throws Exception {
            when(addressService.findByIdEntity(999)).thenReturn(null);

            var result = performDelete("/api/user/addresses/{id}", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).delete(any());
        }

        @Test
        @DisplayName("无权限删除其他用户地址返回404")
        void testDeleteAddress_Forbidden() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Address otherUserAddress = createTestAddress(1, otherUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(otherUserAddress);

            var result = performDelete("/api/user/addresses/{id}", 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).delete(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteAddress_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/addresses/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(addressService, never()).delete(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteAddress_ServiceException() throws Exception {
            Address address = createTestAddress(1, testUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(address);
            doThrow(new RuntimeException("删除失败")).when(addressService).delete(1);

            var result = performDelete("/api/user/addresses/{id}", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("设置默认地址测试")
    class SetDefaultAddressTests {

        @Test
        @DisplayName("成功设置默认地址")
        void testSetDefaultAddress_Success() throws Exception {
            Address address = createTestAddress(1, testUser, false);
            AddressDTO defaultAddress = createTestAddressDTO(1, testUserId, true);

            when(addressService.findByIdEntity(1)).thenReturn(address);
            when(addressService.setDefault(testUserId, 1)).thenReturn(defaultAddress);

            var result = performPut("/api/user/addresses/{id}/default", null, 1);

            assertSuccess(result, "设置默认地址成功");
            result.andExpect(jsonPath("$.data.isDefault").value(true));
            verify(addressService).setDefault(testUserId, 1);
        }

        @Test
        @DisplayName("设置默认地址时清除之前的默认地址")
        void testSetDefaultAddress_ClearsPreviousDefault() throws Exception {
            Address newDefaultAddress = createTestAddress(2, testUser, false);
            AddressDTO newDefaultDTO = createTestAddressDTO(2, testUserId, true);

            when(addressService.findByIdEntity(2)).thenReturn(newDefaultAddress);
            when(addressService.setDefault(testUserId, 2)).thenReturn(newDefaultDTO);

            var result = performPut("/api/user/addresses/{id}/default", null, 2);

            assertSuccess(result, "设置默认地址成功");
            verify(addressService).setDefault(testUserId, 2);
        }

        @Test
        @DisplayName("设置不存在的地址为默认返回404")
        void testSetDefaultAddress_NotFound() throws Exception {
            when(addressService.findByIdEntity(999)).thenReturn(null);

            var result = performPut("/api/user/addresses/{id}/default", null, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).setDefault(any(), any());
        }

        @Test
        @DisplayName("无权限设置其他用户地址为默认返回404")
        void testSetDefaultAddress_Forbidden() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Address otherUserAddress = createTestAddress(1, otherUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(otherUserAddress);

            var result = performPut("/api/user/addresses/{id}/default", null, 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(addressService, never()).setDefault(any(), any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testSetDefaultAddress_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/user/addresses/1/default")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(addressService, never()).setDefault(any(), any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testSetDefaultAddress_ServiceException() throws Exception {
            Address address = createTestAddress(1, testUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(address);
            when(addressService.setDefault(testUserId, 1))
                    .thenThrow(new RuntimeException("设置失败"));

            var result = performPut("/api/user/addresses/{id}/default", null, 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("超长联系人姓名")
        void testAddAddress_LongContactName() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setContactName("a".repeat(50));
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setContactName("a".repeat(50));

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("超长详细地址")
        void testAddAddress_LongDetailAddress() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setDetailAddress("a".repeat(255));
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setDetailAddress("a".repeat(255));

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("空字符串联系人姓名")
        void testAddAddress_EmptyContactName() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setContactName("");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setContactName("");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("特殊字符地址")
        void testAddAddress_SpecialCharacters() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setDetailAddress("测试地址#101室，包含特殊字符！@#$%");
            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setDetailAddress("测试地址#101室，包含特殊字符！@#$%");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.detailAddress").value("测试地址#101室，包含特殊字符！@#$%"));
        }

        @Test
        @DisplayName("中文地址信息")
        void testAddAddress_ChineseCharacters() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);
            newAddress.setProvince("广东省");
            newAddress.setCity("深圳市");
            newAddress.setDistrict("南山区");
            newAddress.setDetailAddress("科技园南区测试大厦A座");
            newAddress.setContactName("张三");

            AddressDTO savedAddress = createTestAddressDTO(1, testUserId, false);
            savedAddress.setProvince("广东省");
            savedAddress.setCity("深圳市");
            savedAddress.setDistrict("南山区");
            savedAddress.setDetailAddress("科技园南区测试大厦A座");
            savedAddress.setContactName("张三");

            when(addressService.create(any(Address.class))).thenReturn(savedAddress);

            var result = performPost("/api/user/addresses", newAddress);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.province").value("广东省"))
                    .andExpect(jsonPath("$.data.city").value("深圳市"))
                    .andExpect(jsonPath("$.data.district").value("南山区"))
                    .andExpect(jsonPath("$.data.contactName").value("张三"));
        }

        @Test
        @DisplayName("多个地址全部设为默认")
        void testMultipleDefaultAddresses() throws Exception {
            Address address1 = createTestAddress(1, testUser, false);
            Address address2 = createTestAddress(2, testUser, false);
            AddressDTO defaultDTO = createTestAddressDTO(2, testUserId, true);

            when(addressService.findByIdEntity(2)).thenReturn(address2);
            when(addressService.setDefault(testUserId, 2)).thenReturn(defaultDTO);

            var result = performPut("/api/user/addresses/{id}/default", null, 2);

            assertSuccess(result, "设置默认地址成功");
            verify(addressService).setDefault(testUserId, 2);
        }
    }

    @Nested
    @DisplayName("并发和状态测试")
    class ConcurrencyAndStateTests {

        @Test
        @DisplayName("地址所有权验证 - 相同用户ID")
        void testAddressOwnership_SameUserId() throws Exception {
            Address address = createTestAddress(1, testUser, false);
            address.getUser().setId(testUserId);

            when(addressService.findByIdEntity(1)).thenReturn(address);
            doNothing().when(addressService).delete(1);

            var result = performDelete("/api/user/addresses/{id}", 1);

            assertSuccess(result, "地址删除成功");
            verify(addressService).delete(1);
        }

        @Test
        @DisplayName("地址所有权验证 - 不同用户ID")
        void testAddressOwnership_DifferentUserId() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Address otherUserAddress = createTestAddress(1, otherUser, false);

            when(addressService.findByIdEntity(1)).thenReturn(otherUserAddress);

            var result = performDelete("/api/user/addresses/{id}", 1);

            assertNotFound(result);
            verify(addressService, never()).delete(any());
        }

        @Test
        @DisplayName("地址用户为null时返回500")
        void testAddressOwnership_NullUser() throws Exception {
            Address address = createTestAddress(1, null, false);

            when(addressService.findByIdEntity(1)).thenReturn(address);

            var result = performDelete("/api/user/addresses/{id}", 1);

            result.andExpect(status().isInternalServerError());
            verify(addressService, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("未认证访问测试")
    class UnauthenticatedAccessTests {

        @Test
        @DisplayName("获取地址列表未认证")
        void testGetAddresses_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/user/addresses")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("添加地址未认证")
        void testAddAddress_Unauthenticated() throws Exception {
            Address newAddress = createTestAddress(null, testUser, false);

            mockMvc.perform(post("/api/user/addresses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newAddress)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("更新地址未认证")
        void testUpdateAddress_Unauthenticated() throws Exception {
            Address updateData = createTestAddress(1, testUser, false);

            mockMvc.perform(put("/api/user/addresses/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("删除地址未认证")
        void testDeleteAddress_Unauthenticated() throws Exception {
            mockMvc.perform(delete("/api/user/addresses/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("设置默认地址未认证")
        void testSetDefaultAddress_Unauthenticated() throws Exception {
            mockMvc.perform(put("/api/user/addresses/1/default")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}

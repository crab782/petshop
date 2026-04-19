package com.petshop.controller.api;

import com.petshop.entity.Pet;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户宠物API测试")
public class UserApiControllerPetTest extends UserApiControllerTestBase {

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
            var petServiceField = UserApiController.class.getDeclaredField("petService");
            petServiceField.setAccessible(true);
            petServiceField.set(controller, petService);

            var userServiceField = UserApiController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(controller, userService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    @Nested
    @DisplayName("获取宠物列表测试")
    class GetPetsTests {

        @Test
        @DisplayName("成功获取宠物列表")
        void testGetPets_Success() throws Exception {
            Pet pet1 = TestDataFactory.createPet(1, testUser);
            Pet pet2 = TestDataFactory.createPet(2, testUser);
            List<Pet> pets = Arrays.asList(pet1, pet2);

            when(petService.findByUserId(testUserId)).thenReturn(pets);

            var result = performGet("/api/user/pets");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value(pet1.getName()))
                    .andExpect(jsonPath("$[1].id").value(2));
            verify(petService).findByUserId(testUserId);
        }

        @Test
        @DisplayName("获取空宠物列表")
        void testGetPets_EmptyList() throws Exception {
            when(petService.findByUserId(testUserId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/user/pets");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(petService).findByUserId(testUserId);
        }

        @Test
        @DisplayName("获取单个宠物的列表")
        void testGetPets_SinglePet() throws Exception {
            Pet pet = TestDataFactory.createPet(1, testUser);
            pet.setName("小白");
            pet.setType("猫");
            pet.setBreed("英短");

            when(petService.findByUserId(testUserId)).thenReturn(Collections.singletonList(pet));

            var result = performGet("/api/user/pets");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("小白"))
                    .andExpect(jsonPath("$[0].type").value("猫"))
                    .andExpect(jsonPath("$[0].breed").value("英短"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPets_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/pets")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(petService, never()).findByUserId(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetPets_ServiceException() throws Exception {
            when(petService.findByUserId(testUserId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/pets");

            result.andExpect(status().isInternalServerError());
            verify(petService).findByUserId(testUserId);
        }
    }

    @Nested
    @DisplayName("添加宠物测试")
    class AddPetTests {

        @Test
        @DisplayName("成功添加宠物")
        void testAddPet_Success() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("小黄");
            newPet.setType("狗");
            newPet.setBreed("柯基");
            newPet.setAge(2);
            newPet.setGender("male");

            Pet savedPet = TestDataFactory.createPet(1, testUser);
            savedPet.setName("小黄");
            savedPet.setType("狗");
            savedPet.setBreed("柯基");
            savedPet.setAge(2);
            savedPet.setGender("male");

            when(petService.create(any(Pet.class))).thenReturn(savedPet);

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("小黄"))
                    .andExpect(jsonPath("$.type").value("狗"))
                    .andExpect(jsonPath("$.breed").value("柯基"))
                    .andExpect(jsonPath("$.age").value(2))
                    .andExpect(jsonPath("$.gender").value("male"));
            verify(petService).create(any(Pet.class));
        }

        @Test
        @DisplayName("添加宠物时自动关联用户")
        void testAddPet_AutoAssociateUser() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("小花");
            newPet.setType("猫");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("小花"));
            verify(petService).create(any(Pet.class));
        }

        @Test
        @DisplayName("添加宠物包含所有字段")
        void testAddPet_AllFields() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("豆豆");
            newPet.setType("狗");
            newPet.setBreed("泰迪");
            newPet.setAge(5);
            newPet.setGender("female");
            newPet.setAvatar("http://example.com/avatar.jpg");
            newPet.setDescription("一只可爱的泰迪");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("豆豆"))
                    .andExpect(jsonPath("$.type").value("狗"))
                    .andExpect(jsonPath("$.breed").value("泰迪"))
                    .andExpect(jsonPath("$.age").value(5))
                    .andExpect(jsonPath("$.gender").value("female"))
                    .andExpect(jsonPath("$.avatar").value("http://example.com/avatar.jpg"))
                    .andExpect(jsonPath("$.description").value("一只可爱的泰迪"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddPet_Unauthorized() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("小黑");
            newPet.setType("狗");

            mockMvc.perform(post("/api/user/pets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newPet)))
                    .andExpect(status().isUnauthorized());

            verify(petService, never()).create(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddPet_ServiceException() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("小黑");
            newPet.setType("狗");

            when(petService.create(any(Pet.class)))
                    .thenThrow(new RuntimeException("保存失败"));

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("更新宠物测试")
    class UpdatePetTests {

        @Test
        @DisplayName("成功更新宠物")
        void testUpdatePet_Success() throws Exception {
            Pet existingPet = TestDataFactory.createPet(1, testUser);

            Pet updateData = new Pet();
            updateData.setName("更新后的名字");
            updateData.setType("猫");
            updateData.setBreed("布偶");
            updateData.setAge(3);
            updateData.setGender("female");

            Pet updatedPet = TestDataFactory.createPet(1, testUser);
            updatedPet.setName("更新后的名字");
            updatedPet.setType("猫");
            updatedPet.setBreed("布偶");
            updatedPet.setAge(3);
            updatedPet.setGender("female");

            when(petService.findById(1)).thenReturn(existingPet);
            when(petService.update(any(Pet.class))).thenReturn(updatedPet);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("更新后的名字"))
                    .andExpect(jsonPath("$.type").value("猫"))
                    .andExpect(jsonPath("$.breed").value("布偶"))
                    .andExpect(jsonPath("$.age").value(3))
                    .andExpect(jsonPath("$.gender").value("female"));
            verify(petService).update(any(Pet.class));
        }

        @Test
        @DisplayName("更新宠物部分字段")
        void testUpdatePet_PartialUpdate() throws Exception {
            Pet existingPet = TestDataFactory.createPet(1, testUser);
            existingPet.setName("原名字");
            existingPet.setType("狗");
            existingPet.setBreed("金毛");

            Pet updateData = new Pet();
            updateData.setName("新名字");
            updateData.setType("狗");
            updateData.setAge(4);

            Pet updatedPet = TestDataFactory.createPet(1, testUser);
            updatedPet.setName("新名字");
            updatedPet.setAge(4);

            when(petService.findById(1)).thenReturn(existingPet);
            when(petService.update(any(Pet.class))).thenReturn(updatedPet);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("新名字"));
            verify(petService).update(any(Pet.class));
        }

        @Test
        @DisplayName("更新不存在的宠物返回404")
        void testUpdatePet_NotFound() throws Exception {
            Pet updateData = new Pet();
            updateData.setName("更新后的名字");
            updateData.setType("狗");

            when(petService.findById(999)).thenReturn(null);

            var result = performPut("/api/user/pets/{id}", updateData, 999);

            result.andExpect(status().isNotFound());
            verify(petService, never()).update(any());
        }

        @Test
        @DisplayName("无权限更新其他用户的宠物返回404")
        void testUpdatePet_Forbidden() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Pet otherPet = TestDataFactory.createPet(1, otherUser);

            Pet updateData = new Pet();
            updateData.setName("更新后的名字");
            updateData.setType("狗");

            when(petService.findById(1)).thenReturn(otherPet);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isNotFound());
            verify(petService, never()).update(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdatePet_Unauthorized() throws Exception {
            Pet updateData = new Pet();
            updateData.setName("更新后的名字");

            mockMvc.perform(put("/api/user/pets/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isUnauthorized());

            verify(petService, never()).update(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdatePet_ServiceException() throws Exception {
            Pet existingPet = TestDataFactory.createPet(1, testUser);

            Pet updateData = new Pet();
            updateData.setName("更新后的名字");
            updateData.setType("狗");

            when(petService.findById(1)).thenReturn(existingPet);
            when(petService.update(any(Pet.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("删除宠物测试")
    class DeletePetTests {

        @Test
        @DisplayName("成功删除宠物")
        void testDeletePet_Success() throws Exception {
            Pet pet = TestDataFactory.createPet(1, testUser);

            when(petService.findById(1)).thenReturn(pet);
            doNothing().when(petService).delete(1);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isNoContent());
            verify(petService).delete(1);
        }

        @Test
        @DisplayName("删除不存在的宠物返回404")
        void testDeletePet_NotFound() throws Exception {
            when(petService.findById(999)).thenReturn(null);

            var result = performDelete("/api/user/pets/{id}", 999);

            result.andExpect(status().isNotFound());
            verify(petService, never()).delete(any());
        }

        @Test
        @DisplayName("无权限删除其他用户的宠物返回404")
        void testDeletePet_Forbidden() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Pet otherPet = TestDataFactory.createPet(1, otherUser);

            when(petService.findById(1)).thenReturn(otherPet);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isNotFound());
            verify(petService, never()).delete(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeletePet_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/pets/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(petService, never()).delete(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeletePet_ServiceException() throws Exception {
            Pet pet = TestDataFactory.createPet(1, testUser);

            when(petService.findById(1)).thenReturn(pet);
            doThrow(new RuntimeException("删除失败")).when(petService).delete(1);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("所有权验证测试")
    class OwnershipValidationTests {

        @Test
        @DisplayName("更新宠物时验证所有权 - 不同用户ID")
        void testUpdatePet_OwnershipValidation_DifferentUserId() throws Exception {
            User otherUser = TestDataFactory.createUser(999, "其他用户", "other@test.com");
            Pet otherPet = TestDataFactory.createPet(1, otherUser);

            Pet updateData = new Pet();
            updateData.setName("尝试修改");

            when(petService.findById(1)).thenReturn(otherPet);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isNotFound());
            verify(petService, never()).update(any());
        }

        @Test
        @DisplayName("删除宠物时验证所有权 - 不同用户ID")
        void testDeletePet_OwnershipValidation_DifferentUserId() throws Exception {
            User otherUser = TestDataFactory.createUser(999, "其他用户", "other@test.com");
            Pet otherPet = TestDataFactory.createPet(1, otherUser);

            when(petService.findById(1)).thenReturn(otherPet);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isNotFound());
            verify(petService, never()).delete(any());
        }

        @Test
        @DisplayName("更新宠物时验证所有权 - 同一用户")
        void testUpdatePet_OwnershipValidation_SameUser() throws Exception {
            Pet existingPet = TestDataFactory.createPet(1, testUser);

            Pet updateData = new Pet();
            updateData.setName("合法更新");
            updateData.setType("狗");

            Pet updatedPet = TestDataFactory.createPet(1, testUser);
            updatedPet.setName("合法更新");

            when(petService.findById(1)).thenReturn(existingPet);
            when(petService.update(any(Pet.class))).thenReturn(updatedPet);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("合法更新"));
            verify(petService).update(any(Pet.class));
        }

        @Test
        @DisplayName("删除宠物时验证所有权 - 同一用户")
        void testDeletePet_OwnershipValidation_SameUser() throws Exception {
            Pet pet = TestDataFactory.createPet(1, testUser);

            when(petService.findById(1)).thenReturn(pet);
            doNothing().when(petService).delete(1);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isNoContent());
            verify(petService).delete(1);
        }

        @Test
        @DisplayName("多个宠物时只能操作自己的宠物")
        void testMultiplePets_OwnershipValidation() throws Exception {
            Pet myPet = TestDataFactory.createPet(1, testUser);
            User otherUser = TestDataFactory.createUser(2);
            Pet otherPet = TestDataFactory.createPet(2, otherUser);

            when(petService.findById(1)).thenReturn(myPet);
            when(petService.findById(2)).thenReturn(otherPet);
            doNothing().when(petService).delete(any());

            var resultMyPet = performDelete("/api/user/pets/{id}", 1);
            resultMyPet.andExpect(status().isNoContent());

            var resultOtherPet = performDelete("/api/user/pets/{id}", 2);
            resultOtherPet.andExpect(status().isNotFound());

            verify(petService).delete(1);
            verify(petService, never()).delete(2);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("宠物名称为空")
        void testAddPet_EmptyName() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("");
            newPet.setType("狗");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("宠物名称为null")
        void testAddPet_NullName() throws Exception {
            Pet newPet = new Pet();
            newPet.setName(null);
            newPet.setType("狗");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("宠物年龄为0")
        void testAddPet_ZeroAge() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("幼宠");
            newPet.setType("猫");
            newPet.setAge(0);

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.age").value(0));
        }

        @Test
        @DisplayName("宠物年龄为负数")
        void testAddPet_NegativeAge() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("测试宠物");
            newPet.setType("狗");
            newPet.setAge(-1);

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("宠物年龄为最大值")
        void testAddPet_MaxAge() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("老宠物");
            newPet.setType("龟");
            newPet.setAge(Integer.MAX_VALUE);

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.age").value(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("宠物类型为超长字符串")
        void testAddPet_LongType() throws Exception {
            Pet newPet = new Pet();
            String longType = "a".repeat(100);
            newPet.setName("测试宠物");
            newPet.setType(longType);

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("宠物描述为超长文本")
        void testAddPet_LongDescription() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("测试宠物");
            newPet.setType("狗");
            newPet.setDescription("a".repeat(1000));

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("宠物性别为male")
        void testAddPet_MaleGender() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("公宠");
            newPet.setType("狗");
            newPet.setGender("male");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.gender").value("male"));
        }

        @Test
        @DisplayName("宠物性别为female")
        void testAddPet_FemaleGender() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("母宠");
            newPet.setType("猫");
            newPet.setGender("female");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.gender").value("female"));
        }

        @Test
        @DisplayName("宠物头像URL为空")
        void testAddPet_NullAvatar() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("测试宠物");
            newPet.setType("狗");
            newPet.setAvatar(null);

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(1);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("更新宠物ID为null时")
        void testUpdatePet_NullId() throws Exception {
            Pet updateData = new Pet();
            updateData.setName("更新");

            var result = performPut("/api/user/pets/{id}", updateData, "null");

            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("并发和状态测试")
    class ConcurrencyAndStateTests {

        @Test
        @DisplayName("更新已删除的宠物返回404")
        void testUpdatePet_AfterDeleted() throws Exception {
            Pet updateData = new Pet();
            updateData.setName("更新");
            updateData.setType("狗");

            when(petService.findById(1)).thenReturn(null);

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("删除已删除的宠物返回404")
        void testDeletePet_AfterDeleted() throws Exception {
            when(petService.findById(1)).thenReturn(null);

            var result = performDelete("/api/user/pets/{id}", 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("同一宠物不能被其他用户更新")
        void testSamePet_DifferentUsers() throws Exception {
            User user1 = TestDataFactory.createUser(1);
            User user2 = TestDataFactory.createUser(2);
            Pet pet = TestDataFactory.createPet(1, user1);

            Pet updateData = new Pet();
            updateData.setName("用户2尝试修改");

            when(petService.findById(1)).thenReturn(pet);

            mockUserSession(user2);
            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isNotFound());
            verify(petService, never()).update(any());
        }
    }

    @Nested
    @DisplayName("数据完整性测试")
    class DataIntegrityTests {

        @Test
        @DisplayName("添加宠物后返回完整数据")
        void testAddPet_ReturnCompleteData() throws Exception {
            Pet newPet = new Pet();
            newPet.setName("完整数据测试");
            newPet.setType("狗");
            newPet.setBreed("拉布拉多");
            newPet.setAge(3);
            newPet.setGender("male");
            newPet.setAvatar("http://example.com/pet.jpg");
            newPet.setDescription("这是一只拉布拉多");

            when(petService.create(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(100);
                return pet;
            });

            var result = performPost("/api/user/pets", newPet);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.name").value("完整数据测试"))
                    .andExpect(jsonPath("$.type").value("狗"))
                    .andExpect(jsonPath("$.breed").value("拉布拉多"))
                    .andExpect(jsonPath("$.age").value(3))
                    .andExpect(jsonPath("$.gender").value("male"))
                    .andExpect(jsonPath("$.avatar").value("http://example.com/pet.jpg"))
                    .andExpect(jsonPath("$.description").value("这是一只拉布拉多"));
        }

        @Test
        @DisplayName("更新宠物后返回更新后的数据")
        void testUpdatePet_ReturnUpdatedData() throws Exception {
            Pet existingPet = TestDataFactory.createPet(1, testUser);
            existingPet.setName("旧名字");
            existingPet.setAge(2);

            Pet updateData = new Pet();
            updateData.setName("新名字");
            updateData.setType("猫");
            updateData.setBreed("英短");
            updateData.setAge(4);
            updateData.setGender("female");

            when(petService.findById(1)).thenReturn(existingPet);
            when(petService.update(any(Pet.class))).thenAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                return pet;
            });

            var result = performPut("/api/user/pets/{id}", updateData, 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("新名字"))
                    .andExpect(jsonPath("$.type").value("猫"))
                    .andExpect(jsonPath("$.breed").value("英短"))
                    .andExpect(jsonPath("$.age").value(4))
                    .andExpect(jsonPath("$.gender").value("female"));
        }

        @Test
        @DisplayName("获取宠物列表包含所有必要字段")
        void testGetPets_ContainsAllFields() throws Exception {
            Pet pet = TestDataFactory.createPet(1, testUser);
            pet.setName("测试宠物");
            pet.setType("狗");
            pet.setBreed("金毛");
            pet.setAge(5);
            pet.setGender("male");
            pet.setAvatar("http://example.com/avatar.jpg");

            when(petService.findByUserId(testUserId)).thenReturn(Collections.singletonList(pet));

            var result = performGet("/api/user/pets");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("测试宠物"))
                    .andExpect(jsonPath("$[0].type").value("狗"))
                    .andExpect(jsonPath("$[0].breed").value("金毛"))
                    .andExpect(jsonPath("$[0].age").value(5))
                    .andExpect(jsonPath("$[0].gender").value("male"))
                    .andExpect(jsonPath("$[0].avatar").value("http://example.com/avatar.jpg"));
        }
    }
}
